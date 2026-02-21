package com.example.hidearmor;

/**
 * Tracks whether the currently rendering entity is the local player.
 * Set by PlayerRendererMixin before and after each player render call.
 * Used by all transparency-related Mixins to only affect the local player.
 */
public class LocalPlayerTracker {
    private static volatile int localPlayerId = -1;
    private static volatile int currentlyRenderingId = -999;

    /** Called on each client tick to update the local player's entity ID. */
    public static void setLocalPlayerId(int id) {
        localPlayerId = id;
    }

    /** Called by PlayerRendererMixin before rendering a player entity. */
    public static void beginRender(int entityId) {
        currentlyRenderingId = entityId;
    }

    /** Called by PlayerRendererMixin after rendering a player entity. */
    public static void endRender() {
        currentlyRenderingId = -999;
    }

    /** Returns true if the currently rendering player is the local player. */
    public static boolean isRenderingLocalPlayer() {
        return localPlayerId != -1 && currentlyRenderingId == localPlayerId;
    }
}
