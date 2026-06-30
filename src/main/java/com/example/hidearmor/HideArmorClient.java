package com.example.hidearmor;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class HideArmorClient implements ClientModInitializer {
    public static KeyMapping toggleKey;

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
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.hidearmor.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath("hidearmor", "main"))));
        // --- Keybind listener ---
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                if (client.player != null) {
                    if (client.screen instanceof HideArmorScreen screen) {
                        screen.onClose();
                    } else if (client.screen == null) {
                        client.setScreen(new HideArmorScreen(null));
                    }
                }
            }
        });
    }

    public static void broadcastConfig() {
        if (HideArmorMod.getConfig() == null || !HideArmorMod.getConfig().enableMultiplayerSync)
            return;

        var client = net.minecraft.client.Minecraft.getInstance();
        if (client.player == null)
            return;
        ClientPlayNetworking.send(PlayerConfigPayload.from(client.player.getUUID(), HideArmorMod.getConfig()));
    }
}
