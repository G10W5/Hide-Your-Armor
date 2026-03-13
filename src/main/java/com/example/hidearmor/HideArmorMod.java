package com.example.hidearmor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class HideArmorMod implements ClientModInitializer {
    private static ModConfig config;
    public static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        // Dummy.test();

        config = ModConfig.load();

        // --- Payload registration (must happen before any networking) ---
        PayloadTypeRegistry.playC2S().register(PlayerConfigPayload.ID, PlayerConfigPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerConfigPayload.ID, PlayerConfigPayload.CODEC);

        // --- Server-side relay: when ANY client sends us their config, forward to all
        // others ---
        // This works in LAN (integrated server) and dedicated servers that have the mod
        // installed.
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ServerPlayNetworking.registerGlobalReceiver(PlayerConfigPayload.ID, (payload, context) -> {
                // Relay to all OTHER players
                context.server().getPlayerManager().getPlayerList().forEach(player -> {
                    if (!player.getUuid().equals(payload.playerUuid())) {
                        ServerPlayNetworking.send(player, payload);
                    }
                });
            });
        });

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

    /**
     * Broadcasts the local player's current config to the server (which relays to
     * other clients).
     * Call this after any config change if multiplayer sync is enabled.
     */
    public static void broadcastConfig() {
        if (config == null || !config.enableMultiplayerSync)
            return;
        var client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player == null)
            return;
        ClientPlayNetworking.send(PlayerConfigPayload.from(client.player.getUuid(), config));
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
