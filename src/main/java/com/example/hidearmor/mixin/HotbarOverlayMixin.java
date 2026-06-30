package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.example.hidearmor.ShieldBlockingOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class HotbarOverlayMixin {

    /**
     * After the hotbar is drawn, overlay the blocking animation on the
     * offhand shield slot (only when shield opacity is 0 and player is blocking).
     */
    @Inject(method = "extractItemHotbar", at = @At("RETURN"))
    private void onAfterHotbar(GuiGraphicsExtractor ctx, DeltaTracker tracker, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gameMode == null) return;

        // Only show if shield is hidden (opacity == 0)
        float shieldOpacity = HideArmorMod.getShieldOpacity();
        if (shieldOpacity > 0.0f) return;

        // Only show if offhand item is a shield
        ItemStack offhand = mc.player.getItemInHand(InteractionHand.OFF_HAND);
        if (!offhand.is(Items.SHIELD)) return;

        // Detect blocking state
        boolean blocking = mc.player.isBlocking();
        ShieldBlockingOverlay.tick(blocking);

        if (!blocking) return;

        float progress = ShieldBlockingOverlay.getProgress();
        if (progress < 0) return;

        // --- Hotbar layout (vanilla constants) ---
        // Main hotbar: width/2 - 91 .. width/2 + 91,  y = height - 22
        // Offhand slot (right-handed): x = width/2 - 91 - 29,  y = height - 23
        // Item inside the slot is 16x16, starting at slotX + 3, slotY + 3

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        boolean leftHand = mc.options.mainHand().get() == net.minecraft.world.entity.HumanoidArm.LEFT;
        int slotX;
        if (leftHand) {
            // offhand on right
            slotX = screenW / 2 + 91 + 3;
        } else {
            // offhand on left (default)
            slotX = screenW / 2 - 91 - 29 + 3;
        }
        int slotY = screenH - 23 + 3;

        // Item area is 16x16
        int itemX = slotX;
        int itemY = slotY;
        int itemW = 16;
        int itemH = 16;

        // Sweep from top: progress goes 0→1 as the dark bar fills downward
        int sweepH = (int) (itemH * progress);

        if (sweepH > 0) {
            // Dark semi-transparent fill (like vanilla cooldown)
            ctx.fill(itemX, itemY, itemX + itemW, itemY + sweepH, 0xAA000000);
        }

        // Bright leading edge line for polish
        if (sweepH < itemH) {
            ctx.fill(itemX, itemY + sweepH, itemX + itemW, itemY + sweepH + 1, 0xCCFFFFFF);
        }
    }
}
