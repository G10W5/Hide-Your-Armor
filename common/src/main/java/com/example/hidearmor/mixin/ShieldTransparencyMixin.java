package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Gradual shield transparency for 26.1.2 / 26.2.
 *
 * Shields are submitted through the sprite-based overload, which resolves a
 * cutout RenderType before reaching SubmitNodeCollection.submitModel. A cutout
 * type ignores alpha, so fading needs both a blending RenderType and a patched
 * tint colour. Both are applied here on the model submission itself, which is
 * the one entry point shared by the first-person, third-person and inventory
 * shield paths.
 *
 * SUBMITTING_LOCAL_SHIELD is set by ShieldModelRendererMixin around
 * ShieldSpecialRenderer.submit so the patches can't leak onto other models.
 */
@Mixin(SubmitNodeCollection.class)
public class ShieldTransparencyMixin {

    static final ThreadLocal<Boolean> SUBMITTING_LOCAL_SHIELD = ThreadLocal.withInitial(() -> Boolean.FALSE);

    static boolean isFadingShield() {
        if (!Boolean.TRUE.equals(SUBMITTING_LOCAL_SHIELD.get())) {
            return false;
        }
        float opacity = HideArmorMod.getShieldOpacity();
        return opacity > 0.0f && opacity < 1.0f;
    }

    @ModifyVariable(method = "submitModel", at = @At("HEAD"), argsOnly = true)
    private RenderType hidearmor$shieldRenderType(RenderType original) {
        if (!isFadingShield()) {
            return original;
        }
        if (original.toString().toLowerCase().contains("glint")) {
            return original;
        }
        return RenderTypes.entityTranslucent(Sheets.SHIELD_SHEET);
    }

    @ModifyVariable(method = "submitModel", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private int hidearmor$shieldAlpha(int original) {
        if (!isFadingShield()) {
            return original;
        }
        int alpha = (int) (HideArmorMod.getShieldOpacity() * 255.0f);
        return (original & 0x00FFFFFF) | (alpha << 24);
    }
}
