package com.example.hidearmor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side cache of opacity configs received from other players via network
 * packets.
 * Used by LocalPlayerTracker to look up the correct config when rendering a
 * non-local player.
 */
public class PlayerConfigCache {

    private static final Map<UUID, ModConfig> CACHE = new ConcurrentHashMap<>();

    /** Store or update a received config for the given player. */
    public static void set(UUID uuid, ModConfig config) {
        CACHE.put(uuid, config);
    }

    /**
     * Returns the cached config for the given player, or null if not received yet.
     * Caller should fall back to a fully-visible default config if null.
     */
    public static ModConfig get(UUID uuid) {
        return CACHE.get(uuid);
    }

    /** Clear all cached configs (call on server disconnect). */
    public static void clear() {
        CACHE.clear();
    }
}
