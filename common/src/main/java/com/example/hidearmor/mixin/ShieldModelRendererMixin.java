package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.core.component.DataComponentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Scopes the shield transparency patches in ShieldTransparencyMixin to the
 * submission of the local player's own shield.
 */
@Mixin(ShieldSpecialRenderer.class)
public class ShieldModelRendererMixin {

    private static final String SUBMIT = "submit(Lnet/minecraft/core/component/DataComponentMap;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V";

    @Inject(method = SUBMIT, at = @At("HEAD"))
    private void hidearmor$beginShieldSubmit(DataComponentMap components, PoseStack poseStack,
            SubmitNodeCollector collector, int light, int overlay, boolean leftHand, int color, CallbackInfo ci) {
        if (HideArmorMod.isRenderingLocalShield) {
            ShieldTransparencyMixin.SUBMITTING_LOCAL_SHIELD.set(true);
        }
    }

    @Inject(method = SUBMIT, at = @At("RETURN"))
    private void hidearmor$endShieldSubmit(DataComponentMap components, PoseStack poseStack,
            SubmitNodeCollector collector, int light, int overlay, boolean leftHand, int color, CallbackInfo ci) {
        ShieldTransparencyMixin.SUBMITTING_LOCAL_SHIELD.set(false);
    }
}
