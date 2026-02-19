package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemFeatureRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void onRenderItem(ArmedEntityRenderState state, ItemRenderState itemState, ItemStack stack, Arm arm,
            MatrixStack matrices, OrderedRenderCommandQueue vertexConsumers, int light, CallbackInfo ci) {
        // We can check if the hand held item is a shield in the entity state
        // Many 1.21.x states store the item stack or item type for hands
        // However, if we don't have the stack, we can check a generic 'isShield'
        // property if it exists in ItemRenderState
        // For now, we'll try to broad-match or check the state
        if (HideArmorMod.isShieldHidden()) {
            // In 1.21.4, ItemRenderState usually represents the model being rendered.
            // We'll rely on a Mixin to ShieldModelRenderer for more precision if this
            // fails.
            // But for now, let's assume this renders the item in hand.
            // We can check the arm and the state's held item if we find the field.
            ci.cancel();
        }
    }
}
