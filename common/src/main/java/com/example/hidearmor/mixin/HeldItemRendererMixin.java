package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 26.3: ItemInHandRenderer was replaced by FirstPersonHandsAndItemsRenderer.
// This renderer only ever draws the local player's own hands, so any shield
// seen here is the local shield.
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V", at = @At("HEAD"), cancellable = true)
    private void onRenderItem(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState renderState,
            float armHeight, float bob, InteractionHand hand, float swing, ItemStack stack, float equipProgress,
            PoseStack matrices, SubmitNodeCollector vertexConsumers, int light, CallbackInfo ci) {
        if (stack.getItem() instanceof ShieldItem) {
            float opacity = HideArmorMod.getShieldOpacity();
            if (opacity <= 0.0f) {
                ci.cancel();
            } else {
                HideArmorMod.isRenderingLocalShield = true;
                HideArmorMod.isFirstPersonShield = true;
            }
        }
    }

    @Inject(method = "submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V", at = @At("TAIL"))
    private void onRenderItemReturn(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState renderState,
            float armHeight, float bob, InteractionHand hand, float swing, ItemStack stack, float equipProgress,
            PoseStack matrices, SubmitNodeCollector vertexConsumers, int light, CallbackInfo ci) {
        if (stack.getItem() instanceof ShieldItem) {
            HideArmorMod.isRenderingLocalShield = false;
            HideArmorMod.isFirstPersonShield = false;
        }
    }
}
