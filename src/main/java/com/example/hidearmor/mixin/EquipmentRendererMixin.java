package com.example.hidearmor.mixin;

import com.example.hidearmor.LocalPlayerTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.RenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {

    private float getOpacity(ItemStack stack) {
        com.example.hidearmor.ModConfig cfg = LocalPlayerTracker.getConfigForCurrentPlayer();
        if (cfg == null)
            return 1.0f;

        if (stack == null || stack.isEmpty())
            return 1.0f;

        if (stack.isOf(Items.SHIELD)) {
            return cfg.shieldOpacity;
        }

        EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);
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

        if (stack.isOf(Items.SHIELD)) {
            return cfg.showGlintShield;
        }

        EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);
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

    @WrapOperation(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayers;armorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private RenderLayer modifyArmorLayer(Identifier texture, Operation<RenderLayer> original,
            EquipmentModel.LayerType layerType, RegistryKey<?> asset, Model<?> model, Object state, ItemStack stack) {
        RenderLayer layer = original.call(texture);
        float opacity = getOpacity(stack);
        if (opacity < 1.0f && opacity > 0.0f) {
            return RenderLayers.armorTranslucent(texture);
        }
        return layer;
    }

    @WrapOperation(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/TexturedRenderLayers;getArmorTrims(Z)Lnet/minecraft/client/render/RenderLayer;"))
    private RenderLayer modifyTrimLayer(boolean decal, Operation<RenderLayer> original,
            EquipmentModel.LayerType layerType, RegistryKey<?> asset, Model<?> model, Object state, ItemStack stack) {
        float opacity = getOpacity(stack);
        if (opacity < 1.0f && opacity > 0.0f) {
            return RenderLayers.armorTranslucent(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
        }
        return original.call(decal);
    }

    @WrapOperation(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/RenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"))
    private <S> void injectAlphaToSubmitModel(
            RenderCommandQueue queue, Model<? super S> model, S state, MatrixStack matrices, RenderLayer layer,
            int light, int overlay, int color, Sprite sprite, int unknown1,
            ModelCommandRenderer.CrumblingOverlayCommand crumbling,
            Operation<Void> original,
            EquipmentModel.LayerType layerType, RegistryKey<?> asset, Model<?> fallbackModel, Object fallbackState,
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
        if (opacity < 1.0f && layerType == EquipmentModel.LayerType.HUMANOID_LEGGINGS) {
            EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);
            if (equippable != null && equippable.slot() == EquipmentSlot.CHEST)
                return;
        }

        int modifiedColor = color;
        if (opacity < 1.0f) {
            int alpha = (int) (opacity * 255.0f);
            modifiedColor = ColorHelper.getArgb(alpha, ColorHelper.getRed(color), ColorHelper.getGreen(color),
                    ColorHelper.getBlue(color));
        }
        original.call(queue, model, state, matrices, layer, light, overlay, modifiedColor, sprite, unknown1, crumbling);
    }
}
