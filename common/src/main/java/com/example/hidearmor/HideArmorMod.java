package com.example.hidearmor;

import java.io.File;
import java.nio.file.Path;

/**
 * Common mod class — no Fabric or NeoForge imports.
 * Platform-specific config directory is injected via init().
 */
public class HideArmorMod {
    private static ModConfig config;
    private static File configDir;

    public static final String MOD_ID = "hidearmor";

    public static void init(Path configDirPath) {
        configDir = configDirPath.toFile();
        config = ModConfig.load(configDir);
    }

    public static ModConfig getConfig() {
        return config;
    }

    public static File getConfigDir() {
        return configDir;
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

    public static boolean isSkullsAndBlocksVisible() {
        return config == null || config.showSkullsAndBlocks;
    }

    private static Runnable broadcastCallback;

    public static void setBroadcastCallback(Runnable callback) {
        broadcastCallback = callback;
    }

    public static void broadcastConfig() {
        if (broadcastCallback != null) {
            broadcastCallback.run();
        }
    }

    public static volatile boolean isRenderingLocalShield = false;
    public static volatile boolean isFirstPersonShield = false;
    public static volatile boolean isChestplateFullyHidden = false;
}
