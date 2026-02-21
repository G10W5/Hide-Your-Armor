package com.example.hidearmor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

public class PlayerPreviewWidget extends ClickableWidget {

    public PlayerPreviewWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }

    private int slideYOffset = 0;

    public void setSlideYOffset(int slideYOffset) {
        this.slideYOffset = slideYOffset;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null)
            return;

        int renderY = this.getY() + slideYOffset;

        // Draw background
        context.fill(this.getX(), renderY, this.getX() + this.getWidth(), renderY + this.getHeight(),
                0x60000000);

        // Draw border manually
        int x = this.getX();
        int y = renderY;
        int w = this.getWidth();
        int h = this.getHeight();
        int borderColor = 0xFFC0C0C0;
        context.fill(x, y, x + w, y + 1, borderColor);
        context.fill(x, y + h - 1, x + w, y + h, borderColor);
        context.fill(x, y, x + 1, y + h, borderColor);
        context.fill(x + w - 1, y, x + w, y + h, borderColor);

        // Draw entity
        int size = Math.min(this.getWidth(), this.getHeight()) / 2;
        InventoryScreen.drawEntity(context, this.getX(), y, this.getX() + this.getWidth(),
                y + this.getHeight(), (int) (size * 0.8), 0.0625f, (float) mouseX, (float) mouseY,
                client.player);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
