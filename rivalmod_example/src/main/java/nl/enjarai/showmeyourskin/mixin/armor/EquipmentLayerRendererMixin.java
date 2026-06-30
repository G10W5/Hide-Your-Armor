package nl.enjarai.showmeyourskin.mixin.armor;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAsset;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentLayerRenderer.class)
public class EquipmentLayerRendererMixin {
  @Inject(
    method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
    at = @At("HEAD")
  )
  private void loadConfig(EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> resourceKey,
                          Model<?> model, Object object, ItemStack itemStack, PoseStack poseStack,
                          SubmitNodeCollector submitNodeCollector, int i, @Nullable Identifier identifier,
                          int j, int k, CallbackInfo ci, @Share("armor_config") LocalRef<ArmorConfig.PieceConfig> armorConfig) {
    if (!(object instanceof LivingEntityRenderState renderState)) {
      return;
    }

    var config = renderState.getData(ShowMeYourSkinClient.ARMOR_CONFIG_KEY);
    if (config == null) {
      return;
    }

    var equippable = itemStack.get(DataComponents.EQUIPPABLE);
    if (equippable == null) {
      return;
    }


    if (itemStack.is(Items.ELYTRA)) {
      armorConfig.set(config.elytra);
      return;
    }

    if (itemStack.is(ItemTags.TRIMMABLE_ARMOR)) {
      armorConfig.set(switch (equippable.slot()) {
        case FEET -> config.feet;
        case LEGS -> config.legs;
        case CHEST -> config.chest;
        case HEAD -> config.head;
        default -> null;
      });
      return;
    }

    if (equippable.slot() == EquipmentSlot.HEAD) {
      armorConfig.set(config.hat);
    }
  }

  @WrapWithCondition(
    method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
      ordinal = 0
    )
  )
  private boolean hideBase(OrderedSubmitNodeCollector instance, Model<?> model, Object s, PoseStack poseStack,
                           RenderType renderType, int i, int j, int k, @Nullable TextureAtlasSprite textureAtlasSprite,
                           int l, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay,
                           @Share("armor_config") LocalRef<ArmorConfig.PieceConfig> armorConfig) {
    return armorConfig.get() == null || armorConfig.get().base();
  }

  @WrapWithCondition(
    method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
      ordinal = 1
    )
  )
  private boolean hideGlint(OrderedSubmitNodeCollector instance, Model<?> model, Object s, PoseStack poseStack,
                           RenderType renderType, int i, int j, int k, @Nullable TextureAtlasSprite textureAtlasSprite,
                           int l, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay,
                           @Share("armor_config") LocalRef<ArmorConfig.PieceConfig> armorConfig) {
    return armorConfig.get() == null || armorConfig.get().glint();
  }

  @WrapWithCondition(
    method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
      ordinal = 2
    )
  )
  private boolean hideTrim(OrderedSubmitNodeCollector instance, Model<?> model, Object s, PoseStack poseStack,
                           RenderType renderType, int i, int j, int k, @Nullable TextureAtlasSprite textureAtlasSprite,
                           int l, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay,
                           @Share("armor_config") LocalRef<ArmorConfig.PieceConfig> armorConfig) {
    return armorConfig.get() == null || armorConfig.get().trim();
  }
}
