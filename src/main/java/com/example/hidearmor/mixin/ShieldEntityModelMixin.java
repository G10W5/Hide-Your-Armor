package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.model.Model.class)
public class ShieldEntityModelMixin {
    @Inject(method = "getLayer", at = @At("RETURN"), cancellable = true)
    private void onGetLayer(Identifier texture, CallbackInfoReturnable<RenderLayer> cir) {
        if ((Object) this instanceof ShieldEntityModel && HideArmorMod.isRenderingLocalShield
                && !HideArmorMod.isFirstPersonShield) {
            float opacity = HideArmorMod.getShieldOpacity();
            if (opacity > 0.0f && opacity < 1.0f) {
                cir.setReturnValue(net.minecraft.client.render.RenderLayers.entityTranslucent(texture));
            }
        }
    }
}
