package nl.enjarai.showmeyourskin;

import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import nl.enjarai.cicada.api.util.ProperLogger;
import nl.enjarai.showmeyourskin.config.ModConfigServer;
import nl.enjarai.showmeyourskin.net.ConfigSyncPacket;
import nl.enjarai.showmeyourskin.net.SettingsUpdatePacket;
import org.slf4j.Logger;

public class ShowMeYourSkin implements ModInitializer {
  public static final String MODID = "showmeyourskin";
  public static final Logger LOGGER = ProperLogger.getLogger(MODID);

  public static final OwoNetChannel NET_CHANNEL = OwoNetChannel.createOptional(id("networking"));

  @Override
  public void onInitialize() {
    ModConfigServer.load();

    initNetworking();
  }

  // TODO:
  // - example armors
  // - use nearby players when available

  private static void initNetworking() {
    NET_CHANNEL.registerClientbound(ConfigSyncPacket.class, ConfigSyncPacket::handleClient);
    NET_CHANNEL.registerServerbound(SettingsUpdatePacket.class, SettingsUpdatePacket::handleServer);

    ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
      if (NET_CHANNEL.canSendToPlayer(handler)) {
        NET_CHANNEL.serverHandle(handler.player).send(new ConfigSyncPacket(ModConfigServer.INSTANCE.synced));
      }

      handler.getPlayer().getComponent(Components.ARMOR_CONFIG).ensureValid();
    });
  }

  public static Identifier id(String path) {
    return Identifier.fromNamespaceAndPath(MODID, path);
  }
}
