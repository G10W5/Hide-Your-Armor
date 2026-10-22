package com.example.hidearmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Draws the shield blocking animation over the offhand slot when the shield
 * itself is hidden.
 *
 * Shared by HotbarOverlayMixin (26.1.2, where the hotbar lives on Gui) and
 * HotbarOverlayHudMixin (26.2, where it moved to Hud).
 */
public final class ShieldHotbarOverlay {

    private ShieldHotbarOverlay() {
    }

    public static void render(GuiGraphicsExtractor ctx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gameMode == null) return;

        float shieldOpacity = HideArmorMod.getShieldOpacity();
        if (shieldOpacity >= 0.25f) return;

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
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        boolean leftHand = mc.options.mainHand().get() == net.minecraft.world.entity.HumanoidArm.LEFT;
        int slotX;
        if (leftHand) {
            slotX = screenW / 2 + 91 + 3;
        } else {
            slotX = screenW / 2 - 91 - 29 + 3;
        }
        int slotY = screenH - 23 + 3;

        int itemX = slotX;
        int itemY = slotY;
        int itemW = 16;
        int itemH = 16;

        // Proportional alpha: at 0% shield opacity -> full overlay, at 25% -> no overlay
        float overlayStrength = 1.0f - (shieldOpacity / 0.25f);
        int overlayAlpha = (int) (overlayStrength * 0xAA);

        // Sweep from top: progress goes 0->1 as the dark bar fills downward
        int sweepH = (int) (itemH * progress);

        if (sweepH > 0) {
            int overlayColor = (overlayAlpha << 24) | 0x000000;
            ctx.fill(itemX, itemY, itemX + itemW, itemY + sweepH, overlayColor);
        }

        // Bright leading edge line, also scaled by overlay strength
        if (sweepH < itemH) {
            int edgeAlpha = (int) (overlayStrength * 0xCC);
            int edgeColor = (edgeAlpha << 24) | 0xFFFFFF;
            ctx.fill(itemX, itemY + sweepH, itemX + itemW, itemY + sweepH + 1, edgeColor);
        }
    }
}
