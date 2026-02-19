package com.example.hidearmor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class HideArmorMod implements ClientModInitializer {
    private static ModConfig config;
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        config = ModConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hidearmor.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.hidearmor"));

        // Register the keybind handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleKey.wasPressed() && client.player != null) {
                client.setScreen(new HideArmorScreen(Text.translatable("gui.hidearmor.title")));
            }
        });
    }

    public static ModConfig getConfig() {
        return config;
    }

    public static boolean isHelmetHidden() {
        return config != null && !config.helmet;
    }

    public static boolean isChestplateHidden() {
        return config != null && !config.chestplate;
    }

    public static boolean isLeggingsHidden() {
        return config != null && !config.leggings;
    }

    public static boolean isBootsHidden() {
        return config != null && !config.boots;
    }

    public static boolean isShieldHidden() {
        return config != null && !config.shield;
    }
}
