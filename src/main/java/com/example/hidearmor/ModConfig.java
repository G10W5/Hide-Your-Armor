package com.example.hidearmor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(),
            "hidearmor.json");

    public float helmetOpacity = 1.0f;
    public float chestplateOpacity = 1.0f;
    public float leggingsOpacity = 1.0f;
    public float bootsOpacity = 1.0f;
    public float shieldOpacity = 1.0f;
    // Toggle visibility for special items
    public boolean showElytra = true;
    public boolean showSkullsAndBlocks = true;
    public boolean enableMultiplayerSync = true;
    
    // Enchantment Glint toggles
    public boolean showGlintHelmet = true;
    public boolean showGlintChestplate = true;
    public boolean showGlintLeggings = true;
    public boolean showGlintBoots = true;
    public boolean showGlintShield = true;

    // UI Theme
    public String uiTheme = "Sleek"; // "Cobblestone" or "Sleek"

    // Presets
    public List<Preset> presets = new ArrayList<>();

    public record Preset(
        String name,
        boolean isDefault,
        float helmetOpacity, float chestplateOpacity, float leggingsOpacity, float bootsOpacity,
        float shieldOpacity,
        boolean showElytra, boolean showSkullsAndBlocks,
        boolean showGlintHelmet, boolean showGlintChestplate, boolean showGlintLeggings, boolean showGlintBoots, boolean showGlintShield
    ) {
        public static Preset fromConfig(String name, ModConfig config) {
            return new Preset(
                name, false,
                config.helmetOpacity, config.chestplateOpacity, config.leggingsOpacity, config.bootsOpacity,
                config.shieldOpacity,
                config.showElytra, config.showSkullsAndBlocks,
                config.showGlintHelmet, config.showGlintChestplate, config.showGlintLeggings, config.showGlintBoots, config.showGlintShield
            );
        }

        public void applyTo(ModConfig config) {
            config.helmetOpacity = this.helmetOpacity;
            config.chestplateOpacity = this.chestplateOpacity;
            config.leggingsOpacity = this.leggingsOpacity;
            config.bootsOpacity = this.bootsOpacity;
            config.shieldOpacity = this.shieldOpacity;
            config.showElytra = this.showElytra;
            config.showSkullsAndBlocks = this.showSkullsAndBlocks;
            config.showGlintHelmet = this.showGlintHelmet;
            config.showGlintChestplate = this.showGlintChestplate;
            config.showGlintLeggings = this.showGlintLeggings;
            config.showGlintBoots = this.showGlintBoots;
            config.showGlintShield = this.showGlintShield;
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            System.err.println("Failed to save HideArmorMod config: " + e.getMessage());
        }
    }

    public static ModConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded.presets == null) loaded.presets = new ArrayList<>();
                if (loaded.presets.isEmpty()) loaded.initDefaultPresets();
                return loaded;
            } catch (IOException e) {
                System.err.println("Failed to load HideArmorMod config: " + e.getMessage());
            }
        }
        ModConfig def = new ModConfig();
        def.initDefaultPresets();
        return def;
    }

    private void initDefaultPresets() {
        presets.clear();
        presets.add(new Preset(
            "Full Visibility", true,
            1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
            true, true,
            true, true, true, true, true
        ));
        presets.add(new Preset(
            "Invisible", true,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            false, false,
            false, false, false, false, false
        ));
    }
}
