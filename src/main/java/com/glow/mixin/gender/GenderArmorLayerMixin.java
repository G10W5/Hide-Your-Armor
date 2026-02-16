package com.glow.mixin.gender;

import com.glow.HideArmor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.wildfire.render.GenderArmorLayer")
public abstract class GenderArmorLayerMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                         int light, BipedEntityRenderState state, float limbAngle, float limbDistance,
                         CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && HideArmor.isHidden) {
            ci.cancel();
        }
    }
}