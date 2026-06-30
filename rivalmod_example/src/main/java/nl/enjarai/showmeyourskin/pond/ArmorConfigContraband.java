package nl.enjarai.showmeyourskin.pond;

import nl.enjarai.showmeyourskin.config.ArmorConfig;
import org.jetbrains.annotations.Nullable;

public interface ArmorConfigContraband {
  void show_me_your_skin$setArmorConfig(ArmorConfig armorConfig);

  @Nullable ArmorConfig show_me_your_skin$getArmorConfig();
}
