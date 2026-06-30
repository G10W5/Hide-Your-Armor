package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V", at = @At("HEAD"), cancellable = true)
    private void onRenderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext context, MatrixStack matrices,
            OrderedRenderCommandQueue vertexConsumers, int light, CallbackInfo ci) {
        if (stack.getItem() instanceof ShieldItem
                && entity == net.minecraft.client.MinecraftClient.getInstance().player) {
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

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V", at = @At("RETURN"))
    private void onRenderItemReturn(LivingEntity entity, ItemStack stack, ItemDisplayContext context,
            MatrixStack matrices,
            OrderedRenderCommandQueue vertexConsumers, int light, CallbackInfo ci) {
        if (stack.getItem() instanceof ShieldItem
                && entity == net.minecraft.client.MinecraftClient.getInstance().player) {
            HideArmorMod.isRenderingLocalShield = false;
            HideArmorMod.isFirstPersonShield = false;
        }
    }
}
