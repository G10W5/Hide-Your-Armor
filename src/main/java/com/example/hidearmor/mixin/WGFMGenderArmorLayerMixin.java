package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.example.hidearmor.LocalPlayerTracker;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.mutable.MutableBoolean;
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
 * - Glint is suppressed by replacing the MutableBoolean glint param with false.
 *   (renderGlint() no longer exists in WGFM 5.x; glint is inlined in renderBreastArmor)
 */
@Pseudo
@Mixin(targets = "com.wildfire.render.GenderArmorLayer")
public class WGFMGenderArmorLayerMixin {

    private static final ThreadLocal<Identifier> CURRENT_TEXTURE = new ThreadLocal<>();

    /**
     * Cancel the breast armor render entirely at 0% opacity.
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
     * Cancel the breast armor TRIM render entirely at 0% opacity.
     */
    @Inject(method = "renderArmorTrim", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRenderArmorTrimHead(CallbackInfo ci) {
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
     */
    @ModifyVariable(method = "renderBreastArmor", at = @At("STORE"), ordinal = 0, remap = false)
    private RenderType modifyBreastArmorLayer(RenderType originalLayer) {
        float alpha = HideArmorMod.getConfig().chestplateOpacity;
        if (LocalPlayerTracker.isRenderingLocalPlayer() && alpha < 1.0f && alpha > 0.0f) {
            Identifier tex = CURRENT_TEXTURE.get();
            CURRENT_TEXTURE.remove();
            if (tex != null) {
                return RenderTypes.armorTranslucent(tex);
            }
        } else {
            CURRENT_TEXTURE.remove();
        }
        return originalLayer;
    }

    /**
     * Suppress glint on breast armor if toggled off in config.
     * In WGFM 5.x, glint is controlled by a MutableBoolean param in renderBreastArmor,
     * not a separate renderGlint() method. We replace it with false to suppress.
     */
    @ModifyVariable(method = "renderBreastArmor", at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false)
    private MutableBoolean suppressGlint(MutableBoolean glint) {
        if (!LocalPlayerTracker.isRenderingLocalPlayer())
            return glint;
        if (!HideArmorMod.getConfig().showGlintChestplate) {
            return new MutableBoolean(false);
        }
        return glint;
    }
}
