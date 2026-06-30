package nl.enjarai.showmeyourskin.util;

import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.ModConfigServer;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class ArmorConfigComponent implements ComponentV3, AutoSyncedComponent {
  private static final KeyedEndec<ArmorConfig> KEYED_CONFIG = ArmorConfig.ENDEC.keyed("config", ArmorConfig.VANILLA_VALUES);

  private ArmorConfig config;

  public ArmorConfigComponent(ArmorConfig config) {
    this.config = config;
  }

  @Override
  public void readData(ValueInput readView) {
    setConfig(readView.get(KEYED_CONFIG));
  }

  @Override
  public void writeData(ValueOutput writeView) {
    writeView.put(KEYED_CONFIG, config);
  }

  public ArmorConfig getConfig() {
    return config;
  }

  public void setConfig(ArmorConfig config) {
    this.config = config;
  }

  public void ensureValid() {
    if (!ModConfigServer.INSTANCE.synced.allowNotShowInCombat()) getConfig().showInCombat = true;
    if (!ModConfigServer.INSTANCE.synced.allowNotShowNameTag()) getConfig().showNameTag = true;
    if (!ModConfigServer.INSTANCE.synced.allowNotForceElytraWhenFlying()) getConfig().forceElytraWhenFlying = true;
  }

  @Override
  public boolean shouldSyncWith(ServerPlayer player) {
    return ShowMeYourSkin.NET_CHANNEL.canSendToPlayer(player);
  }

  @Override
  public boolean isRequiredOnClient() {
    return false;
  }
}
