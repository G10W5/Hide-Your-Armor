package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import net.minecraft.client.render.item.model.special.ShieldModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ShieldModelRenderer.class)
public class ShieldModelRendererMixin {

    @ModifyVariable(method = "render(Ljava/lang/Object;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;IIZI)V", at = @At("HEAD"), ordinal = 2, argsOnly = true)
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
