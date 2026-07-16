package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.example.hidearmor.LocalPlayerTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SkullBlockRenderer.class)
public class SkullBlockRendererMixin {

    @WrapOperation(
        method = "submitSkull(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/model/object/skull/SkullModelBase;Lnet/minecraft/client/renderer/rendertype/RenderType;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V")
    )
    private <S> void wrapSkullSubmitModel(
            OrderedSubmitNodeCollector queue, Model<? super S> model, S state, PoseStack poseStack, RenderType renderType,
            int light, int overlay, int outlineColor, ModelFeatureRenderer.CrumblingOverlay crumbling,
            Operation<Void> original) {
        if (LocalPlayerTracker.isRenderingLocalPlayer()) {
            float opacity = HideArmorMod.getSkullsAndBlocksOpacity();
            if (opacity < 1.0f) {
                int alpha = (int) (opacity * 255.0f);
                int tintedColor = ARGB.color(alpha, 255, 255, 255);
                queue.submitModel(model, state, poseStack, renderType, light, overlay, tintedColor, null, outlineColor, crumbling);
                return;
            }
        }
        original.call(queue, model, state, poseStack, renderType, light, overlay, outlineColor, crumbling);
    }
}
