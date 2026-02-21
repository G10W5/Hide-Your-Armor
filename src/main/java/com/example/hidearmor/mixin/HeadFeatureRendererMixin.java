package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.example.hidearmor.LocalPlayerTracker;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeadFeatureRenderer.class)
public class HeadFeatureRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/LivingEntityRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    private void onRenderHead(MatrixStack matrices, OrderedRenderCommandQueue queue, int light,
            LivingEntityRenderState state, float limbAngle, float limbDistance, CallbackInfo ci) {
        // Only hide skull/block helmets for the local player
        if (LocalPlayerTracker.isRenderingLocalPlayer() && !HideArmorMod.isSkullsAndBlocksVisible()) {
            ci.cancel();
        }
    }
}
