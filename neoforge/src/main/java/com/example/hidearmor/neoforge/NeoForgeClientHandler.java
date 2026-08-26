package com.example.hidearmor.neoforge;

import com.example.hidearmor.HideArmorMod;
import com.example.hidearmor.HideArmorScreen;
import com.example.hidearmor.PlayerConfigPayload;
import com.example.hidearmor.PlayerConfigCache;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.lwjgl.glfw.GLFW;

public class NeoForgeClientHandler {
    public static KeyMapping toggleKey;
    private static int syncTickCounter = 0;

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        toggleKey = new KeyMapping(
                "key.hidearmor.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath("hidearmor", "main")));
        event.register(toggleKey);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (toggleKey.consumeClick()) {
            if (mc.gui.screen() instanceof HideArmorScreen screen) {
                screen.onClose();
            } else if (mc.gui.screen() == null) {
                mc.gui.setScreen(new HideArmorScreen(null));
            }
        }

        syncTickCounter++;
        if (syncTickCounter >= 100) {
            syncTickCounter = 0;
            broadcastConfig();
        }
    }

    @SubscribeEvent
    public void onPlayerDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        PlayerConfigCache.clear();
        syncTickCounter = 0;
    }

    public static void broadcastConfig() {
        if (HideArmorMod.getConfig() == null || !HideArmorMod.getConfig().enableMultiplayerSync)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.getConnection() == null) return;

        // Don't try to send to vanilla servers (e.g. Hypixel) that don't have the channel – would throw
        // UnsupportedOperationException: Payload hidearmor:sync may not be sent to the server!
        if (!net.neoforged.neoforge.network.registration.NetworkRegistry.hasChannel(
                mc.getConnection(), PlayerConfigPayload.ID.id())) {
            return;
        }

        PlayerConfigPayload payload = PlayerConfigPayload.from(mc.player.getUUID(), HideArmorMod.getConfig());
        try {
            ClientPacketDistributor.sendToServer(payload);
        } catch (UnsupportedOperationException ignored) {
            // Vanilla / non-modded server – ignore
        }
    }
}
