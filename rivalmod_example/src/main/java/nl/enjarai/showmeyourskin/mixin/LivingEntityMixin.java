package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import nl.enjarai.showmeyourskin.util.CombatLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
  @Inject(
    method = "handleDamageEvent",
    at = @At(value = "HEAD")
  )
  private void showmeyourskin$triggerCombat(DamageSource damageSource, CallbackInfo ci) {
    if ((Object) this instanceof AbstractClientPlayer player) {
      CombatLogger.INSTANCE.triggerCombat(player.getUUID());
    }
  }
}
