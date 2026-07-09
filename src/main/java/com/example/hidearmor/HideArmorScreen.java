package com.example.hidearmor;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HideArmorScreen extends Screen {
        private enum ActiveTab {
                ARMOR, OFFHAND
        }

        private ActiveTab activeTab = ActiveTab.ARMOR;

        // Main panel layout
        private static final int PANEL_W = 340;
        private static final int PANEL_H = 220;
        private static final int LEFT_W = 195;
        private static final int SLIDER_W = 130;
        private static final int SPACING = 24;
        private static final int SLIDER_TOP = 40;
        private static final int TOGGLE_TOP = 145;
        private static final int ICON_TOP = 157;
        private static final int DONE_TOP = PANEL_H - 24;

        // Preset strip layout (separate panel above main)
        private static final int PRESET_STRIP_W = PANEL_W;
        private static final int PRESET_STRIP_H = 32;
        private static final int PRESET_STRIP_GAP = 4;

        // Open animation
        private long openTime = -1;
        private long closeTime = -1;
        private static final float ANIM_MS = 380f;

        private PlayerPreviewWidget previewWidget;
        private static final Identifier BG_TEXTURE = Identifier.fromNamespaceAndPath("hidearmor", "textures/gui/bg.png");

        // Slider icons
        private record IconInfo(net.minecraft.world.item.Item item, int x, int y) {}
        private final List<IconInfo> sliderIcons = new ArrayList<>();

        // Glint toggle info
        private record GlintToggleInfo(int x, int y, boolean enabled) {}
        private final List<GlintToggleInfo> glintToggles = new ArrayList<>();

        // Preset tooltip info
        private record PresetTooltipInfo(int x, int y, int w, int h, String name) {}
        private final List<PresetTooltipInfo> presetTooltips = new ArrayList<>();

        // Naming popup state
        private boolean showingNamePopup = false;
        private EditBox nameInput;
        private Button confirmBtn;
        private Button cancelBtn;

        public HideArmorScreen(Screen parent) {
                super(Component.translatable("gui.hidearmor.title"));
        }

        private int px() {
                return (this.width - PANEL_W) / 2;
        }

        private int py() {
                return (this.height - PANEL_H - PRESET_STRIP_H - PRESET_STRIP_GAP) / 2 + PRESET_STRIP_H + PRESET_STRIP_GAP;
        }

        private int presetStripX() {
                return (this.width - PRESET_STRIP_W) / 2;
        }

        private int presetStripY() {
                return py() - PRESET_STRIP_H - PRESET_STRIP_GAP;
        }

        private float animProgress() {
                long now = System.currentTimeMillis();
                if (closeTime > 0) {
                        float t = 1f - Math.min((now - closeTime) / ANIM_MS, 1f);
                        if (t <= 0) {
                                super.onClose();
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
                buildWidgets();
        }

        @Override
        protected void rebuildWidgets() {
                if (!showingNamePopup) {
                        buildWidgets();
                } else {
                        buildNamePopup();
                }
        }

        private void buildWidgets() {
                this.clearWidgets();
                sliderIcons.clear();
                glintToggles.clear();
                presetTooltips.clear();

                ModConfig config = HideArmorMod.getConfig();
                int px = px(), py = py();
                int contentX = px + 10;

                // ---- Tab buttons ----
                int tabW = 22, tabGap = 26;
                this.addRenderableWidget(new ToggleIconButton(contentX, py + 6, tabW, tabW, Items.IRON_CHESTPLATE,
                                activeTab == ActiveTab.ARMOR, b -> {
                                        activeTab = ActiveTab.ARMOR;
                                        rebuildWidgets();
                                }, false));
                this.addRenderableWidget(new ToggleIconButton(contentX + tabGap, py + 6, tabW, tabW, Items.SHIELD,
                                activeTab == ActiveTab.OFFHAND, b -> {
                                        activeTab = ActiveTab.OFFHAND;
                                        rebuildWidgets();
                                }, false));

                // ---- Preset strip buttons (in the separate strip panel) ----
                int psX = presetStripX() + 8;
                int psY = presetStripY() + 16;
                int presetBtnSize = 14;

                // [+] button
                this.addRenderableWidget(new PresetButton(
                                psX, psY, presetBtnSize, presetBtnSize,
                                "+", null, false, false, b -> {
                                        showingNamePopup = true;
                                        buildNamePopup();
                                }));
                psX += presetBtnSize + 3;

                // Existing preset buttons
                for (int i = 0; i < config.presets.size() && i < 9; i++) {
                        final int idx = i;
                        ModConfig.Preset preset = config.presets.get(i);
                        boolean canDelete = !preset.isDefault();

                        this.addRenderableWidget(new PresetButton(
                                        psX, psY, presetBtnSize, presetBtnSize,
                                        String.valueOf(i + 1), preset.name(), canDelete, canDelete,
                                        b -> {
                                                config.presets.get(idx).applyTo(config);
                                                rebuildWidgets();
                                        }));
                        presetTooltips.add(new PresetTooltipInfo(
                                        psX, psY, presetBtnSize, presetBtnSize, preset.name()));
                        psX += presetBtnSize + 3;
                }

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
                        addSlider(sliderX, iconX, sliderY, "gui.hidearmor.shield", Items.SHIELD,
                                        config.shieldOpacity, v -> config.shieldOpacity = v.floatValue());
                        addGlintToggle(glintBtnX, sliderY, config.showGlintShield, b -> {
                                config.showGlintShield = !config.showGlintShield;
                                rebuildWidgets();
                        });
                }

                // ---- Visibility toggles ----
                int iconY = py + ICON_TOP;
                int iconGap = 28;
                this.addRenderableWidget(new TooltipToggleIconButton(contentX, iconY, 24, 24, Items.SKELETON_SKULL,
                                !config.showSkullsAndBlocks, b -> {
                                        config.showSkullsAndBlocks = !config.showSkullsAndBlocks;
                                        rebuildWidgets();
                                }, !config.showSkullsAndBlocks,
                                Component.translatable("gui.hidearmor.tooltip.skull").getString()));
                this.addRenderableWidget(new TooltipToggleIconButton(contentX + iconGap, iconY, 24, 24, Items.ELYTRA,
                                !config.showElytra, b -> {
                                        config.showElytra = !config.showElytra;
                                        rebuildWidgets();
                                }, !config.showElytra,
                                Component.translatable("gui.hidearmor.tooltip.elytra").getString()));
                this.addRenderableWidget(new TooltipToggleIconButton(contentX + iconGap * 2, iconY, 24, 24,
                                Items.COMPASS, !config.enableMultiplayerSync,
                                b -> {
                                        config.enableMultiplayerSync = !config.enableMultiplayerSync;
                                        if (config.enableMultiplayerSync)
                                                HideArmorClient.broadcastConfig();
                                        rebuildWidgets();
                                }, !config.enableMultiplayerSync, "Multiplayer sync"));

                // ---- Done button ----
                this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> this.onClose())
                                .bounds(px + 8, py + DONE_TOP, 60, 20).build());

                // ---- Player preview ----
                int rightW = PANEL_W - LEFT_W;
                int previewW = rightW - 16, previewH = PANEL_H - 16;
                this.previewWidget = new PlayerPreviewWidget(
                                px + LEFT_W + 8,
                                py + 8,
                                previewW, previewH);
                this.addRenderableWidget(this.previewWidget);
        }

        private void buildNamePopup() {
                this.clearWidgets();
                sliderIcons.clear();
                glintToggles.clear();
                presetTooltips.clear();

                int cx = this.width / 2;
                int cy = this.height / 2;
                int popupW = 200;
                int popupH = 80;
                int popupX = cx - popupW / 2;
                int popupY = cy - popupH / 2;

                nameInput = new EditBox(this.font, popupX + 10, popupY + 25, popupW - 20, 18,
                                Component.literal(""));
                nameInput.setMaxLength(20);
                nameInput.setFocused(true);
                this.addWidget(nameInput);

                confirmBtn = Button.builder(Component.literal("OK"), b -> {
                        String name = nameInput.getValue().trim();
                        if (name.isEmpty()) name = "Preset";
                        ModConfig config = HideArmorMod.getConfig();
                        config.presets.add(ModConfig.Preset.fromConfig(name, config));
                        showingNamePopup = false;
                        rebuildWidgets();
                }).bounds(popupX + popupW - 120, popupY + popupH - 28, 50, 20).build();

                cancelBtn = Button.builder(CommonComponents.GUI_CANCEL, b -> {
                        showingNamePopup = false;
                        rebuildWidgets();
                }).bounds(popupX + 10, popupY + popupH - 28, 50, 20).build();

                this.addRenderableWidget(nameInput);
                this.addRenderableWidget(confirmBtn);
                this.addRenderableWidget(cancelBtn);
        }

        private void addGlintToggle(int x, int y, boolean currentState, Button.OnPress onPress) {
                net.minecraft.world.item.Item icon = currentState ? Items.ENCHANTED_BOOK : Items.BOOK;
                ToggleIconButton btn = new ToggleIconButton(x, y, 20, 20, icon, currentState, onPress, !currentState);
                this.addRenderableWidget(btn);
                glintToggles.add(new GlintToggleInfo(x, y, currentState));
        }

        private net.minecraft.client.gui.components.AbstractWidget addSlider(int sliderX, int iconX, int sliderY,
                        String key, net.minecraft.world.item.Item icon,
                        float init, java.util.function.Consumer<Double> setter) {
                OptionInstance<Double> option = new OptionInstance<>(key,
                                OptionInstance.noTooltip(),
                                (t, v) -> Component.literal(Component.translatable(key).getString().split(" ")[0] + " "
                                                + (int) (v * 100) + "%"),
                                OptionInstance.UnitDouble.INSTANCE, (double) init, (val) -> setter.accept(val));
                net.minecraft.client.gui.components.AbstractWidget widget = option
                                .createButton(minecraft.options, sliderX, sliderY, SLIDER_W);
                this.addRenderableWidget(widget);
                sliderIcons.add(new IconInfo(icon, iconX, sliderY + 2));
                return widget;
        }

        // ============================================================
        // Rendering
        // ============================================================

        @Override
        public void extractBackground(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
                super.extractBackground(ctx, mx, my, delta);
        }

        @Override
        public boolean isPauseScreen() {
                return false;
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
                float anim = animProgress();
                int yOff = (int) ((1f - anim) * 50f);

                if (showingNamePopup) {
                        // Draw darkened background
                        ctx.fill(0, 0, this.width, this.height, 0x80000000);
                        drawNamePopup(ctx);
                        super.extractRenderState(ctx, mx, my, delta);
                        return;
                }

                if (this.previewWidget != null) {
                        this.previewWidget.setSlideYOffset(yOff);
                }

                ctx.pose().pushMatrix();
                ctx.pose().translate(0f, (float) yOff);

                // Draw preset strip
                drawPresetStrip(ctx, anim);

                // Draw main panel
                drawPanel(ctx, anim);

                // Render all children
                super.extractRenderState(ctx, mx, my - yOff, delta);

                // Post-child decorations
                int px = px(), py = py();
                int contentX = px + 10;
                int tabGap = 26;
                int activeTabX = (activeTab == ActiveTab.ARMOR) ? contentX : contentX + tabGap;

                // Active tab underline
                ctx.fill(activeTabX, py + 6 + 22 + 2, activeTabX + 22, py + 6 + 22 + 3, 0xFFFFFFFF);

                // "Visibility" label
                ctx.text(this.font, "Visibility", contentX, py + TOGGLE_TOP, 0xFF888888, false);

                // "Glint" column header
                int glintHeaderX = contentX + 20 + SLIDER_W + 6;
                ctx.text(this.font, "Glint", glintHeaderX, py + 24, 0xFF777777, false);

                // Item icons next to sliders
                for (IconInfo info : sliderIcons) {
                        ctx.item(new ItemStack(info.item()), info.x(), info.y());
                }

                // Preset tooltips
                for (PresetTooltipInfo info : presetTooltips) {
                        int mxT = mx, myT = my - yOff;
                        if (mxT >= info.x() && mxT < info.x() + info.w() &&
                            myT >= info.y() && myT < info.y() + info.h()) {
                                ctx.setTooltipForNextFrame(this.font,
                                                Component.literal(info.name()), mxT, myT);
                        }
                }

                ctx.pose().popMatrix();
        }

        private void drawPresetStrip(GuiGraphicsExtractor ctx, float anim) {
                int sx = presetStripX(), sy = presetStripY();
                ModConfig config = HideArmorMod.getConfig();
                boolean isSleek = "Sleek".equalsIgnoreCase(config.uiTheme);

                if (isSleek) {
                        ctx.fill(sx, sy, sx + PRESET_STRIP_W, sy + PRESET_STRIP_H, 0xFF1A1A1E);
                        // Border
                        ctx.fill(sx, sy, sx + PRESET_STRIP_W, sy + 1, 0xFFFFFFFF);
                        ctx.fill(sx, sy, sx + 1, sy + PRESET_STRIP_H, 0xFFFFFFFF);
                        ctx.fill(sx, sy + PRESET_STRIP_H - 1, sx + PRESET_STRIP_W, sy + PRESET_STRIP_H, 0xFF373737);
                        ctx.fill(sx + PRESET_STRIP_W - 1, sy, sx + PRESET_STRIP_W, sy + PRESET_STRIP_H, 0xFF373737);
                        // Inner bevel
                        ctx.fill(sx + 1, sy + 1, sx + PRESET_STRIP_W - 1, sy + 2, 0xFFC6C6C6);
                        ctx.fill(sx + 1, sy + 1, sx + 2, sy + PRESET_STRIP_H - 1, 0xFFC6C6C6);
                        ctx.fill(sx + 1, sy + PRESET_STRIP_H - 2, sx + PRESET_STRIP_W - 1, sy + PRESET_STRIP_H - 1, 0xFF8B8B8B);
                        ctx.fill(sx + PRESET_STRIP_W - 2, sy + 1, sx + PRESET_STRIP_W - 1, sy + PRESET_STRIP_H - 1, 0xFF8B8B8B);
                        // Fill
                        ctx.fill(sx + 2, sy + 2, sx + PRESET_STRIP_W - 2, sy + PRESET_STRIP_H - 2, 0xFF2D2D2D);
                } else {
                        ctx.fill(sx, sy, sx + PRESET_STRIP_W, sy + PRESET_STRIP_H, 0xFF1A1A1E);
                        int borderAlpha = (int) (anim * 200);
                        int borderColor = (borderAlpha << 24) | 0xA0A0A0;
                        ctx.fill(sx, sy, sx + PRESET_STRIP_W, sy + 1, borderColor);
                        ctx.fill(sx, sy + PRESET_STRIP_H - 1, sx + PRESET_STRIP_W, sy + PRESET_STRIP_H, borderColor);
                        ctx.fill(sx, sy, sx + 1, sy + PRESET_STRIP_H, borderColor);
                        ctx.fill(sx + PRESET_STRIP_W - 1, sy, sx + PRESET_STRIP_W, sy + PRESET_STRIP_H, borderColor);
                }

                // "Presets" label at top of strip
                ctx.text(this.font, "Presets", sx + 8, sy + 4, 0xFF888888, false);
        }

        private void drawPanel(GuiGraphicsExtractor ctx, float anim) {
                int px = px(), py = py();
                int alpha = (int) (anim * 255);
                ModConfig config = HideArmorMod.getConfig();
                boolean isSleek = "Sleek".equalsIgnoreCase(config.uiTheme);

                if (isSleek) {
                        ctx.fill(px, py, px + PANEL_W, py + PANEL_H, 0xFF1A1A1E);

                        ctx.fill(px, py, px + PANEL_W, py + 2, 0xFFFFFFFF);
                        ctx.fill(px, py, px + 2, py + PANEL_H, 0xFFFFFFFF);
                        ctx.fill(px, py + PANEL_H - 2, px + PANEL_W, py + PANEL_H, 0xFF373737);
                        ctx.fill(px + PANEL_W - 2, py, px + PANEL_W, py + PANEL_H, 0xFF373737);

                        ctx.fill(px + 2, py + 2, px + PANEL_W - 2, py + 3, 0xFFC6C6C6);
                        ctx.fill(px + 2, py + 2, px + 3, py + PANEL_H - 2, 0xFFC6C6C6);
                        ctx.fill(px + 2, py + PANEL_H - 3, px + PANEL_W - 2, py + PANEL_H - 2, 0xFF8B8B8B);
                        ctx.fill(px + PANEL_W - 3, py + 2, px + PANEL_W - 2, py + PANEL_H - 2, 0xFF8B8B8B);

                        ctx.fill(px + 3, py + 3, px + PANEL_W - 3, py + PANEL_H - 3, 0xFF2D2D2D);

                        // Horizontal separator under tabs
                        ctx.fill(px + 6, py + 34, px + LEFT_W - 6, py + 35, 0xFF4A4A54);

                        // Inset backgrounds for slider rows
                        int sliderY = py + SLIDER_TOP;
                        int sliderX = px + 10 + 20;
                        for (int i = 0; i < 4; i++) {
                                int rowY = sliderY + SPACING * i;
                                ctx.fill(sliderX - 2, rowY - 1, sliderX + SLIDER_W + 2, rowY + 21, 0xFF191919);
                                ctx.fill(sliderX - 1, rowY, sliderX + SLIDER_W + 1, rowY + 20, 0xFF222222);
                        }

                        // Horizontal separator above visibility section
                        ctx.fill(px + 6, py + TOGGLE_TOP - 5, px + LEFT_W - 6, py + TOGGLE_TOP - 4, 0xFF4A4A54);

                        // Vertical divider
                        ctx.fill(px + LEFT_W - 1, py + 4, px + LEFT_W, py + PANEL_H - 4, 0xFF191919);
                        ctx.fill(px + LEFT_W, py + 4, px + LEFT_W + 1, py + PANEL_H - 4, 0xFF4A4A54);
                } else {
                        int tileSize = 64;
                        for (int ty = 0; ty < PANEL_H; ty += tileSize) {
                                for (int tx = 0; tx < PANEL_W; tx += tileSize) {
                                        int tw = Math.min(tileSize, PANEL_W - tx);
                                        int th = Math.min(tileSize, PANEL_H - ty);
                                        ctx.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BG_TEXTURE, px + tx, py + ty, 0.0f, 0.0f, tw, th, tileSize, tileSize, 0xFFFFFFFF);
                                }
                        }

                        int borderAlpha = (int) (anim * 200);
                        int borderColor = (borderAlpha << 24) | 0xA0A0A0;
                        ctx.fill(px, py, px + PANEL_W, py + 1, borderColor);
                        ctx.fill(px, py + PANEL_H - 1, px + PANEL_W, py + PANEL_H, borderColor);
                        ctx.fill(px, py, px + 1, py + PANEL_H, borderColor);
                        ctx.fill(px + PANEL_W - 1, py, px + PANEL_W, py + PANEL_H, borderColor);

                        ctx.fill(px + LEFT_W, py + 5, px + LEFT_W + 1, py + PANEL_H - 5, 0xFF404050);
                }

                if (alpha < 255) {
                        ctx.fill(px, py, px + PANEL_W, py + PANEL_H, ((255 - alpha) << 24) | 0x000000);
                }
        }

        private void drawNamePopup(GuiGraphicsExtractor ctx) {
                int cx = this.width / 2;
                int cy = this.height / 2;
                int popupW = 200;
                int popupH = 80;
                int popupX = cx - popupW / 2;
                int popupY = cy - popupH / 2;

                // Popup background
                ctx.fill(popupX, popupY, popupX + popupW, popupY + popupH, 0xFF1A1A1E);
                ctx.fill(popupX, popupY, popupX + popupW, popupY + 1, 0xFFFFFFFF);
                ctx.fill(popupX, popupY, popupX + 1, popupY + popupH, 0xFFFFFFFF);
                ctx.fill(popupX, popupY + popupH - 1, popupX + popupW, popupY + popupH, 0xFF373737);
                ctx.fill(popupX + popupW - 1, popupY, popupX + popupW, popupY + popupH, 0xFF373737);

                // Title
                ctx.text(this.font, "Name Preset", popupX + 10, popupY + 8, 0xFFCCCCCC, false);
        }

        @Override
        public void onClose() {
                HideArmorMod.getConfig().save();
                HideArmorClient.broadcastConfig();
                super.onClose();
        }

        // ============================================================
        // Preset Button
        // ============================================================
        private class PresetButton extends Button {
                private final String label;
                private final boolean canDelete;
                private final boolean showDeleteHint;

                public PresetButton(int x, int y, int w, int h, String label, String presetName,
                                boolean canDelete, boolean showDeleteHint, OnPress onPress) {
                        super(x, y, w, h, Component.nullToEmpty(label), onPress, DEFAULT_NARRATION);
                        this.label = label;
                        this.canDelete = canDelete;
                        this.showDeleteHint = showDeleteHint;
                }

                @Override
                protected void extractContents(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
                        int bgColor = isHoveredOrFocused() ? 0xFF555555 : 0xFF333333;
                        ctx.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), bgColor);

                        int borderColor = isHoveredOrFocused() ? 0xFFAAAAAA : 0xFF666666;
                        ctx.fill(getX(), getY(), getX() + getWidth(), getY() + 1, borderColor);
                        ctx.fill(getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight(), borderColor);
                        ctx.fill(getX(), getY(), getX() + 1, getY() + getHeight(), borderColor);
                        ctx.fill(getX() + getWidth() - 1, getY(), getX() + getWidth(), getY() + getHeight(), borderColor);

                        int textW = HideArmorScreen.this.font.width(label);
                        int textX = getX() + (getWidth() - textW) / 2;
                        int textY = getY() + (getHeight() - 8) / 2;
                        ctx.text(HideArmorScreen.this.font, label, textX, textY, 0xFFCCCCCC, false);

                        // Red dot hint for deletable presets
                        if (showDeleteHint && isHoveredOrFocused()) {
                                int dotX = getX() + getWidth() - 5;
                                int dotY = getY() + 2;
                                ctx.fill(dotX, dotY, dotX + 2, dotY + 2, 0xFFFF4444);
                        }
                }

                @Override
                public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean hasFocused) {
                        if (this.active && this.visible && this.isHoveredOrFocused()) {
                                int btn = event.buttonInfo().button();
                                if (btn == 1 && canDelete) {
                                        ModConfig config = HideArmorMod.getConfig();
                                        int idx = getDeleteablePresetIndex();
                                        if (idx >= 0) {
                                                config.presets.remove(idx);
                                                rebuildWidgets();
                                        }
                                        return true;
                                }
                        }
                        return super.mouseClicked(event, hasFocused);
                }

                private int getDeleteablePresetIndex() {
                        ModConfig config = HideArmorMod.getConfig();
                        // Find which deletable preset this button represents
                        int deletableCount = 0;
                        for (int i = 0; i < config.presets.size(); i++) {
                                if (!config.presets.get(i).isDefault()) {
                                        // Match by label text (the number)
                                        if (this.label.equals(String.valueOf(i + 1))) {
                                                return i;
                                        }
                                        deletableCount++;
                                }
                        }
                        return -1;
                }
        }

        // ============================================================
        // Toggle Icon Button
        // ============================================================
        private class ToggleIconButton extends Button {
                private final net.minecraft.world.item.Item item;
                private final boolean showCross;

                public ToggleIconButton(int x, int y, int w, int h, net.minecraft.world.item.Item item,
                                boolean active, OnPress onPress, boolean showCross) {
                        super(x, y, w, h, net.minecraft.network.chat.Component.nullToEmpty(""), onPress, DEFAULT_NARRATION);
                        this.item = item;
                        this.showCross = showCross;
                }

                @Override
                protected void extractContents(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
                        ctx.item(new ItemStack(item), getX() + (getWidth() - 16) / 2,
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
        // Tooltip Toggle Icon Button
        // ============================================================
        private class TooltipToggleIconButton extends ToggleIconButton {
                private final String tooltipText;

                public TooltipToggleIconButton(int x, int y, int w, int h, net.minecraft.world.item.Item item,
                                boolean active, OnPress onPress, boolean showCross, String tooltipText) {
                        super(x, y, w, h, item, active, onPress, showCross);
                        this.tooltipText = tooltipText;
                }

                @Override
                protected void extractContents(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
                        super.extractContents(ctx, mx, my, delta);
                        if (isHovered()) {
                                ctx.setTooltipForNextFrame(HideArmorScreen.this.font,
                                                net.minecraft.network.chat.Component.literal(tooltipText), mx, my);
                        }
                }
        }
}
