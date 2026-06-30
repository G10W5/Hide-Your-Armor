package nl.enjarai.showmeyourskin.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.gui.ArmorConfigScreen;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
  public static KeyMapping OPEN_SETTINGS;
  public static KeyMapping GLOBAL_TOGGLE;

  private static final KeyMapping.Category KEYBIND_CATEGORY = new KeyMapping.Category(Identifier.withDefaultNamespace("category.showmeyourskin.showmeyourskin"));

  public static void register() {
    OPEN_SETTINGS = KeyMappingHelper.registerKeyMapping(new KeyMapping(
      "Open Settings",
      InputConstants.Type.KEYSYM,
      GLFW.GLFW_KEY_K,
      KEYBIND_CATEGORY
    ));

    GLOBAL_TOGGLE = KeyMappingHelper.registerKeyMapping(new KeyMapping(
      "Global Toggle",
      InputConstants.Type.KEYSYM,
      GLFW.GLFW_KEY_J,
      KEYBIND_CATEGORY
    ));
  }

  public static void tick(Minecraft client) {
    while (OPEN_SETTINGS.consumeClick()) {
      client.setScreen(ArmorConfigScreen.createConfigScreen(null));
    }
    while (GLOBAL_TOGGLE.consumeClick()) {
      ModConfig.INSTANCE.globalEnabled = !ModConfig.INSTANCE.globalEnabled;
      ModConfig.INSTANCE.save();
      if (client.player != null) {
        client.player.sendOverlayMessage(
          Component.translatable(
            "key.showmeyourskin." +
              (ModConfig.INSTANCE.globalEnabled ? "global_toggle.enable" : "global_toggle.disable")
          )
        );
      }
    }
  }
}
