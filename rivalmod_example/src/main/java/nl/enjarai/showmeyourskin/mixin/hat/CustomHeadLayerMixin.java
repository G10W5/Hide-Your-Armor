package nl.enjarai.showmeyourskin.mixin.hat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.mixin.ItemStackRenderStateAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomHeadLayer.class)
public class CustomHeadLayerMixin {
  @Inject(
    method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V",
    at = @At("HEAD"),
    cancellable = true
  )
  private void hideHat(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int j, LivingEntityRenderState livingEntityRenderState, float f, float g, CallbackInfo ci) {
    var config = livingEntityRenderState.getData(ShowMeYourSkinClient.ARMOR_CONFIG_KEY);
    if (config == null) {
      return;
    }

    if (!config.hat.base()) {
      ci.cancel();
    }

    if (!config.hat.glint() && !livingEntityRenderState.headItem.isEmpty()) {
      var accessible = (ItemStackRenderStateAccessor) livingEntityRenderState.headItem;

      for (int i = 0; i < accessible.getActiveLayerCount(); i++) {
        accessible.getLayers()[i].setFoilType(ItemStackRenderState.FoilType.NONE);
      }
    }
  }
}
