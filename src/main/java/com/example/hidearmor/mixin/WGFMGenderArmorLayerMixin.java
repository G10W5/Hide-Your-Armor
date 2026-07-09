package com.example.hidearmor.mixin;

import com.example.hidearmor.LocalPlayerTracker;
import com.example.hidearmor.ModConfig;
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

    @Inject(method = "renderBreastArmor", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRenderHead(CallbackInfo ci) {
        ModConfig cfg = LocalPlayerTracker.getConfigForCurrentPlayer();
        if (cfg == null) return;
        if (cfg.chestplateOpacity <= 0.0f) {
            ci.cancel();
        }
    }

    @Inject(method = "renderArmorTrim", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRenderArmorTrimHead(CallbackInfo ci) {
        ModConfig cfg = LocalPlayerTracker.getConfigForCurrentPlayer();
        if (cfg == null) return;
        if (cfg.chestplateOpacity <= 0.0f) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderBreastArmor", at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false)
    private Identifier captureTexture(Identifier texture) {
        CURRENT_TEXTURE.set(texture);
        return texture;
    }

    @ModifyVariable(method = "renderBreastArmor", at = @At("STORE"), ordinal = 0, remap = false)
    private RenderType modifyBreastArmorLayer(RenderType originalLayer) {
        ModConfig cfg = LocalPlayerTracker.getConfigForCurrentPlayer();
        if (cfg != null && cfg.chestplateOpacity < 1.0f && cfg.chestplateOpacity > 0.0f) {
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

    @ModifyVariable(method = "renderBreastArmor", at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false)
    private MutableBoolean suppressGlint(MutableBoolean glint) {
        ModConfig cfg = LocalPlayerTracker.getConfigForCurrentPlayer();
        if (cfg == null) return glint;
        if (!cfg.showGlintChestplate) {
            return new MutableBoolean(false);
        }
        return glint;
    }
}
