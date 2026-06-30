package nl.enjarai.showmeyourskin.mixin.shield;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.mixin.ItemStackRenderStateAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
  @WrapWithCondition(
    method = "renderItem",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
    )
  )
  private boolean hideShield(ItemStackRenderState instance, PoseStack poseStack,
                             SubmitNodeCollector submitNodeCollector, int j, int k, int l,
                             @Local(argsOnly = true) ItemStack itemStack,
                             @Local(argsOnly = true) LivingEntity livingEntity) {
    if (!itemStack.is(Items.SHIELD)) {
      return true;
    }

    var config = ModConfig.INSTANCE.getApplicable(livingEntity.getUUID());
    if (!config.shield.glint()) {
      var accessible = (ItemStackRenderStateAccessor) instance;
      for (int i = 0; i < accessible.getActiveLayerCount(); i++) {
        accessible.getLayers()[i].setFoilType(ItemStackRenderState.FoilType.NONE);
      }
    }

    return config.shield.base();
  }
}
