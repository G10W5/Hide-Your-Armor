package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.pond.ArmorConfigContraband;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {
    
    @Inject(
            method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void showmeyourskin$removeLabel(T livingEntity, double d, CallbackInfoReturnable<Boolean> cir) {
        if (
                !ModConfig.INSTANCE.getApplicable(livingEntity.getUUID()).showNameTag
        ) cir.setReturnValue(false);
    }
    
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("HEAD")
    )
    private <T extends LivingEntity, S extends LivingEntityRenderState> void showmeyourskin$captureEntity(T livingEntity, S livingEntityRenderState, float f, CallbackInfo ci){
//        var entity = ShowMeYourSkinClient.ENTITY_RENDER_STATE_KEY.get(livingEntityRenderState);
//        if (entity != null) {
//            IWishMixinAllowedForPublicStaticFields.currentEntity = entity;
//        }
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL")
    )
    private void showmeyourskin$updateRenderState(LivingEntity livingEntity, LivingEntityRenderState livingEntityRenderState, float f, CallbackInfo ci) {
        if (livingEntityRenderState instanceof AvatarRenderState renderState) {
            var armorConfig = ModConfig.INSTANCE.getApplicable(livingEntity.getUUID());

            if (livingEntity instanceof ArmorConfigContraband contraband && contraband.show_me_your_skin$getArmorConfig() != null) {
                armorConfig = contraband.show_me_your_skin$getArmorConfig();
            }

            renderState.setData(ShowMeYourSkinClient.ARMOR_CONFIG_KEY, armorConfig);
        }
    }
}
