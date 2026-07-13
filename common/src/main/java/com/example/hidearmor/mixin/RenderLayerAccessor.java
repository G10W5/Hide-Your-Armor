package com.example.hidearmor.mixin;

import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.class)
public interface RenderLayerAccessor {
    @Accessor("state")
    net.minecraft.client.renderer.rendertype.RenderSetup getRenderSetup();
}
