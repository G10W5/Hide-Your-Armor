package com.example.hidearmor.fabric;

import com.example.hidearmor.HideArmorMod;
import com.example.hidearmor.PlayerConfigPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class HideArmorFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HideArmorMod.init(net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir());

        PayloadTypeRegistry.serverboundPlay().register(PlayerConfigPayload.ID, PlayerConfigPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PlayerConfigPayload.ID, PlayerConfigPayload.CODEC);

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ServerPlayNetworking.registerGlobalReceiver(PlayerConfigPayload.ID, (payload, context) -> {
                context.server().getPlayerList().getPlayers().forEach(player -> {
                    if (!player.getUUID().equals(payload.playerUuid())) {
                        ServerPlayNetworking.send(player, payload);
                    }
                });
            });
        });
    }
}
