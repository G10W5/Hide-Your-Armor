package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.entity.ClientMannequin;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.pond.ArmorConfigContraband;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientMannequin.class)
public class ClientMannequinMixin implements ArmorConfigContraband {
  @Unique
  private ArmorConfig armorConfig;

  @Override
  public void show_me_your_skin$setArmorConfig(ArmorConfig armorConfig) {
    this.armorConfig = armorConfig;
  }

  @Override
  public @Nullable ArmorConfig show_me_your_skin$getArmorConfig() {
    return armorConfig;
  }
}
