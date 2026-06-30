package com.example.hidearmor;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

/**
 * Network packet that broadcasts a player's opacity config to other clients.
 * Sent C2S when the local player saves their config (if multiplayer sync is
 * enabled).
 * The server relays it S2C to all other connected clients that have the mod.
 */
public record PlayerConfigPayload(
        UUID playerUuid,
        float helmetOpacity,
        float chestplateOpacity,
        float leggingsOpacity,
        float bootsOpacity,
        float shieldOpacity,
        boolean showElytra,
        boolean showSkullsAndBlocks,
        boolean showGlintHelmet,
        boolean showGlintChestplate,
        boolean showGlintLeggings,
        boolean showGlintBoots,
        boolean showGlintShield) implements CustomPayload {

    public static final CustomPayload.Id<PlayerConfigPayload> ID = new CustomPayload.Id<>(
            Identifier.of("hidearmor", "sync"));

    public static final PacketCodec<PacketByteBuf, PlayerConfigPayload> CODEC = PacketCodec
            .of(PlayerConfigPayload::encode, PlayerConfigPayload::decode);

    private static void encode(PlayerConfigPayload payload, PacketByteBuf buf) {
        buf.writeUuid(payload.playerUuid);
        buf.writeFloat(payload.helmetOpacity);
        buf.writeFloat(payload.chestplateOpacity);
        buf.writeFloat(payload.leggingsOpacity);
        buf.writeFloat(payload.bootsOpacity);
        buf.writeFloat(payload.shieldOpacity);
        buf.writeBoolean(payload.showElytra);
        buf.writeBoolean(payload.showSkullsAndBlocks);
        buf.writeBoolean(payload.showGlintHelmet);
        buf.writeBoolean(payload.showGlintChestplate);
        buf.writeBoolean(payload.showGlintLeggings);
        buf.writeBoolean(payload.showGlintBoots);
        buf.writeBoolean(payload.showGlintShield);
    }

    private static PlayerConfigPayload decode(PacketByteBuf buf) {
        return new PlayerConfigPayload(
                buf.readUuid(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean());
    }

    /** Convert a ModConfig to a payload for the given player UUID. */
    public static PlayerConfigPayload from(UUID uuid, ModConfig config) {
        return new PlayerConfigPayload(
                uuid,
                config.helmetOpacity,
                config.chestplateOpacity,
                config.leggingsOpacity,
                config.bootsOpacity,
                config.shieldOpacity,
                config.showElytra,
                config.showSkullsAndBlocks,
                config.showGlintHelmet,
                config.showGlintChestplate,
                config.showGlintLeggings,
                config.showGlintBoots,
                config.showGlintShield);
    }

    /** Convert this payload into a ModConfig snapshot. */
    public ModConfig toConfig() {
        ModConfig cfg = new ModConfig();
        cfg.helmetOpacity = this.helmetOpacity;
        cfg.chestplateOpacity = this.chestplateOpacity;
        cfg.leggingsOpacity = this.leggingsOpacity;
        cfg.bootsOpacity = this.bootsOpacity;
        cfg.shieldOpacity = this.shieldOpacity;
        cfg.showElytra = this.showElytra;
        cfg.showSkullsAndBlocks = this.showSkullsAndBlocks;
        cfg.showGlintHelmet = this.showGlintHelmet;
        cfg.showGlintChestplate = this.showGlintChestplate;
        cfg.showGlintLeggings = this.showGlintLeggings;
        cfg.showGlintBoots = this.showGlintBoots;
        cfg.showGlintShield = this.showGlintShield;
        return cfg;
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
