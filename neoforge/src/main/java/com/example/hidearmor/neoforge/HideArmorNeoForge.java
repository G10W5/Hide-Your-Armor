package com.example.hidearmor.neoforge;

import com.example.hidearmor.HideArmorMod;
import com.example.hidearmor.PlayerConfigPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod("hidearmor")
public class HideArmorNeoForge {
    public HideArmorNeoForge(IEventBus modEventBus) {
        HideArmorMod.init(FMLPaths.CONFIGDIR.get());

        modEventBus.addListener(this::onRegisterPayloads);
        modEventBus.addListener(NeoForgeClientHandler::registerKeyMappings);
        NeoForge.EVENT_BUS.register(new NeoForgeClientHandler());

        HideArmorMod.setBroadcastCallback(NeoForgeClientHandler::broadcastConfig);
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("hidearmor:1");
        registrar.playToServer(PlayerConfigPayload.ID, PlayerConfigPayload.CODEC, this::handlePayload);
    }

    private void handlePayload(PlayerConfigPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().level().getServer().getPlayerList().getPlayers().forEach(player -> {
                if (!player.getUUID().equals(payload.playerUuid())) {
                    player.connection.send(payload);
                }
            });
        });
    }
}
