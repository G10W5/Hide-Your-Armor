package com.glow.mixin;

import com.glow.HideArmor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorHideMixin {
    private BipedEntityRenderState currentState;
    private static final double BASE_TOLERANCE = 0.5D;
    private static final double SPEED_FACTOR = 2.0D;
    private static final double ACCEL_FACTOR = 1.5D;
    private double lastSpeedSq = 0.0D;

    @Inject(method = "render", at = @At("HEAD"))
    private void captureEntity(MatrixStack matrices, VertexConsumerProvider vertexConsumers, 
                             int light, BipedEntityRenderState state, float limbAngle, 
                             float limbDistance, CallbackInfo ci) {
        this.currentState = state;
    }

    @Inject(method = {"renderArmor", "renderElytra"}, at = @At("HEAD"), cancellable = true)
    private void onRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                         ItemStack stack, EquipmentSlot slot, int light, 
                         BipedEntityModel<?> model, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.player != null && currentState != null && HideArmor.isHidden) {
            // Calculate current speed
            double velX = client.player.getVelocity().x;
            double velY = client.player.getVelocity().y;
            double velZ = client.player.getVelocity().z;
            double speedSq = velX * velX + velY * velY + velZ * velZ;
            
            // Calculate acceleration factor
            double acceleration = Math.max(0, speedSq - lastSpeedSq);
            
            // Update speed-based tolerance with acceleration
            double tolerance = BASE_TOLERANCE + 
                             Math.sqrt(speedSq) * SPEED_FACTOR + 
                             Math.sqrt(acceleration) * ACCEL_FACTOR;
            
            // Calculate predicted position
            double predX = client.player.getX() + velX;
            double predY = client.player.getY() + velY;
            double predZ = client.player.getZ() + velZ;
            
            // Calculate distance to predicted position
            double dx = currentState.x - predX;
            double dy = currentState.y - predY;
            double dz = currentState.z - predZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            
            if (distance < tolerance) {
                ci.cancel();
            }
            
            lastSpeedSq = speedSq;
        }
    }
}