package nl.enjarai.showmeyourskin.mixin.shield;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.mixin.ItemStackRenderStateAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {
  @Inject(
    method = "submitArmWithItem",
    at = @At("HEAD"),
    cancellable = true
  )
  private void hideShield(ArmedEntityRenderState armedEntityRenderState, ItemStackRenderState itemStackRenderState,
                          ItemStack itemStack, HumanoidArm humanoidArm, PoseStack poseStack,
                          SubmitNodeCollector submitNodeCollector, int j, CallbackInfo ci) {
    var config = armedEntityRenderState.getData(ShowMeYourSkinClient.ARMOR_CONFIG_KEY);
    if (config == null) {
      return;
    }

    if (!config.shield.base()) {
      ci.cancel();
    }

    if (!config.shield.glint() && !itemStackRenderState.isEmpty()) {
      var accessible = (ItemStackRenderStateAccessor) itemStackRenderState;

      for (int i = 0; i < accessible.getActiveLayerCount(); i++) {
        accessible.getLayers()[i].setFoilType(ItemStackRenderState.FoilType.NONE);
      }
    }
  }
}
