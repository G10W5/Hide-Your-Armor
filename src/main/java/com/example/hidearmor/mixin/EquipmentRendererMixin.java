package com.example.hidearmor.mixin;

import com.example.hidearmor.LocalPlayerTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EquipmentLayerRenderer.class)
public class EquipmentRendererMixin {

    private float getOpacity(ItemStack stack) {
        com.example.hidearmor.ModConfig cfg = LocalPlayerTracker.getConfigForCurrentPlayer();
        if (cfg == null)
            return 1.0f;

        if (stack == null || stack.isEmpty())
            return 1.0f;

        if (stack.is(Items.SHIELD)) {
            return cfg.shieldOpacity;
        }

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null)
            return 1.0f;
        EquipmentSlot slot = equippable.slot();
        if (slot == null)
            return 1.0f;

        switch (slot) {
            case HEAD:
                return cfg.helmetOpacity;
            case CHEST:
                return cfg.chestplateOpacity;
            case LEGS:
                return cfg.leggingsOpacity;
            case FEET:
                return cfg.bootsOpacity;
            case OFFHAND:
                return cfg.shieldOpacity;
            default:
                return 1.0f;
        }
    }

    private boolean getShowGlint(ItemStack stack) {
        com.example.hidearmor.ModConfig cfg = LocalPlayerTracker.getConfigForCurrentPlayer();
        if (cfg == null)
            return true;

        if (stack == null || stack.isEmpty())
            return true;

        if (stack.is(Items.SHIELD)) {
            return cfg.showGlintShield;
        }

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null)
            return true;
        EquipmentSlot slot = equippable.slot();
        if (slot == null)
            return true;

        switch (slot) {
            case HEAD:
                return cfg.showGlintHelmet;
            case CHEST:
                return cfg.showGlintChestplate;
            case LEGS:
                return cfg.showGlintLeggings;
            case FEET:
                return cfg.showGlintBoots;
            case OFFHAND:
                return cfg.showGlintShield;
            default:
                return true;
        }
    }

    @WrapOperation(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;armorCutoutNoCull(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType modifyArmorLayer(Identifier texture, Operation<RenderType> original,
            EquipmentClientInfo.LayerType layerType, ResourceKey<?> asset, Model<?> model, Object state, ItemStack stack) {
        RenderType layer = original.call(texture);
        float opacity = getOpacity(stack);
        if (opacity < 1.0f && opacity > 0.0f) {
            return RenderTypes.armorTranslucent(texture);
        }
        return layer;
    }

    @WrapOperation(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Sheets;armorTrimsSheet(Z)Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType modifyTrimLayer(boolean decal, Operation<RenderType> original,
            EquipmentClientInfo.LayerType layerType, ResourceKey<?> asset, Model<?> model, Object state, ItemStack stack) {
        float opacity = getOpacity(stack);
        if (opacity < 1.0f && opacity > 0.0f) {
            return RenderTypes.armorTranslucent(Sheets.ARMOR_TRIMS_SHEET);
        }
        return original.call(decal);
    }

    @WrapOperation(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
    private <S> void injectAlphaToSubmitModel(
            OrderedSubmitNodeCollector queue, Model<? super S> model, S state, PoseStack matrices, RenderType layer,
            int light, int overlay, int color, TextureAtlasSprite sprite, int unknown1,
            ModelFeatureRenderer.CrumblingOverlay crumbling,
            Operation<Void> original,
            EquipmentClientInfo.LayerType layerType, ResourceKey<?> asset, Model<?> fallbackModel, Object fallbackState,
            ItemStack stack) {
        float opacity = getOpacity(stack);
        if (opacity <= 0.0f)
            return; // invisible

        // Check if this is a glint layer call — skip if glint is disabled
        // The glint RenderLayer name typically contains "glint"
        String layerName = layer.toString();
        if (layerName.contains("glint") && !getShowGlint(stack)) {
            return; // suppress glint rendering
        }

        // Skip inner layer for translucent chestplate to avoid breast-area artifacts
        if (opacity < 1.0f && layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS) {
            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
            if (equippable != null && equippable.slot() == EquipmentSlot.CHEST)
                return;
        }

        int modifiedColor = color;
        if (opacity < 1.0f) {
            int alpha = (int) (opacity * 255.0f);
            modifiedColor = ARGB.color(alpha, ARGB.red(color), ARGB.green(color),
                    ARGB.blue(color));
        }
        original.call(queue, model, state, matrices, layer, light, overlay, modifiedColor, sprite, unknown1, crumbling);
    }
}
