package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShieldSpecialRenderer.class)
public class ShieldModelRendererMixin {

    @WrapOperation(
        method = "submit(Lnet/minecraft/core/component/DataComponentMap;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;IIILnet/minecraft/client/resources/model/sprite/SpriteId;Lnet/minecraft/client/resources/model/sprite/SpriteGetter;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
        )
    )
    private <S> void wrapBaseModel(
            SubmitNodeCollector collector, Model<S> model, S data, PoseStack poseStack,
            int light, int overlay, int color, SpriteId spriteId, SpriteGetter spriteGetter,
            int unknown, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay,
            Operation<SubmitNodeCollector> original) {
        if (HideArmorMod.isRenderingLocalShield) {
            float opacity = HideArmorMod.getShieldOpacity();
            if (opacity > 0.0f && opacity < 1.0f) {
                int alpha = (int) (opacity * 255.0f);
                color = (color & 0x00FFFFFF) | (alpha << 24);
            }
        }
        original.call(collector, model, data, poseStack, light, overlay, color, spriteId, spriteGetter, unknown, crumblingOverlay);
    }
}
