package com.example.hidearmor.mixin;

import com.example.hidearmor.LocalPlayerTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into LivingEntityRenderer.render (where the method is actually
 * declared,
 * not PlayerEntityRenderer which only inherits it). Checks if the render state
 * is a PlayerEntityRenderState matching the local player to set
 * LocalPlayerTracker.
 */
@Mixin(LivingEntityRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("HEAD"))
    private void onPreRender(LivingEntityRenderState state, MatrixStack matrices,
            OrderedRenderCommandQueue queue, CameraRenderState cameraState, CallbackInfo ci) {
        if (state instanceof PlayerEntityRenderState playerState) {
            var client = MinecraftClient.getInstance();
            if (client.player != null) {
                LocalPlayerTracker.setLocalPlayerId(client.player.getId());
            }
            LocalPlayerTracker.beginRender(playerState.id);
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("RETURN"))
    private void onPostRender(LivingEntityRenderState state, MatrixStack matrices,
            OrderedRenderCommandQueue queue, CameraRenderState cameraState, CallbackInfo ci) {
        if (state instanceof PlayerEntityRenderState) {
            LocalPlayerTracker.endRender();
        }
    }
}
