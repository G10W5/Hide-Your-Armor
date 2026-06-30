package nl.enjarai.showmeyourskin.config;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.world.entity.EquipmentSlot;

public class ArmorConfig {
  public static final ArmorConfig VANILLA_VALUES = new ArmorConfig();
  public static final Endec<ArmorConfig> ENDEC = StructEndecBuilder.of(
    ArmorPieceConfig.ENDEC.fieldOf("head", c -> c.head),
    ArmorPieceConfig.ENDEC.fieldOf("chest", c -> c.chest),
    ArmorPieceConfig.ENDEC.fieldOf("legs", c -> c.legs),
    ArmorPieceConfig.ENDEC.fieldOf("feet", c -> c.feet),
    PieceConfig.ENDEC.fieldOf("elytra", c -> c.elytra),
    PieceConfig.ENDEC.fieldOf("shield", c -> c.shield),
    PieceConfig.ENDEC.fieldOf("hat", c -> c.hat),
    Endec.BOOLEAN.fieldOf("show_in_combat", c -> c.showInCombat),
    Endec.BOOLEAN.fieldOf("show_name_tag", c -> c.showNameTag),
    Endec.BOOLEAN.fieldOf("force_elytra_when_flying", c -> c.forceElytraWhenFlying),
    ArmorConfig::new
  );

  public ArmorPieceConfig head = new ArmorPieceConfig();
  public ArmorPieceConfig chest = new ArmorPieceConfig();
  public ArmorPieceConfig legs = new ArmorPieceConfig();
  public ArmorPieceConfig feet = new ArmorPieceConfig();
  public PieceConfig elytra = new PieceConfig();
  public PieceConfig shield = new PieceConfig();
  public PieceConfig hat = new PieceConfig();
  public boolean showInCombat = true;
  public boolean showNameTag = true;
  public boolean forceElytraWhenFlying = true;

  public ArmorConfig() {
  }

  public ArmorConfig(ArmorPieceConfig head, ArmorPieceConfig chest, ArmorPieceConfig legs, ArmorPieceConfig feet, PieceConfig elytra, PieceConfig shield, PieceConfig hat, boolean showInCombat, boolean showNameTag, boolean forceElytraWhenFlying) {
    this.head = head;
    this.chest = chest;
    this.legs = legs;
    this.feet = feet;
    this.elytra = elytra;
    this.shield = shield;
    this.hat = hat;
    this.showInCombat = showInCombat;
    this.showNameTag = showNameTag;
    this.forceElytraWhenFlying = forceElytraWhenFlying;
  }

  public ArmorPieceConfig getEquipmentConfig(EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> head;
      case CHEST -> chest;
      case LEGS -> legs;
      case FEET -> feet;
      default -> throw new IllegalArgumentException("Unexpected value: " + slot);
    };
  }

  public void ensureValid() {
  }

  public ArmorConfig copy() {
    return new ArmorConfig(head.copy(), chest.copy(), legs.copy(), feet.copy(), elytra.copy(), shield.copy(), hat.copy(), showInCombat, showNameTag, forceElytraWhenFlying);
  }

  public void clear() {
    head = new ArmorPieceConfig();
    chest = new ArmorPieceConfig();
    legs = new ArmorPieceConfig();
    feet = new ArmorPieceConfig();
    elytra = new PieceConfig();
    shield = new PieceConfig();
    hat = new PieceConfig();
    showInCombat = true;
    showNameTag = true;
    forceElytraWhenFlying = true;
  }

  public static class ArmorPieceConfig extends PieceConfig {
    public static final Endec<ArmorPieceConfig> ENDEC = StructEndecBuilder.of(
      Endec.BOOLEAN.fieldOf("base", c -> c.base),
      Endec.BOOLEAN.fieldOf("glint", c -> c.glint),
      Endec.BOOLEAN.fieldOf("trim", c -> c.trim),
      ArmorPieceConfig::new
    );
    public static final ArmorPieceConfig VANILLA_VALUES = new ArmorPieceConfig();

    public boolean trim = true;

    public ArmorPieceConfig(boolean base, boolean glint, boolean trim) {
      super(base, glint);
      this.trim = trim;
    }

    public ArmorPieceConfig() {
    }

    public void setAll(boolean value) {
      this.base = value;
      this.trim = value;
      this.glint = value;
    }

    @Override
    public boolean trim() {
      return trim;
    }

    public ArmorPieceConfig copy() {
      return new ArmorPieceConfig(base, glint, trim);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ArmorPieceConfig that)) return false;

      return base == that.base && trim == that.trim && glint == that.glint;
    }

    @Override
    public int hashCode() {
      int result = Boolean.hashCode(base);
      result = 31 * result + Boolean.hashCode(trim);
      result = 31 * result + Boolean.hashCode(glint);
      return result;
    }
  }

  public static class PieceConfig {
    public static final Endec<PieceConfig> ENDEC = StructEndecBuilder.of(
      Endec.BOOLEAN.fieldOf("base", c -> c.base),
      Endec.BOOLEAN.fieldOf("glint", c -> c.glint),
      PieceConfig::new
    );
    public static final PieceConfig VANILLA_VALUES = new PieceConfig();

    public boolean base = true;
    public boolean glint = true;

    public PieceConfig(boolean base, boolean glint) {
      this.base = base;
      this.glint = glint;
    }

    public PieceConfig() {
    }

    public void setAll(boolean value) {
      this.base = value;
      this.glint = value;
    }

    public boolean base() {
      return base;
    }

    public boolean trim() {
      return true;
    }

    public boolean glint() {
      return glint;
    }

    public PieceConfig copy() {
      return new PieceConfig(base, glint);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof PieceConfig that)) return false;

      return base == that.base && glint == that.glint;
    }

    @Override
    public int hashCode() {
      int result = Boolean.hashCode(base);
      result = 31 * result + Boolean.hashCode(glint);
      return result;
    }
  }
}
