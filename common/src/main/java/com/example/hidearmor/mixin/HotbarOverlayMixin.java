package com.example.hidearmor.mixin;

import com.example.hidearmor.ShieldHotbarOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.1.2 draws the hotbar on Gui. On 26.2 the hotbar moved to Hud, so this
 * mixin silently does nothing there (see HotbarOverlayHudMixin).
 */
@Mixin(targets = "net.minecraft.client.gui.Gui")
public class HotbarOverlayMixin {

    @Inject(method = "extractItemHotbar", at = @At("RETURN"), require = 0)
    private void onAfterHotbar(GuiGraphicsExtractor ctx, DeltaTracker tracker, CallbackInfo ci) {
        ShieldHotbarOverlay.render(ctx);
    }
}
