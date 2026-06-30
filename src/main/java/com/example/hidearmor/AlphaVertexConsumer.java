package com.example.hidearmor;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class AlphaVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final float alphaScale;

    public AlphaVertexConsumer(VertexConsumer delegate, float alphaScale) {
        this.delegate = delegate;
        this.alphaScale = alphaScale;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        delegate.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        delegate.setColor(red, green, blue, Math.max(0, Math.min(255, (int) (alpha * alphaScale))));
        return this;
    }

    @Override
    public VertexConsumer setColor(int argb) {
        int alpha = (argb >> 24) & 0xFF;
        int newAlpha = Math.max(0, Math.min(255, (int) (alpha * alphaScale)));
        delegate.setColor((argb & 0x00FFFFFF) | (newAlpha << 24));
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        delegate.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        delegate.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        delegate.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        delegate.setNormal(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float lineWidth) {
        delegate.setLineWidth(lineWidth);
        return this;
    }
}
