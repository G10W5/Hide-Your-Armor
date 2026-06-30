package nl.enjarai.showmeyourskin.config;

import io.wispforest.endec.Endec;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public enum HideableEquipment {
  HEAD,
  CHEST,
  LEGS,
  FEET,
  ELYTRA,
  SHIELD,
  HAT;

  private final String id;

  HideableEquipment() {
    this.id = name().toLowerCase();
  }

  public String getId() {
    return id;
  }

  @Nullable
  public static HideableEquipment fromId(String id) {
    for (var i : values()) {
      if (i.id.equals(id)) {
        return i;
      }
    }
    return null;
  }

  @Nullable
  public static HideableEquipment fromSlot(EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> HEAD;
      case CHEST -> CHEST;
      case LEGS -> LEGS;
      case FEET -> FEET;
      default -> null;
    };
  }

  @Nullable
  public EquipmentSlot toSlot() {
    return switch (this) {
      case HEAD -> EquipmentSlot.HEAD;
      case CHEST -> EquipmentSlot.CHEST;
      case LEGS -> EquipmentSlot.LEGS;
      case FEET -> EquipmentSlot.FEET;
      default -> null;
    };
  }

  public static Endec<HideableEquipment> getEndec() {
    return Endec.STRING.xmap(HideableEquipment::fromId, HideableEquipment::getId);
  }

  public static Endec<EquipmentSlot> getSlotEndec() {
    return Endec.STRING.xmap(EquipmentSlot::byName, EquipmentSlot::getName);
  }
}
