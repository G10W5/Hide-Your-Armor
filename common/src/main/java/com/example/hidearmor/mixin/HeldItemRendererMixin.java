package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V", at = @At("HEAD"), cancellable = true)
    private void onRenderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext context, PoseStack matrices,
            SubmitNodeCollector vertexConsumers, int light, CallbackInfo ci) {
        if (stack.getItem() instanceof ShieldItem
                && entity == net.minecraft.client.Minecraft.getInstance().player) {
            float opacity = HideArmorMod.getShieldOpacity();
            if (opacity <= 0.0f) {
                ci.cancel();
            } else {
                HideArmorMod.isRenderingLocalShield = true;
                HideArmorMod.isFirstPersonShield = (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                        || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
            }
        }
    }

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V", at = @At("RETURN"))
    private void onRenderItemReturn(LivingEntity entity, ItemStack stack, ItemDisplayContext context,
            PoseStack matrices,
            SubmitNodeCollector vertexConsumers, int light, CallbackInfo ci) {
        if (stack.getItem() instanceof ShieldItem
                && entity == net.minecraft.client.Minecraft.getInstance().player) {
            HideArmorMod.isRenderingLocalShield = false;
            HideArmorMod.isFirstPersonShield = false;
        }
    }
}
