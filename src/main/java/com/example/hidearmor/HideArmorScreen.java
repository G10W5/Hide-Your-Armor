package com.example.hidearmor;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class HideArmorScreen extends Screen {
    private final ModConfig config;

    public HideArmorScreen(Text title) {
        super(title);
        this.config = HideArmorMod.getConfig();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Left side buttons (Helmet, Chestplate)
        this.addDrawableChild(ButtonWidget.builder(getToggleText("helmet", config.helmet), (button) -> {
            config.helmet = !config.helmet;
            button.setMessage(getToggleText("helmet", config.helmet));
            config.save();
        }).dimensions(centerX - 140, centerY - 40, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(getToggleText("chestplate", config.chestplate), (button) -> {
            config.chestplate = !config.chestplate;
            button.setMessage(getToggleText("chestplate", config.chestplate));
            config.save();
        }).dimensions(centerX - 140, centerY - 10, 100, 20).build());

        // Right side buttons (Leggings, Boots)
        this.addDrawableChild(ButtonWidget.builder(getToggleText("leggings", config.leggings), (button) -> {
            config.leggings = !config.leggings;
            button.setMessage(getToggleText("leggings", config.leggings));
            config.save();
        }).dimensions(centerX + 40, centerY - 40, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(getToggleText("boots", config.boots), (button) -> {
            config.boots = !config.boots;
            button.setMessage(getToggleText("boots", config.boots));
            config.save();
        }).dimensions(centerX + 40, centerY - 10, 100, 20).build());

        // Shield button (Bottom Right)
        this.addDrawableChild(ButtonWidget.builder(getToggleText("shield", config.shield), (button) -> {
            config.shield = !config.shield;
            button.setMessage(getToggleText("shield", config.shield));
            config.save();
        }).dimensions(centerX + 40, centerY + 20, 100, 20).build());

        // Close button (Bottom Center)
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), (button) -> {
            this.close();
        }).dimensions(centerX - 50, centerY + 65, 100, 20).build());
    }

    private Text getToggleText(String key, boolean visible) {
        String status = visible ? "visible" : "hidden";
        return Text.translatable("gui.hidearmor." + key).append(": ")
                .append(Text.translatable("gui.hidearmor." + status));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render semi-transparent background
        context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);

        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        if (this.client != null && this.client.player != null) {
            int centerX = this.width / 2;
            int centerY = this.height / 2;

            // Render player (scaled and centered)
            InventoryScreen.drawEntity(context, centerX - 30, centerY - 60, centerX + 30, centerY + 40, 45, 0.0625f,
                    mouseX, mouseY, this.client.player);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
