package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.example.hidearmor.LocalPlayerTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class CapeRendererMixin {

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    private void onRenderCape(PoseStack matrices, SubmitNodeCollector queue, int light,
            AvatarRenderState state, float limbAngle, float limbDistance, CallbackInfo ci) {
        if (LocalPlayerTracker.isRenderingLocalPlayer()) {
            float opacity = HideArmorMod.getCapeOpacity();
            if (opacity <= 0.0f) {
                ci.cancel();
            }
        }
    }

    @WrapOperation(
        method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;entitySolid(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;")
    )
    private RenderType wrapCapeRenderType(Identifier texture, Operation<RenderType> original) {
        if (LocalPlayerTracker.isRenderingLocalPlayer()) {
            float opacity = HideArmorMod.getCapeOpacity();
            if (opacity < 1.0f) {
                return RenderTypes.entityTranslucent(texture);
            }
        }
        return original.call(texture);
    }

    @WrapOperation(
        method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIIILnet/minecraft/client/model/ModelFeatureRenderer$CrumblingOverlay;)V")
    )
    private <S> void wrapCapeSubmitModel(
            OrderedSubmitNodeCollector queue, Model<? super S> model, S state, PoseStack poseStack, RenderType renderType,
            int light, int overlay, int outlineColor, ModelFeatureRenderer.CrumblingOverlay crumbling,
            Operation<Void> original) {
        if (LocalPlayerTracker.isRenderingLocalPlayer()) {
            float opacity = HideArmorMod.getCapeOpacity();
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
