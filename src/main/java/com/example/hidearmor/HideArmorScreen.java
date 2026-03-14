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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class HideArmorScreen extends Screen {
        private enum ActiveTab {
                ARMOR, OFFHAND
        }

        private ActiveTab activeTab = ActiveTab.ARMOR;

        // Panel layout constants
        private static final int PANEL_W = 340;
        private static final int PANEL_H = 220;
        private static final int LEFT_W = 195;
        private static final int SLIDER_W = 130;
        private static final int SPACING = 24;
        private static final int SLIDER_TOP = 40;
        private static final int TOGGLE_TOP = 145;
        private static final int ICON_TOP = 157;
        private static final int DONE_TOP = PANEL_H - 24;

        // Open animation: slides up from below + fades in
        private long openTime = -1;
        private long closeTime = -1;
        private static final float ANIM_MS = 380f;

        private PlayerPreviewWidget previewWidget;
        private net.minecraft.client.gui.widget.ClickableWidget chestplateSlider;
        private final boolean isWGFMLoaded = FabricLoader.getInstance().isModLoaded("wildfire_gender") || FabricLoader.getInstance().isModLoaded("wildfire-gender");
        private static final Identifier BG_TEXTURE = Identifier.of("hidearmor", "textures/gui/bg.png");

        // Item icons drawn next to each slider (cleared on every rebuildWidgets)
        private record IconInfo(net.minecraft.item.Item item, int x, int y) {
        }

        private final List<IconInfo> sliderIcons = new ArrayList<>();

        // Glint toggle info for rendering
        private record GlintToggleInfo(int x, int y, boolean enabled) {
        }
        private final List<GlintToggleInfo> glintToggles = new ArrayList<>();

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
                glintToggles.clear();

                ModConfig config = HideArmorMod.getConfig();
                int px = px(), py = py();
                int contentX = px + 10;

                // ---- Tab buttons ----
                int tabW = 22, tabGap = 26;
                this.addDrawableChild(new ToggleIconButton(contentX, py + 6, tabW, tabW, Items.IRON_CHESTPLATE,
                                activeTab == ActiveTab.ARMOR, b -> {
                                        activeTab = ActiveTab.ARMOR;
                                        rebuildWidgets();
                                }, false));
                this.addDrawableChild(new ToggleIconButton(contentX + tabGap, py + 6, tabW, tabW, Items.SHIELD,
                                activeTab == ActiveTab.OFFHAND, b -> {
                                        activeTab = ActiveTab.OFFHAND;
                                        rebuildWidgets();
                                }, false));

                // ---- Sliders ----
                int sliderX = contentX + 20;
                int iconX = contentX + 2;
                int sliderY = py + SLIDER_TOP;
                int glintBtnX = sliderX + SLIDER_W + 4;

                if (activeTab == ActiveTab.ARMOR) {
                        addSlider(sliderX, iconX, sliderY, "gui.hidearmor.helmet", Items.DIAMOND_HELMET,
                                        config.helmetOpacity, v -> config.helmetOpacity = v.floatValue());
                        addGlintToggle(glintBtnX, sliderY, config.showGlintHelmet, b -> {
                                config.showGlintHelmet = !config.showGlintHelmet;
                                rebuildWidgets();
                        });

                        addSlider(sliderX, iconX, sliderY + SPACING, "gui.hidearmor.chestplate",
                                        Items.DIAMOND_CHESTPLATE, config.chestplateOpacity,
                                        v -> config.chestplateOpacity = v.floatValue());
                        this.chestplateSlider = (net.minecraft.client.gui.widget.ClickableWidget) this.children().get(this.children().size() - 1);
                        addGlintToggle(glintBtnX, sliderY + SPACING, config.showGlintChestplate, b -> {
                                config.showGlintChestplate = !config.showGlintChestplate;
                                rebuildWidgets();
                        });

                        addSlider(sliderX, iconX, sliderY + SPACING * 2, "gui.hidearmor.leggings",
                                        Items.DIAMOND_LEGGINGS, config.leggingsOpacity,
                                        v -> config.leggingsOpacity = v.floatValue());
                        addGlintToggle(glintBtnX, sliderY + SPACING * 2, config.showGlintLeggings, b -> {
                                config.showGlintLeggings = !config.showGlintLeggings;
                                rebuildWidgets();
                        });

                        addSlider(sliderX, iconX, sliderY + SPACING * 3, "gui.hidearmor.boots", Items.DIAMOND_BOOTS,
                                        config.bootsOpacity, v -> config.bootsOpacity = v.floatValue());
                        addGlintToggle(glintBtnX, sliderY + SPACING * 3, config.showGlintBoots, b -> {
                                config.showGlintBoots = !config.showGlintBoots;
                                rebuildWidgets();
                        });
                } else {
                        String btnText = Text.translatable("gui.hidearmor.shield").getString() + ": "
                                        + (config.shieldOpacity > 0.5f ? "ON" : "OFF");
                        this.addDrawableChild(ButtonWidget.builder(Text.literal(btnText), btn -> {
                                config.shieldOpacity = (config.shieldOpacity > 0.5f) ? 0.0f : 1.0f;
                                rebuildWidgets();
                        }).dimensions(sliderX, sliderY, SLIDER_W, 20).build());
                        sliderIcons.add(new IconInfo(Items.SHIELD, iconX, sliderY + 2));
                        addGlintToggle(glintBtnX, sliderY, config.showGlintShield, b -> {
                                config.showGlintShield = !config.showGlintShield;
                                rebuildWidgets();
                        });
                }

                // ---- Visibility toggles ----
                int iconY = py + ICON_TOP;
                int iconGap = 28;
                this.addDrawableChild(new ToggleIconButton(contentX, iconY, 24, 24, Items.SKELETON_SKULL,
                                !config.showSkullsAndBlocks, b -> {
                                        config.showSkullsAndBlocks = !config.showSkullsAndBlocks;
                                        rebuildWidgets();
                                }, !config.showSkullsAndBlocks));
                this.addDrawableChild(new ToggleIconButton(contentX + iconGap, iconY, 24, 24, Items.ELYTRA,
                                !config.showElytra, b -> {
                                        config.showElytra = !config.showElytra;
                                        rebuildWidgets();
                                }, !config.showElytra));
                this.addDrawableChild(new TooltipToggleIconButton(contentX + iconGap * 2, iconY, 24, 24,
                                Items.COMPASS, !config.enableMultiplayerSync,
                                b -> {
                                        config.enableMultiplayerSync = !config.enableMultiplayerSync;
                                        if (config.enableMultiplayerSync)
                                                HideArmorMod.broadcastConfig();
                                        rebuildWidgets();
                                }, !config.enableMultiplayerSync, "Multiplayer sync"));

                // ---- Done button ----
                this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close())
                                .dimensions(px + 8, py + DONE_TOP, 60, 20).build());

                // ---- Player preview ----
                int rightW = PANEL_W - LEFT_W;
                int previewW = rightW - 16, previewH = PANEL_H - 16;
                this.previewWidget = new PlayerPreviewWidget(
                                px + LEFT_W + 8,
                                py + 8,
                                previewW, previewH);
                this.addDrawableChild(this.previewWidget);
        }

        private void addGlintToggle(int x, int y, boolean currentState, ButtonWidget.PressAction onPress) {
                net.minecraft.item.Item icon = currentState ? Items.ENCHANTED_BOOK : Items.BOOK;
                ToggleIconButton btn = new ToggleIconButton(x, y, 20, 20, icon, currentState, onPress, !currentState);
                this.addDrawableChild(btn);
                glintToggles.add(new GlintToggleInfo(x, y, currentState));
        }

        private net.minecraft.client.gui.widget.ClickableWidget addSlider(int sliderX, int iconX, int sliderY,
                        String key, net.minecraft.item.Item icon,
                        float init, java.util.function.Consumer<Double> setter) {
                // Direct slider widget
                net.minecraft.client.gui.widget.ClickableWidget widget = new SimpleOption<>(key,
                                SimpleOption.emptyTooltip(),
                                (t, v) -> Text.literal(Text.translatable(key).getString().split(" ")[0] + " "
                                                + (int) (v * 100) + "%"),
                                SimpleOption.DoubleSliderCallbacks.INSTANCE, Codec.DOUBLE, (double) init, setter)
                                .createWidget(client.options, sliderX, sliderY, SLIDER_W);
                this.addDrawableChild(widget);

                // Store icon info for rendering
                sliderIcons.add(new IconInfo(icon, iconX, sliderY + 2));
                return widget;
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
                int contentX = px + 10;
                int tabGap = 26;
                int activeTabX = (activeTab == ActiveTab.ARMOR) ? contentX : contentX + tabGap;

                // Active tab underline
                ctx.fill(activeTabX, py + 6 + 22 + 2, activeTabX + 22, py + 6 + 22 + 3, 0xFFFFFFFF);

                // "Visibility" label
                ctx.drawText(this.textRenderer, "Visibility", contentX, py + TOGGLE_TOP, 0xFF888888, false);

                // "Glint" column header (centered above the toggle buttons)
                if (activeTab == ActiveTab.ARMOR) {
                        int glintHeaderX = contentX + 20 + SLIDER_W + 6;
                        ctx.drawText(this.textRenderer, "Glint", glintHeaderX, py + 30, 0xFF777777, false);
                }

                // WGFM contextual warning
                if (activeTab == ActiveTab.ARMOR && this.isWGFMLoaded && this.chestplateSlider != null
                                && (this.chestplateSlider.isHovered() || this.chestplateSlider.isFocused())) {
                        ctx.drawTextWithShadow(this.textRenderer, "! WGFM Breast Armor: 0% or >0% only", contentX, py + SLIDER_TOP + SPACING + 22, 0xFFFFFF00);
                }

                // Item icons next to sliders
                for (IconInfo info : sliderIcons) {
                        ctx.drawItem(new ItemStack(info.item()), info.x(), info.y());
                }

                ctx.getMatrices().popMatrix();
        }

        private void drawPanel(DrawContext ctx, float anim) {
                int px = px(), py = py();
                int alpha = (int) (anim * 255);
                ModConfig config = HideArmorMod.getConfig();
                boolean isSleek = "Sleek".equalsIgnoreCase(config.uiTheme);

                if (isSleek) {
                        // ---- Sleek Theme: Minecraft-style beveled border ----

                        // Main background (dark charcoal, like MC inventory)
                        ctx.fill(px, py, px + PANEL_W, py + PANEL_H, 0xFF1A1A1E);

                        // MC-style beveled border (outer)
                        // Top edge - light
                        ctx.fill(px, py, px + PANEL_W, py + 2, 0xFFFFFFFF);
                        // Left edge - light
                        ctx.fill(px, py, px + 2, py + PANEL_H, 0xFFFFFFFF);
                        // Bottom edge - dark shadow
                        ctx.fill(px, py + PANEL_H - 2, px + PANEL_W, py + PANEL_H, 0xFF373737);
                        // Right edge - dark shadow
                        ctx.fill(px + PANEL_W - 2, py, px + PANEL_W, py + PANEL_H, 0xFF373737);

                        // MC-style beveled border (inner bevel)
                        // Top inner - slightly darker than white
                        ctx.fill(px + 2, py + 2, px + PANEL_W - 2, py + 3, 0xFFC6C6C6);
                        // Left inner
                        ctx.fill(px + 2, py + 2, px + 3, py + PANEL_H - 2, 0xFFC6C6C6);
                        // Bottom inner - lighter shadow
                        ctx.fill(px + 2, py + PANEL_H - 3, px + PANEL_W - 2, py + PANEL_H - 2, 0xFF8B8B8B);
                        // Right inner
                        ctx.fill(px + PANEL_W - 3, py + 2, px + PANEL_W - 2, py + PANEL_H - 2, 0xFF8B8B8B);

                        // Inner panel fill (dark grey like MC containers)
                        ctx.fill(px + 3, py + 3, px + PANEL_W - 3, py + PANEL_H - 3, 0xFF2D2D2D);

                        // Horizontal separator under tabs
                        ctx.fill(px + 6, py + 34, px + LEFT_W - 6, py + 35, 0xFF4A4A54);

                        // Inset backgrounds for each slider row (darker recessed slots)
                        int sliderY = py + SLIDER_TOP;
                        int sliderX = px + 10 + 20;
                        for (int i = 0; i < 4; i++) {
                                int rowY = sliderY + SPACING * i;
                                // Inset shadow (top-left darker, bottom-right lighter)
                                ctx.fill(sliderX - 2, rowY - 1, sliderX + SLIDER_W + 2, rowY + 21, 0xFF191919);
                                ctx.fill(sliderX - 1, rowY, sliderX + SLIDER_W + 1, rowY + 20, 0xFF222222);
                        }

                        // Horizontal separator above visibility section
                        ctx.fill(px + 6, py + TOGGLE_TOP - 5, px + LEFT_W - 6, py + TOGGLE_TOP - 4, 0xFF4A4A54);

                        // Vertical divider between left pane and player preview (recessed)
                        ctx.fill(px + LEFT_W - 1, py + 4, px + LEFT_W, py + PANEL_H - 4, 0xFF191919);
                        ctx.fill(px + LEFT_W, py + 4, px + LEFT_W + 1, py + PANEL_H - 4, 0xFF4A4A54);

                } else {
                        // ---- Cobblestone Theme: tiled texture ----
                        int tileSize = 64;
                        for (int ty = 0; ty < PANEL_H; ty += tileSize) {
                                for (int tx = 0; tx < PANEL_W; tx += tileSize) {
                                        int tw = Math.min(tileSize, PANEL_W - tx);
                                        int th = Math.min(tileSize, PANEL_H - ty);
                                        ctx.drawTexture(net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED, BG_TEXTURE, px + tx, py + ty, 0.0f, 0.0f, tw, th, tileSize, tileSize, 0xFFFFFFFF);
                                }
                        }

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

                // Fade overlay tied to animation alpha
                if (alpha < 255) {
                        ctx.fill(px, py, px + PANEL_W, py + PANEL_H, ((255 - alpha) << 24) | 0x000000);
                }
        }

        @Override
        public void close() {
                HideArmorMod.getConfig().save();
                HideArmorMod.broadcastConfig();
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

        // ============================================================
        // Tooltip Toggle Icon Button (compass / multiplayer sync)
        // ============================================================
        private class TooltipToggleIconButton extends ToggleIconButton {
                private final String tooltipText;

                public TooltipToggleIconButton(int x, int y, int w, int h, net.minecraft.item.Item item,
                                boolean active, PressAction onPress, boolean showCross, String tooltipText) {
                        super(x, y, w, h, item, active, onPress, showCross);
                        this.tooltipText = tooltipText;
                }

                @Override
                public void drawIcon(DrawContext ctx, int mx, int my, float delta) {
                        super.drawIcon(ctx, mx, my, delta);
                        if (isHovered()) {
                                ctx.drawTooltip(HideArmorScreen.this.textRenderer,
                                                net.minecraft.text.Text.literal(tooltipText), mx, my);
                        }
                }
        }
}
