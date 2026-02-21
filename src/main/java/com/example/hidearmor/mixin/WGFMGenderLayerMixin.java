package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.example.hidearmor.LocalPlayerTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Applies the chestplate opacity to the gender mod breast layer.
 * Targets Wildfire's Gender Mod.
 */
@Pseudo
@Mixin(targets = "com.wildfire.render.GenderLayer")
public class WGFMGenderLayerMixin {

    /**
     * The target method is:
     * public static void renderBox(WildfireModelRenderer.ModelBox model,
     * PoseStack.Pose entry, VertexConsumer vertexConsumer, int light, int overlay,
     * int color)
     * The color parameter is the 3rd integer parameter, so ordinal = 2.
     */
    @ModifyVariable(method = "renderBox", at = @At("HEAD"), ordinal = 2, argsOnly = true, remap = false)
    private static int modifyBreastRenderColor(int originalColor) {
        float alpha = HideArmorMod.getConfig().chestplateOpacity;
        if (!LocalPlayerTracker.isRenderingLocalPlayer()) {
            alpha = 1.0f;
        }

        int alphaInt = (int) (alpha * 255.0f);
        // Replace the alpha byte (top 8 bits) of the ARGB color integer
        return (originalColor & 0x00FFFFFF) | (alphaInt << 24);
    }
}
