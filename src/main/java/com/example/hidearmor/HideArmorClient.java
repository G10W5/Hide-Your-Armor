package com.example.hidearmor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class HideArmorClient implements ClientModInitializer {
    public static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        // --- Client-side receiver: store incoming configs in cache ---
        ClientPlayNetworking.registerGlobalReceiver(PlayerConfigPayload.ID, (payload, context) -> {
            PlayerConfigCache.set(payload.playerUuid(), payload.toConfig());
        });

        // --- Clear cache on disconnect ---
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            PlayerConfigCache.clear();
        });

        // --- Keybind ---
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hidearmor.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KeyBinding.Category.create(Identifier.of("hidearmor", "main"))));

        // --- Keybind listener ---
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

    public static void broadcastConfig() {
        if (HideArmorMod.getConfig() == null || !HideArmorMod.getConfig().enableMultiplayerSync)
            return;

        var client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player == null)
            return;
        ClientPlayNetworking.send(PlayerConfigPayload.from(client.player.getUuid(), HideArmorMod.getConfig()));
    }
}
