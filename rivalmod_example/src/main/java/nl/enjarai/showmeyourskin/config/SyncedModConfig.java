package nl.enjarai.showmeyourskin.config;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;

public record SyncedModConfig(boolean allowNotShowInCombat, boolean allowNotShowNameTag,
                              boolean allowNotForceElytraWhenFlying) {
  public static final Endec<SyncedModConfig> ENDEC = StructEndecBuilder.of(
    Endec.BOOLEAN.optionalFieldOf("allow_not_show_in_combat", SyncedModConfig::allowNotShowInCombat, true),
    Endec.BOOLEAN.optionalFieldOf("allow_not_show_name_tag", SyncedModConfig::allowNotShowNameTag, true),
    Endec.BOOLEAN.optionalFieldOf("allow_not_force_elytra_when_flying", SyncedModConfig::allowNotForceElytraWhenFlying, true),
    SyncedModConfig::new
  );
}
