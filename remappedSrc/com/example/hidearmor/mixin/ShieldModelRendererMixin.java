package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ShieldSpecialRenderer.class)
public class ShieldModelRendererMixin {

    @ModifyVariable(method = "submit(Ljava/lang/Object;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private int modifyShieldColor(int colorArgb) {
        if (HideArmorMod.isRenderingLocalShield) {
            float opacity = HideArmorMod.getShieldOpacity();
            if (opacity > 0.0f && opacity < 1.0f) {
                int alpha = (int) (opacity * 255.0f);
                return (colorArgb & 0x00FFFFFF) | (alpha << 24);
            }
        }
        return colorArgb;
    }
}
