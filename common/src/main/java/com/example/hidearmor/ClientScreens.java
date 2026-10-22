package com.example.hidearmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Version bridge for the current screen.
 *
 * 26.1.2 stores it in Minecraft.screen; 26.2 moved it behind
 * Minecraft.gui.screen()/setScreen(). Opening a screen uses
 * Minecraft.setScreenAndShow, which exists in both.
 */
public final class ClientScreens {

    private static boolean resolved;
    private static Method guiScreenGetter;
    private static Field minecraftScreenField;

    private ClientScreens() {
    }

    private static synchronized void resolve() {
        if (resolved) {
            return;
        }
        resolved = true;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }
        try {
            guiScreenGetter = mc.gui.getClass().getMethod("screen");
            return;
        } catch (Throwable ignored) {
        }
        try {
            minecraftScreenField = Minecraft.class.getField("screen");
        } catch (Throwable ignored) {
        }
    }

    public static Screen get() {
        resolve();
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return null;
        }
        try {
            if (guiScreenGetter != null) {
                return (Screen) guiScreenGetter.invoke(mc.gui);
            }
            if (minecraftScreenField != null) {
                return (Screen) minecraftScreenField.get(mc);
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static void open(Screen screen) {
        Minecraft.getInstance().setScreenAndShow(screen);
    }
}
