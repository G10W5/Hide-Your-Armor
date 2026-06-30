package com.example.hidearmor;

import java.util.UUID;

/**
 * Tracks which player entity is currently being rendered.
 * Extended to also track UUID so opacity configs can be fetched
 * from PlayerConfigCache for non-local players in multiplayer.
 */
public class LocalPlayerTracker {
    private static volatile int localPlayerId = -1;
    private static volatile int currentlyRenderingId = -999;
    private static volatile UUID currentlyRenderingUUID = null;

    /**
     * Called on each client tick to update the local player's entity ID.
     */
    public static void setLocalPlayer(int id, UUID uuid) {
        localPlayerId = id;
    }

    /** Called by PlayerRendererMixin before rendering a player entity. */
    public static void beginRender(int entityId, UUID uuid) {
        currentlyRenderingId = entityId;
        currentlyRenderingUUID = uuid;
    }

    /** Called by PlayerRendererMixin after rendering a player entity. */
    public static void endRender() {
        currentlyRenderingId = -999;
        currentlyRenderingUUID = null;
    }

    /** Returns true if the currently rendering player is the local player. */
    public static boolean isRenderingLocalPlayer() {
        return localPlayerId != -1 && currentlyRenderingId == localPlayerId;
    }

    /**
     * Returns the correct ModConfig for the player currently being rendered.
     * - Local player → our own saved config
     * - Other player → their config from the network cache (or fully visible
     * default)
     */
    public static ModConfig getConfigForCurrentPlayer() {
        if (isRenderingLocalPlayer()) {
            return HideArmorMod.getConfig();
        }
        UUID uuid = currentlyRenderingUUID;
        if (uuid != null) {
            ModConfig cached = PlayerConfigCache.get(uuid);
            if (cached != null)
                return cached;
        }
        // No config received for this player — show them fully
        return null;
    }
}
