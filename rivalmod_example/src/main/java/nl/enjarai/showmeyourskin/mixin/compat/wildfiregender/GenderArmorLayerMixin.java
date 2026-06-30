package nl.enjarai.showmeyourskin.mixin.compat.wildfiregender;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
  targets = "com.wildfire.render.GenderArmorLayer",
  remap = false
)
public class GenderArmorLayerMixin {
  @Inject(
    method = "renderBreastArmor",
    at = @At("HEAD"),
    cancellable = true
  )
  private void hideBreastArmor(Identifier texture, PoseStack matrixStack, SubmitNodeCollector queue,
                               HumanoidRenderState state, @Coerce Object side, int color, boolean glint, CallbackInfo ci) {
    var config = state.getData(ShowMeYourSkinClient.ARMOR_CONFIG_KEY);
    if (config == null) {
      return;
    }

    if (!config.chest.base()) {
      ci.cancel();
    }
  }

  @Inject(
    method = "renderArmorTrim",
    at = @At("HEAD"),
    cancellable = true
  )
  private void hideBreastArmor(ResourceKey<EquipmentAsset> armorModel, PoseStack matrixStack, SubmitNodeCollector queue,
                               HumanoidRenderState state, ArmorTrim trim, @Coerce Object side, boolean glint, CallbackInfo ci) {
    var config = state.getData(ShowMeYourSkinClient.ARMOR_CONFIG_KEY);
    if (config == null) {
      return;
    }

    if (!config.chest.trim()) {
      ci.cancel();
    }
  }

  @Inject(
    method = "renderGlint",
    at = @At("HEAD"),
    cancellable = true
  )
  private void renderGlint(PoseStack matrixStack, SubmitNodeCollector renderQueue, HumanoidRenderState state, @Coerce Object box, CallbackInfo ci) {
    var config = state.getData(ShowMeYourSkinClient.ARMOR_CONFIG_KEY);
    if (config == null) {
      return;
    }

    if (!config.chest.glint()) {
      ci.cancel();
    }
  }
}
