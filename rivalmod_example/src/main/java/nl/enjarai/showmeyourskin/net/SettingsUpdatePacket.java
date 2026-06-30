package nl.enjarai.showmeyourskin.net;

import io.wispforest.owo.network.ServerAccess;
import nl.enjarai.showmeyourskin.Components;
import nl.enjarai.showmeyourskin.config.ArmorConfig;

public record SettingsUpdatePacket(ArmorConfig settings) {
  public void handleServer(ServerAccess access) {
    var component = Components.ARMOR_CONFIG.get(access.player());
    component.setConfig(settings());
    component.ensureValid();
    Components.ARMOR_CONFIG.sync(access.player());
  }
}
