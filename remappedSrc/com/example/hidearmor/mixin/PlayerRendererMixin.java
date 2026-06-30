package com.example.hidearmor.mixin;

import com.example.hidearmor.LocalPlayerTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;

@Mixin(LivingEntityRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At("HEAD"))
    private void onPreRender(LivingEntityRenderState state, PoseStack matrices,
            SubmitNodeCollector queue, CameraRenderState cameraState, CallbackInfo ci) {
        if (state instanceof AvatarRenderState playerState) {
            var client = Minecraft.getInstance();
            if (client.player != null) {
                LocalPlayerTracker.setLocalPlayer(client.player.getId(), client.player.getUUID());
            }
            // Resolve UUID from the entity in the world
            UUID uuid = null;
            if (client.level != null) {
                Entity entity = client.level.getEntity(playerState.id);
                if (entity instanceof AbstractClientPlayer player) {
                    uuid = player.getUUID();
                }
            }
            LocalPlayerTracker.beginRender(playerState.id, uuid);
        }
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At("RETURN"))
    private void onPostRender(LivingEntityRenderState state, PoseStack matrices,
            SubmitNodeCollector queue, CameraRenderState cameraState, CallbackInfo ci) {
        if (state instanceof AvatarRenderState) {
            LocalPlayerTracker.endRender();
        }
    }
}
