package com.example.hidearmor.neoforge;

import com.example.hidearmor.HideArmorMod;
import com.example.hidearmor.PlayerConfigCache;
import com.example.hidearmor.PlayerConfigPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod("hidearmor")
public class HideArmorNeoForge {
    public HideArmorNeoForge(IEventBus modEventBus) {
        HideArmorMod.init(FMLPaths.CONFIGDIR.get());

        modEventBus.addListener(this::onRegisterPayloads);

        // Client-only init — guarded via reflection so dedicated servers never
        // load client classes (NeoForgeClientHandler references Minecraft/Screen).
        if (isClient()) {
            try {
                Class<?> cl = Class.forName("com.example.hidearmor.neoforge.HideArmorNeoForgeClient");
                cl.getMethod("init", IEventBus.class).invoke(null, modEventBus);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean isClient() {
        try {
            Class.forName("net.minecraft.client.gui.screens.Screen", false,
                    HideArmorNeoForge.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        // Channel must be optional so clients can join vanilla servers and
        // dedicated servers can run without the mod.
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToServer(PlayerConfigPayload.ID, PlayerConfigPayload.CODEC, this::handlePayload);
        registrar.playToClient(PlayerConfigPayload.ID, PlayerConfigPayload.CODEC, this::handleClientPayload);
    }

    private void handleClientPayload(PlayerConfigPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> PlayerConfigCache.set(payload.playerUuid(), payload.toConfig()));
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
