package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.phase.TranslucentFeatureRenderPhase;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
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
                    target = "Lnet/minecraft/client/renderer/feature/phase/TranslucentFeatureRenderPhase;submit(Lnet/minecraft/client/renderer/feature/submit/TranslucentSubmit;)V"
            )
    )
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void wrapModelSubmit(TranslucentFeatureRenderPhase phase, TranslucentSubmit submit, Operation<Void> original) {
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

        // 26.3: Submit carries uvMapping instead of a sprite; the RenderType was
        // already swapped to entityTranslucent by ShieldEntityModelMixin for local
        // shields, so keep it and only patch the tinted color alpha.
        RenderType translucentType = modelSubmit.renderType();

        var modified = new ModelFeatureRenderer.Submit(
                translucentType, modelSubmit.pose(), modelSubmit.model(), modelSubmit.state(),
                modelSubmit.lightCoords(), modelSubmit.overlayCoords(), modifiedColor,
                modelSubmit.uvMapping(), modelSubmit.sheetedDecalPose()
        );

        original.call(phase, (TranslucentSubmit) modified);
    }
}
