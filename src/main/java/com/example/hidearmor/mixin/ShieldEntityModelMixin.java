package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import net.minecraft.client.model.object.equipment.ShieldModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.model.Model.class)
public class ShieldEntityModelMixin {
    // Target the overload that takes an Identifier and returns RenderType directly
    @Inject(method = "renderType(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;",
            at = @At("RETURN"), cancellable = true)
    private void onGetLayer(Identifier texture, CallbackInfoReturnable<RenderType> cir) {
        if ((Object) this instanceof ShieldModel && HideArmorMod.isRenderingLocalShield
                && !HideArmorMod.isFirstPersonShield) {
            float opacity = HideArmorMod.getShieldOpacity();
            if (opacity > 0.0f && opacity < 1.0f) {
                cir.setReturnValue(net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture));
            }
        }
    }
}
