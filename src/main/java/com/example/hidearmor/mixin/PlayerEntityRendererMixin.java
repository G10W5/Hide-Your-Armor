package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private void onRenderArmor(MatrixStack matrices, OrderedRenderCommandQueue vertexConsumers, ItemStack stack,
            EquipmentSlot slot, int light, BipedEntityRenderState state, CallbackInfo ci) {
        boolean hidden = false;
        if (slot == EquipmentSlot.HEAD)
            hidden = HideArmorMod.isHelmetHidden();
        else if (slot == EquipmentSlot.CHEST)
            hidden = HideArmorMod.isChestplateHidden();
        else if (slot == EquipmentSlot.LEGS)
            hidden = HideArmorMod.isLeggingsHidden();
        else if (slot == EquipmentSlot.FEET)
            hidden = HideArmorMod.isBootsHidden();

        if (hidden) {
            ci.cancel();
        }
    }
}
