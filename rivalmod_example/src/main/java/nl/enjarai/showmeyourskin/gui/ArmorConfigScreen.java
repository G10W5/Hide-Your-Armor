package nl.enjarai.showmeyourskin.gui;

import io.wispforest.owo.braid.core.Alignment;
import io.wispforest.owo.braid.core.BraidScreen;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Align;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import nl.enjarai.showmeyourskin.Components;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ArmorConfigScreen extends BraidScreen {
  private final Screen parent;

  public ArmorConfigScreen(Widget widget, Screen parent) {
    super(new Align(
      Alignment.CENTER,
      widget
    ));
    this.parent = parent;
  }

  @Override
  public void onClose() {
    this.minecraft.setScreen(this.parent);
  }

  public static ArmorConfigScreen createConfigScreen(@Nullable Screen parent) {
    var player = Minecraft.getInstance().player;
    if (ShowMeYourSkin.NET_CHANNEL.canSendToServer() && player != null) {
      var component = player.getComponent(Components.ARMOR_CONFIG);
      return new ArmorConfigScreen(
        new SyncedArmorConfigWidget(
          new PlayerOption(component.getConfig(), player.getUUID(), player, Component.translatable("gui.showmeyourskin.armorScreen.synced"))
        ), parent
      );
    } else {
      return createLocalConfigScreen(parent);
    }
  }

  public static ArmorConfigScreen createLocalConfigScreen(@Nullable Screen parent) {
    var player = Minecraft.getInstance().player;
    var storedOptions = ModConfig.INSTANCE.overrides.entrySet().stream()
      .map(entry -> new PlayerOption(
        entry.getValue().config(),
        entry.getKey(),
        null,
        Component.literal(entry.getValue().title())
      ))
      .toList();

    if (player != null) {
      //noinspection resource
      var onlineOptions = new ArrayList<>(
        player.connection.getListedOnlinePlayers().stream()
          .map(info -> new PlayerOption(
            ModConfig.INSTANCE.overrides.containsKey(info.getProfile().id()) ?
              ModConfig.INSTANCE.overrides.get(info.getProfile().id()).config() :
              new ArmorConfig(),
            info.getProfile().id(),
            (AbstractClientPlayer) player.level().getPlayerByUUID(info.getProfile().id()),
            Component.literal(info.getProfile().name())
          ))
          .toList()
      );
      // Add all stored options that aren't also in the online list
      onlineOptions.addAll(storedOptions.stream()
        .filter(o -> onlineOptions.stream()
          .noneMatch(o2 -> o2.uuid().equals(o.uuid()))).toList());

      return new ArmorConfigScreen(
        new LocalArmorConfigWidget(
          onlineOptions
        ),
        parent
      );
    } else {
      return new ArmorConfigScreen(
        new LocalArmorConfigWidget(
          storedOptions
        ),
        parent
      );
    }
  }
}
