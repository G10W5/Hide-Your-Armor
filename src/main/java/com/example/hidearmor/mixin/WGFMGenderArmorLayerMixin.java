package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.example.hidearmor.LocalPlayerTracker;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Handles WGFM breast armor transparency:
 * - At 0% opacity, cancels the render entirely via @Inject at HEAD.
 * - At partial opacity, swaps the render layer to armorTranslucent.
 * - Alpha in the vertex color is handled downstream by WGFMGenderLayerMixin
 * which already modifies the color in GenderLayer.renderBox().
 */
@Pseudo
@Mixin(targets = "com.wildfire.render.GenderArmorLayer")
public class WGFMGenderArmorLayerMixin {

    private static final ThreadLocal<Identifier> CURRENT_TEXTURE = new ThreadLocal<>();

    /**
     * Cancel the armor render entirely at 0% opacity.
     */
    @Inject(method = "renderBreastArmor", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRenderHead(CallbackInfo ci) {
        if (!LocalPlayerTracker.isRenderingLocalPlayer())
            return;
        float alpha = HideArmorMod.getConfig().chestplateOpacity;
        if (alpha <= 0.0f) {
            ci.cancel();
        }
    }

    /**
     * Capture texture argument so we can use it in the layer swap below.
     */
    @ModifyVariable(method = "renderBreastArmor", at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false)
    private Identifier captureTexture(Identifier texture) {
        CURRENT_TEXTURE.set(texture);
        return texture;
    }

    /**
     * Swap armorCutoutNoCull for armorTranslucent when opacity < 1.0.
     * This enables alpha blending on the breast armor geometry.
     * The actual vertex alpha is set by WGFMGenderLayerMixin which already
     * intercepts GenderLayer.renderBox() and modifies the color parameter there.
     */
    @ModifyVariable(method = "renderBreastArmor", at = @At("STORE"), ordinal = 0, remap = false)
    private RenderLayer modifyBreastArmorLayer(RenderLayer originalLayer) {
        float alpha = HideArmorMod.getConfig().chestplateOpacity;
        if (LocalPlayerTracker.isRenderingLocalPlayer() && alpha < 1.0f && alpha > 0.0f) {
            Identifier tex = CURRENT_TEXTURE.get();
            CURRENT_TEXTURE.remove();
            if (tex != null) {
                return RenderLayers.armorTranslucent(tex);
            }
        } else {
            CURRENT_TEXTURE.remove();
        }
        return originalLayer;
    }
}
