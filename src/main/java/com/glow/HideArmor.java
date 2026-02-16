package com.glow;

import com.glow.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class HideArmor implements ClientModInitializer {
    public static boolean isHidden;
    private static KeyBinding hideKey;
    private static ModConfig config;

    @Override
    public void onInitializeClient() {
        config = ModConfig.load();
        isHidden = config.armorHidden;

        hideKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hidearmor.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.hidearmor.keys"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (hideKey.wasPressed()) {
                isHidden = !isHidden;
                config.armorHidden = isHidden;
                config.save();
                if (client.player != null) {
                    String key = isHidden ? "message.hidearmor.hidden" : "message.hidearmor.visible";
                    client.player.sendMessage(Text.translatable(key), true);
                }
            }
        });
    }
}