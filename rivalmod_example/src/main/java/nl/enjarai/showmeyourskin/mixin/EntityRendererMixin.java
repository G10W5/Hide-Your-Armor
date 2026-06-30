package nl.enjarai.showmeyourskin.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRendererMixin {
    @Inject(
            method = "submit",
            at = @At("HEAD")
    )
    private <E extends Entity, S extends EntityRenderState> void captureEntityContext(S renderState, CameraRenderState cameraRenderState, double d, double e, double f, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CallbackInfo ci) {
//        var entity = ShowMeYourSkinClient.ENTITY_RENDER_STATE_KEY.get(renderState);
//        if (entity instanceof PlayerEntity) {
//            IWishMixinAllowedForPublicStaticFields.currentEntity = entity;
//        }
    }
}
