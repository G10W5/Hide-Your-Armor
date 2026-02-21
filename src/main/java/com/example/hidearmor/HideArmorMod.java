package com.example.hidearmor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class HideArmorMod implements ClientModInitializer {
    private static ModConfig config;
    public static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        config = ModConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hidearmor.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KeyBinding.Category.create(Identifier.of("hidearmor", "main"))));

        // Register the keybind handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                if (client.player != null) {
                    if (client.currentScreen instanceof HideArmorScreen screen) {
                        screen.close();
                    } else if (client.currentScreen == null) {
                        client.setScreen(new HideArmorScreen(null));
                    }
                }
            }
        });
    }

    public static ModConfig getConfig() {
        return config;
    }

    public static float getHelmetOpacity() {
        return config != null ? config.helmetOpacity : 1.0f;
    }

    public static float getChestplateOpacity() {
        return config != null ? config.chestplateOpacity : 1.0f;
    }

    public static float getLeggingsOpacity() {
        return config != null ? config.leggingsOpacity : 1.0f;
    }

    public static float getBootsOpacity() {
        return config != null ? config.bootsOpacity : 1.0f;
    }

    public static float getShieldOpacity() {
        return config != null ? config.shieldOpacity : 1.0f;
    }

    public static boolean isElytraVisible() {
        return config == null || config.showElytra;
    }

    public static volatile boolean isRenderingLocalShield = false;
    public static volatile boolean isFirstPersonShield = false;

    public static boolean isSkullsAndBlocksVisible() {
        return config == null || config.showSkullsAndBlocks;
    }
}
