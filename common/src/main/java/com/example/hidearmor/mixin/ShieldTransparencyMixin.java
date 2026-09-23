package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.object.equipment.ShieldModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(net.minecraft.client.renderer.SubmitNodeCollection.class)
public class ShieldTransparencyMixin {

    @ModifyExpressionValue(
            method = "submitModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/rendertype/RenderType;hasBlending()Z"
            )
    )
    private boolean forceTranslucentRoute(boolean original) {
        if (original) {
            return true;
        }
        if (HideArmorMod.isRenderingLocalShield) {
            float opacity = HideArmorMod.getShieldOpacity();
            if (opacity > 0.0f && opacity < 1.0f) {
                return true;
            }
        }
        return false;
    }

    @WrapOperation(
            method = "submitModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/feature/phase/FeatureRenderPhase;submit(Lnet/minecraft/client/renderer/feature/submit/SubmitNode;)V"
            )
    )
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void wrapModelSubmit(FeatureRenderPhase phase, SubmitNode submit, Operation<Void> original) {
        if (!(submit instanceof ModelFeatureRenderer.Submit<?> modelSubmit)) {
            original.call(phase, submit);
            return;
        }
        if (!HideArmorMod.isRenderingLocalShield) {
            original.call(phase, submit);
            return;
        }
        float opacity = HideArmorMod.getShieldOpacity();
        if (opacity <= 0.0f || opacity >= 1.0f) {
            original.call(phase, submit);
            return;
        }

        int alpha = (int) (opacity * 255.0f);
        int modifiedColor = (modelSubmit.tintedColor() & 0x00FFFFFF) | (alpha << 24);

        // 26.3: shield submits use sprite-backed cutout types and never call
        // Model.renderType(Identifier), so ShieldEntityModelMixin can't swap them.
        // Swap non-glint shield submits to a blending type here instead, using the
        // shield atlas (same pattern vanilla uses for entitySolidGlint). Without a
        // blending RenderType the patched alpha would be ignored (pop-in).
        RenderType translucentType = modelSubmit.renderType();
        if (modelSubmit.model() instanceof ShieldModel
                && !translucentType.toString().contains("glint")) {
            translucentType = RenderTypes.entityTranslucent(Sheets.SHIELD_BASE.atlasLocation());
        }

        var modified = new ModelFeatureRenderer.Submit(
                translucentType, modelSubmit.pose(), modelSubmit.model(), modelSubmit.state(),
                modelSubmit.lightCoords(), modelSubmit.overlayCoords(), modifiedColor,
                modelSubmit.uvMapping(), modelSubmit.sheetedDecalPose()
        );

        original.call(phase, (SubmitNode) modified);
    }
}
