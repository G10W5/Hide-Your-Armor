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
    private static int syncTickCounter = 0;

    @Override
    public void onInitializeClient() {
        // --- Client-side receiver: store incoming configs in cache ---
        ClientPlayNetworking.registerGlobalReceiver(PlayerConfigPayload.ID, (payload, context) -> {
            PlayerConfigCache.set(payload.playerUuid(), payload.toConfig());
        });

        // --- Clear cache on disconnect ---
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            PlayerConfigCache.clear();
            syncTickCounter = 0;
        });

        // --- Keybind ---
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.hidearmor.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath("hidearmor", "main"))));
        // --- Keybind listener + periodic sync broadcast ---
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                if (client.player != null) {
                    if (client.gui.screen() instanceof HideArmorScreen screen) {
                        screen.onClose();
                    } else if (client.gui.screen() == null) {
                        client.gui.setScreen(new HideArmorScreen(null));
                    }
                }
            }

            // Periodic broadcast every 100 ticks (5 seconds) to sync with late joiners
            if (client.player != null && client.level != null) {
                syncTickCounter++;
                if (syncTickCounter >= 100) {
                    syncTickCounter = 0;
                    broadcastConfig();
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
