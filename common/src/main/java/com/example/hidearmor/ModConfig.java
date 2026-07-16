package com.example.hidearmor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(ModConfig.class, new ModConfigDeserializer())
        .create();

    public float helmetOpacity = 1.0f;
    public float chestplateOpacity = 1.0f;
    public float leggingsOpacity = 1.0f;
    public float bootsOpacity = 1.0f;
    public float shieldOpacity = 1.0f;
    public float elytraOpacity = 1.0f;
    public float skullsAndBlocksOpacity = 1.0f;
    public float capeOpacity = 1.0f;
    public float trimOpacity = 1.0f;
    public boolean enableMultiplayerSync = true;

    public boolean showGlintHelmet = true;
    public boolean showGlintChestplate = true;
    public boolean showGlintLeggings = true;
    public boolean showGlintBoots = true;
    public boolean showGlintShield = true;

    public String uiTheme = "Sleek";

    public List<Preset> presets = new ArrayList<>();

    public record Preset(
        String name,
        boolean isDefault,
        float helmetOpacity, float chestplateOpacity, float leggingsOpacity, float bootsOpacity,
        float shieldOpacity,
        float elytraOpacity, float skullsAndBlocksOpacity, float capeOpacity, float trimOpacity,
        boolean showGlintHelmet, boolean showGlintChestplate, boolean showGlintLeggings, boolean showGlintBoots, boolean showGlintShield
    ) {
        public static Preset fromConfig(String name, ModConfig config) {
            return new Preset(
                name, false,
                config.helmetOpacity, config.chestplateOpacity, config.leggingsOpacity, config.bootsOpacity,
                config.shieldOpacity,
                config.elytraOpacity, config.skullsAndBlocksOpacity, config.capeOpacity, config.trimOpacity,
                config.showGlintHelmet, config.showGlintChestplate, config.showGlintLeggings, config.showGlintBoots, config.showGlintShield
            );
        }

        public void applyTo(ModConfig config) {
            config.helmetOpacity = this.helmetOpacity;
            config.chestplateOpacity = this.chestplateOpacity;
            config.leggingsOpacity = this.leggingsOpacity;
            config.bootsOpacity = this.bootsOpacity;
            config.shieldOpacity = this.shieldOpacity;
            config.elytraOpacity = this.elytraOpacity;
            config.skullsAndBlocksOpacity = this.skullsAndBlocksOpacity;
            config.capeOpacity = this.capeOpacity;
            config.trimOpacity = this.trimOpacity;
            config.showGlintHelmet = this.showGlintHelmet;
            config.showGlintChestplate = this.showGlintChestplate;
            config.showGlintLeggings = this.showGlintLeggings;
            config.showGlintBoots = this.showGlintBoots;
            config.showGlintShield = this.showGlintShield;
        }
    }

    public void save() {
        save(HideArmorMod.getConfigDir());
    }

    public void save(File configDir) {
        File configFile = new File(configDir, "hidearmor.json");
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            System.err.println("Failed to save HideArmorMod config: " + e.getMessage());
        }
    }

    public static ModConfig load(File configDir) {
        File configFile = new File(configDir, "hidearmor.json");
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded.presets == null) loaded.presets = new ArrayList<>();
                if (loaded.presets.isEmpty()) loaded.initDefaultPresets();
                return loaded;
            } catch (Exception e) {
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
            1.0f, 1.0f, 1.0f, 1.0f,
            true, true, true, true, true
        ));
        presets.add(new Preset(
            "Invisible", true,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f,
            false, false, false, false, false
        ));
    }

    private static class ModConfigDeserializer implements JsonDeserializer<ModConfig> {
        @Override
        public ModConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            ModConfig config = new ModConfig();

            config.helmetOpacity = getFloat(obj, "helmetOpacity", 1.0f);
            config.chestplateOpacity = getFloat(obj, "chestplateOpacity", 1.0f);
            config.leggingsOpacity = getFloat(obj, "leggingsOpacity", 1.0f);
            config.bootsOpacity = getFloat(obj, "bootsOpacity", 1.0f);
            config.shieldOpacity = getFloat(obj, "shieldOpacity", 1.0f);
            config.elytraOpacity = getFloat(obj, "elytraOpacity", 1.0f);
            config.skullsAndBlocksOpacity = getFloat(obj, "skullsAndBlocksOpacity", 1.0f);
            config.capeOpacity = getFloat(obj, "capeOpacity", 1.0f);
            config.trimOpacity = getFloat(obj, "trimOpacity", 1.0f);
            config.enableMultiplayerSync = getBool(obj, "enableMultiplayerSync", true);

            config.showGlintHelmet = getBool(obj, "showGlintHelmet", true);
            config.showGlintChestplate = getBool(obj, "showGlintChestplate", true);
            config.showGlintLeggings = getBool(obj, "showGlintLeggings", true);
            config.showGlintBoots = getBool(obj, "showGlintBoots", true);
            config.showGlintShield = getBool(obj, "showGlintShield", true);

            if (obj.has("uiTheme")) config.uiTheme = obj.get("uiTheme").getAsString();

            if (obj.has("presets")) {
                config.presets = new ArrayList<>();
                for (JsonElement e : obj.getAsJsonArray("presets")) {
                    config.presets.add(context.deserialize(e, Preset.class));
                }
            }

            // Migrate old boolean fields to new opacity floats
            migrateOldBooleans(obj, config);

            return config;
        }

        private void migrateOldBooleans(JsonObject obj, ModConfig config) {
            // Old configs had showElytra (boolean) instead of elytraOpacity (float)
            if (obj.has("showElytra") && !obj.has("elytraOpacity")) {
                config.elytraOpacity = obj.get("showElytra").getAsBoolean() ? 1.0f : 0.0f;
            }
            // Old configs had showSkullsAndBlocks (boolean) instead of skullsAndBlocksOpacity (float)
            if (obj.has("showSkullsAndBlocks") && !obj.has("skullsAndBlocksOpacity")) {
                config.skullsAndBlocksOpacity = obj.get("showSkullsAndBlocks").getAsBoolean() ? 1.0f : 0.0f;
            }
        }

        private float getFloat(JsonObject obj, String key, float def) {
            return obj.has(key) ? obj.get(key).getAsFloat() : def;
        }

        private boolean getBool(JsonObject obj, String key, boolean def) {
            return obj.has(key) ? obj.get(key).getAsBoolean() : def;
        }
    }
}
