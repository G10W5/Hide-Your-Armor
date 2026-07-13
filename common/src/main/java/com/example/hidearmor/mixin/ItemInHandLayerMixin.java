package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {

    @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
    private void onShieldRender(ArmedEntityRenderState renderState, ItemStackRenderState itemState,
            ItemStack itemStack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int light,
            CallbackInfo ci) {
        if (itemStack.getItem() instanceof ShieldItem) {
            float opacity = HideArmorMod.getShieldOpacity();
            if (opacity <= 0.0f) {
                ci.cancel();
            } else if (opacity < 1.0f) {
                HideArmorMod.isRenderingLocalShield = true;
            }
        }
    }

    @Inject(method = "submitArmWithItem", at = @At("TAIL"))
    private void onShieldRenderEnd(ArmedEntityRenderState renderState, ItemStackRenderState itemState,
            ItemStack itemStack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int light,
            CallbackInfo ci) {
        if (itemStack.getItem() instanceof ShieldItem) {
            HideArmorMod.isRenderingLocalShield = false;
        }
    }
}
