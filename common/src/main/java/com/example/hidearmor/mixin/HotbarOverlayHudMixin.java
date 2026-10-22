package com.example.hidearmor.mixin;

import com.example.hidearmor.ShieldHotbarOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.2 moved the hotbar from Gui to Hud. The target is referenced by name so
 * this class still loads on 26.1.2, where Hud does not exist and the mixin is
 * skipped.
 */
@Pseudo
@Mixin(targets = "net.minecraft.client.gui.Hud")
public class HotbarOverlayHudMixin {

    @Inject(method = "extractItemHotbar", at = @At("RETURN"), require = 0)
    private void onAfterHotbar(GuiGraphicsExtractor ctx, DeltaTracker tracker, CallbackInfo ci) {
        ShieldHotbarOverlay.render(ctx);
    }
}
