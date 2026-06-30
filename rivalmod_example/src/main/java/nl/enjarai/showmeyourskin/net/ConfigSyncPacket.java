package nl.enjarai.showmeyourskin.net;

import io.wispforest.owo.network.ClientAccess;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.config.SyncedModConfig;

public record ConfigSyncPacket(SyncedModConfig config) {
  public void handleClient(ClientAccess access) {
    ShowMeYourSkinClient.serverConfig = config;
  }
}
