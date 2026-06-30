package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.world.entity.ElytraAnimationState;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
  @Accessor
  EntityEquipment getEquipment();

  @Accessor
  @Mutable
  void setEquipment(EntityEquipment equipment);

  @Accessor
  ElytraAnimationState getElytraAnimationState();

  @Accessor
  @Mutable
  void setElytraAnimationState(ElytraAnimationState elytraAnimationState);
}
