package com.example.hidearmor.neoforge;

import com.example.hidearmor.HideArmorMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Client-only initialization. This class is only ever loaded on the CLIENT
 * (guarded by FMLEnvironment.dist check in HideArmorNeoForge) so it can
 * safely reference client-only classes like Minecraft, HideArmorScreen,
 * KeyMapping via NeoForgeClientHandler.
 */
public final class HideArmorNeoForgeClient {
    private HideArmorNeoForgeClient() {}

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(NeoForgeClientHandler::registerKeyMappings);
        NeoForge.EVENT_BUS.register(new NeoForgeClientHandler());
        HideArmorMod.setBroadcastCallback(NeoForgeClientHandler::broadcastConfig);
    }
}
