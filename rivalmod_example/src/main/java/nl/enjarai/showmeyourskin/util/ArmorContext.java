package nl.enjarai.showmeyourskin.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import nl.enjarai.showmeyourskin.config.HideableEquipment;

public class ArmorContext {
  private final HideableEquipment slot;
  private final LivingEntity entity;

  public ArmorContext(HideableEquipment slot, LivingEntity entity) {
    this.slot = slot;
    this.entity = entity;
  }

  public HideableEquipment getSlot() {
    return slot;
  }

  public LivingEntity getEntity() {
    return entity;
  }

  public boolean shouldModify() {
    return getEntity() instanceof Player;
  }
}
