package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.example.hidearmor.LocalPlayerTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomHeadLayer.class)
public class HeadFeatureRendererMixin {

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    private void onRenderHead(PoseStack matrices, SubmitNodeCollector queue, int light,
            LivingEntityRenderState state, float limbAngle, float limbDistance, CallbackInfo ci) {
        if (LocalPlayerTracker.isRenderingLocalPlayer()) {
            float opacity = HideArmorMod.getSkullsAndBlocksOpacity();
            if (opacity <= 0.0f) {
                ci.cancel();
            }
        }
    }

    @WrapOperation(
        method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;submitSkull(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/model/object/skull/SkullModelBase;Lnet/minecraft/client/renderer/rendertype/RenderType;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V")
    )
    private void wrapSkullRender(
            float animationValue, PoseStack poseStack, SubmitNodeCollector queue, int light,
            SkullModelBase model, RenderType renderType, int outlineColor,
            ModelFeatureRenderer.CrumblingOverlay crumbling, Operation<Void> original) {
        if (LocalPlayerTracker.isRenderingLocalPlayer()) {
            float opacity = HideArmorMod.getSkullsAndBlocksOpacity();
            if (opacity < 1.0f) {
                int alpha = (int) (opacity * 255.0f);
                int tintedColor = ARGB.color(alpha, 255, 255, 255);
                SkullModelBase.State modelState = new SkullModelBase.State();
                modelState.animationPos = animationValue;
                queue.submitModel(model, modelState, poseStack, renderType, light,
                        OverlayTexture.NO_OVERLAY, tintedColor, null, outlineColor, crumbling);
                return;
            }
        }
        original.call(animationValue, poseStack, queue, light, model, renderType, outlineColor, crumbling);
    }
}
