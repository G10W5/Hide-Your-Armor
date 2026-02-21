package com.example.hidearmor;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HideArmorScreen extends Screen {
        private enum ActiveTab {
                ARMOR, OFFHAND
        }

        private ActiveTab activeTab = ActiveTab.ARMOR;

        // Panel layout constants
        private static final int PANEL_W = 400;
        private static final int PANEL_H = 250;
        private static final int LEFT_W = 215;
        private static final int SLIDER_W = 152;
        private static final int SPACING = 24;
        private static final int SLIDER_TOP = 42;
        private static final int TOGGLE_TOP = 150;
        private static final int ICON_TOP = 162;
        private static final int DONE_TOP = PANEL_H - 26;

        // Open animation: slides up from below + fades in
        private long openTime = -1;
        private long closeTime = -1;
        private static final float ANIM_MS = 380f;

        private PlayerPreviewWidget previewWidget;

        // Item icons drawn next to each slider (cleared on every rebuildWidgets)
        private record IconInfo(net.minecraft.item.Item item, int x, int y) {
        }

        private final List<IconInfo> sliderIcons = new ArrayList<>();

        public HideArmorScreen(Screen parent) {
                super(Text.translatable("gui.hidearmor.title"));
        }

        private int px() {
                return (this.width - PANEL_W) / 2;
        }

        private int py() {
                return (this.height - PANEL_H) / 2;
        }

        private float animProgress() {
                long now = System.currentTimeMillis();
                if (closeTime > 0) {
                        float t = 1f - Math.min((now - closeTime) / ANIM_MS, 1f);
                        if (t <= 0) {
                                super.close();
                                return 0f;
                        }
                        return t * t * (3f - 2f * t);
                }
                if (openTime < 0)
                        return 1f;
                float t = Math.min((now - openTime) / ANIM_MS, 1f);
                return t * t * (3f - 2f * t);
        }

        @Override
        protected void init() {
                if (openTime < 0)
                        openTime = System.currentTimeMillis();
                rebuildWidgets();
        }

        private void rebuildWidgets() {
                this.clearChildren();
                sliderIcons.clear();

                ModConfig config = HideArmorMod.getConfig();
                int px = px(), py = py();
                int contentX = px + 18;

                // ---- Tab buttons ----
                int tabW = 22, tabGap = 26;
                this.addDrawableChild(new ToggleIconButton(contentX, py + 8, tabW, tabW, Items.IRON_CHESTPLATE,
                                activeTab == ActiveTab.ARMOR, b -> {
                                        activeTab = ActiveTab.ARMOR;
                                        rebuildWidgets();
                                }, false));
                this.addDrawableChild(new ToggleIconButton(contentX + tabGap, py + 8, tabW, tabW, Items.SHIELD,
                                activeTab == ActiveTab.OFFHAND, b -> {
                                        activeTab = ActiveTab.OFFHAND;
                                        rebuildWidgets();
                                }, false));

                // ---- Sliders ----
                int sliderX = contentX + 22;
                int iconX = contentX + 2;
                int sliderY = py + SLIDER_TOP;

                if (activeTab == ActiveTab.ARMOR) {
                        addSlider(sliderX, iconX, sliderY, "gui.hidearmor.helmet", Items.DIAMOND_HELMET,
                                        config.helmetOpacity, v -> config.helmetOpacity = v.floatValue());
                        addSlider(sliderX, iconX, sliderY + SPACING, "gui.hidearmor.chestplate",
                                        Items.DIAMOND_CHESTPLATE, config.chestplateOpacity,
                                        v -> config.chestplateOpacity = v.floatValue());
                        addSlider(sliderX, iconX, sliderY + SPACING * 2, "gui.hidearmor.leggings",
                                        Items.DIAMOND_LEGGINGS, config.leggingsOpacity,
                                        v -> config.leggingsOpacity = v.floatValue());
                        addSlider(sliderX, iconX, sliderY + SPACING * 3, "gui.hidearmor.boots", Items.DIAMOND_BOOTS,
                                        config.bootsOpacity, v -> config.bootsOpacity = v.floatValue());
                } else {
                        addSlider(sliderX, iconX, sliderY, "gui.hidearmor.offhand", Items.SHIELD, config.shieldOpacity,
                                        v -> config.shieldOpacity = v.floatValue());
                }

                // ---- Visibility toggles ----
                int iconY = py + ICON_TOP;
                int iconGap = 30;
                this.addDrawableChild(new ToggleIconButton(contentX, iconY, 26, 26, Items.SKELETON_SKULL,
                                !config.showSkullsAndBlocks, b -> {
                                        config.showSkullsAndBlocks = !config.showSkullsAndBlocks;
                                        rebuildWidgets();
                                }, !config.showSkullsAndBlocks));
                this.addDrawableChild(new ToggleIconButton(contentX + iconGap, iconY, 26, 26, Items.ELYTRA,
                                !config.showElytra, b -> {
                                        config.showElytra = !config.showElytra;
                                        rebuildWidgets();
                                }, !config.showElytra));

                // ---- Done button ----
                this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close())
                                .dimensions(px + 12, py + DONE_TOP, 70, 20).build());

                // ---- Player preview ----
                int rightW = PANEL_W - LEFT_W;
                int previewW = 130, previewH = 200;
                this.previewWidget = new PlayerPreviewWidget(
                                px + LEFT_W + (rightW - previewW) / 2,
                                py + (PANEL_H - previewH) / 2,
                                previewW, previewH);
                this.addDrawableChild(this.previewWidget);
        }

        private void addSlider(int sliderX, int iconX, int sliderY, String key, net.minecraft.item.Item icon,
                        float init, java.util.function.Consumer<Double> setter) {
                // Direct slider widget
                this.addDrawableChild(new SimpleOption<>(key, SimpleOption.emptyTooltip(),
                                (t, v) -> Text.literal(Text.translatable(key).getString().split(" ")[0] + " "
                                                + (int) (v * 100) + "%"),
                                SimpleOption.DoubleSliderCallbacks.INSTANCE, Codec.DOUBLE, (double) init, setter)
                                .createWidget(client.options, sliderX, sliderY, SLIDER_W));

                // Store icon info for rendering
                sliderIcons.add(new IconInfo(icon, iconX, sliderY + 2));
        }

        // ============================================================
        // Rendering
        // ============================================================

        @Override
        public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
                // Only apply the blur (full-screen, not animated)
                super.renderBackground(ctx, mx, my, delta);
        }

        @Override
        public boolean shouldPause() {
                return false;
        }

        @Override
        public void render(DrawContext ctx, int mx, int my, float delta) {
                float anim = animProgress();
                int yOff = (int) ((1f - anim) * 50f);

                if (this.previewWidget != null) {
                        this.previewWidget.setSlideYOffset(yOff);
                }

                // Push matrix so the entire panel (background + widgets) slides up together
                ctx.getMatrices().pushMatrix();
                ctx.getMatrices().translate(0, yOff);

                // Draw panel background / borders at translated position
                drawPanel(ctx, anim);

                // Render all children with adjusted mouse Y so hit-testing still works
                super.render(ctx, mx, my - yOff, delta);

                // Post-child decorations (on top of sliders)
                int px = px(), py = py();
                int contentX = px + 18;
                int tabGap = 26;
                int activeTabX = (activeTab == ActiveTab.ARMOR) ? contentX : contentX + tabGap;
                ctx.fill(activeTabX, py + 8 + 22 + 3, activeTabX + 22, py + 8 + 22 + 5, 0xFFFFFFFF);
                ctx.drawText(this.textRenderer, "Visibility", contentX, py + TOGGLE_TOP, 0xFF888888, false);

                // Item icons next to sliders
                for (IconInfo info : sliderIcons) {
                        ctx.drawItem(new ItemStack(info.item()), info.x(), info.y());
                }

                ctx.getMatrices().popMatrix();
        }

        private void drawPanel(DrawContext ctx, float anim) {
                int px = px(), py = py();
                int alpha = (int) (anim * 240);

                // Primary panel fill - dark layered gradient for a premium look
                // Layer 1: deep dark base
                ctx.fill(px, py, px + PANEL_W, py + PANEL_H, (alpha << 24) | 0x0A0B10);
                // Layer 2: subtle left-side lighter strip for depth
                ctx.fill(px, py, px + LEFT_W, py + PANEL_H, 0x18FFFFFF);
                // Layer 3: right-side preview area slightly different
                ctx.fill(px + LEFT_W + 1, py, px + PANEL_W, py + PANEL_H, 0x10000000);

                // Subtle gradient: top brighter edge
                ctx.fill(px, py, px + PANEL_W, py + 2, 0x20FFFFFF);
                ctx.fill(px, py + 2, px + PANEL_W, py + 6, 0x10FFFFFF);

                // Border
                int borderAlpha = (int) (anim * 200);
                int borderColor = (borderAlpha << 24) | 0xA0A0A0;
                ctx.fill(px, py, px + PANEL_W, py + 1, borderColor);
                ctx.fill(px, py + PANEL_H - 1, px + PANEL_W, py + PANEL_H, borderColor);
                ctx.fill(px, py, px + 1, py + PANEL_H, borderColor);
                ctx.fill(px + PANEL_W - 1, py, px + PANEL_W, py + PANEL_H, borderColor);

                // Vertical divider
                ctx.fill(px + LEFT_W, py + 5, px + LEFT_W + 1, py + PANEL_H - 5, 0xFF404050);
        }

        @Override
        public void close() {
                HideArmorMod.getConfig().save();
                super.close();
        }

        // ============================================================
        // Toggle Icon Button
        // ============================================================
        private class ToggleIconButton extends ButtonWidget {
                private final net.minecraft.item.Item item;
                private final boolean showCross;

                public ToggleIconButton(int x, int y, int w, int h, net.minecraft.item.Item item,
                                boolean active, PressAction onPress, boolean showCross) {
                        super(x, y, w, h, net.minecraft.text.Text.of(""), onPress, DEFAULT_NARRATION_SUPPLIER);
                        this.item = item;
                        this.showCross = showCross;
                }

                @Override
                public void drawIcon(DrawContext ctx, int mx, int my, float delta) {
                        ctx.drawItem(new ItemStack(item), getX() + (getWidth() - 16) / 2,
                                        getY() + (getHeight() - 16) / 2);
                        if (showCross) {
                                int x1 = getX() + 2, y1 = getY() + 2;
                                int x2 = getX() + getWidth() - 2, y2 = getY() + getHeight() - 2;
                                int dx = x2 - x1, dy = y2 - y1;
                                for (int i = 0; i < dx; i++) {
                                        int ry1 = y1 + i * dy / dx;
                                        int ry2 = y2 - i * dy / dx;
                                        ctx.fill(x1 + i, ry1, x1 + i + 1, ry1 + 2, 0xEEFF2222);
                                        ctx.fill(x1 + i, ry2 - 1, x1 + i + 1, ry2 + 1, 0xEEFF2222);
                                }
                        }
                }
        }
}
