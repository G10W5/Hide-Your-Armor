package com.example.hidearmor.mixin;

import com.example.hidearmor.HideArmorMod;
import com.wildfire.api.IGenderArmor;
import com.wildfire.client.physics.BreastPhysics;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Compat mixin for Wildfire's Female Gender Mod.
 * When our mod sets chestplate opacity to 0% (fully invisible), this mixin
 * overrides the armor tightness to 0 in BreastPhysics so breasts stay at
 * their full size instead of being shrunk by the armor's tightness value.
 */
@Pseudo
@Mixin(BreastPhysics.class)
public class BreastPhysicsCompatMixin {

    /**
     * Override tightness in the main update() path.
     * When isChestplateFullyHidden is true, return 0 tightness so breast
     * size is not reduced by armor compression.
     */
    @Redirect(
        method = "update(Lnet/minecraft/world/entity/LivingEntity;Lcom/wildfire/api/IGenderArmor;)V",
        at = @At(value = "INVOKE", target = "Lcom/wildfire/api/IGenderArmor;tightness()F", ordinal = 0),
        remap = false
    )
    private float overrideTightness(IGenderArmor armor) {
        if (HideArmorMod.isChestplateFullyHidden) {
            return 0f;
        }
        return armor.tightness();
    }

    /**
     * Override tightness in the simplifiedTick() path (used for armor stands).
     */
    @Redirect(
        method = "simplifiedTick",
        at = @At(value = "INVOKE", target = "Lcom/wildfire/api/IGenderArmor;tightness()F"),
        remap = false
    )
    private float overrideSimplifiedTightness(IGenderArmor armor) {
        if (HideArmorMod.isChestplateFullyHidden) {
            return 0f;
        }
        return armor.tightness();
    }
}
