package com.example.hidearmor;

import net.minecraft.client.render.VertexConsumer;

public class AlphaVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final float alphaScale;

    public AlphaVertexConsumer(VertexConsumer delegate, float alphaScale) {
        this.delegate = delegate;
        this.alphaScale = alphaScale;
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z) {
        delegate.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        delegate.color(red, green, blue, Math.max(0, Math.min(255, (int) (alpha * alphaScale))));
        return this;
    }

    @Override
    public VertexConsumer color(int argb) {
        int alpha = (argb >> 24) & 0xFF;
        int newAlpha = Math.max(0, Math.min(255, (int) (alpha * alphaScale)));
        delegate.color((argb & 0x00FFFFFF) | (newAlpha << 24));
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        delegate.texture(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        delegate.overlay(u, v);
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        delegate.light(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        delegate.normal(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer lineWidth(float lineWidth) {
        delegate.lineWidth(lineWidth);
        return this;
    }
}
