package com.example.hidearmor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class HideArmorMod implements ModInitializer {
    private static ModConfig config;

    @Override
    public void onInitialize() {
        config = ModConfig.load();

        // --- Payload registration (must happen on BOTH client and server) ---
        PayloadTypeRegistry.playC2S().register(PlayerConfigPayload.ID, PlayerConfigPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerConfigPayload.ID, PlayerConfigPayload.CODEC);

        // --- Server-side relay: when ANY client sends us their config, forward to all
        // others ---
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
