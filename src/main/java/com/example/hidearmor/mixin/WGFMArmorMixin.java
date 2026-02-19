package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.wildfire.render.GenderArmorLayer", remap = false)
public class WGFMArmorMixin {

    @Inject(method = "isLayerVisible", at = @At("HEAD"), cancellable = true)
    private void onIsLayerVisible(BipedEntityRenderState state, CallbackInfoReturnable<Boolean> cir) {

        if (HideArmorMod.isChestplateHidden()) {
            cir.setReturnValue(false);
        }
    }
}
