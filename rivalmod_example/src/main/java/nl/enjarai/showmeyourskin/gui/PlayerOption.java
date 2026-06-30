package nl.enjarai.showmeyourskin.gui;


import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.trim.*;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.mixin.ClientMannequinAccessor;
import nl.enjarai.showmeyourskin.mixin.LivingEntityAccessor;
import nl.enjarai.showmeyourskin.pond.ArmorConfigContraband;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record PlayerOption(ArmorConfig armorConfig, UUID uuid, @Nullable AbstractClientPlayer player, Component title) {
  @SuppressWarnings("DataFlowIssue")
  public LivingEntity constructFakeEntity() {
    var player = new RemotePlayer((ClientLevel) this.player.level(), this.player.getGameProfile());
    ((LivingEntityAccessor) player).setEquipment(((LivingEntityAccessor) this.player).getEquipment());
    ((LivingEntityAccessor) player).setElytraAnimationState(((LivingEntityAccessor) this.player).getElytraAnimationState());
    player.tickCount = 20;
    player.setPosRaw(0, 9999, 0);
    return player;
  }

  @SuppressWarnings("DataFlowIssue")
  public Mannequin constructMannequin() {
    Mannequin entity;

    if (player != null) {
      entity = Mannequin.create(EntityType.MANNEQUIN, this.player.level());
      ((LivingEntityAccessor) entity).setEquipment(((LivingEntityAccessor) this.player).getEquipment());
      ((LivingEntityAccessor) entity).setElytraAnimationState(((LivingEntityAccessor) this.player).getElytraAnimationState());
      entity.setComponent(DataComponents.PROFILE, ResolvableProfile.createResolved(this.player.getGameProfile()));
    } else if (Minecraft.getInstance().level != null) {
      var level = Minecraft.getInstance().level;

      entity = Mannequin.create(EntityType.MANNEQUIN, level);
      entity.setComponent(DataComponents.PROFILE, ResolvableProfile.createUnresolved(uuid));
      ((ClientMannequinAccessor) entity).invokeUpdateSkin();

      entity.setItemSlot(EquipmentSlot.HEAD, trimAndEnchant(Items.NETHERITE_HELMET.getDefaultInstance(), level));
      entity.setItemSlot(EquipmentSlot.CHEST, enchant(Items.ELYTRA.getDefaultInstance(), level));
      entity.setItemSlot(EquipmentSlot.LEGS, trimAndEnchant(Items.NETHERITE_LEGGINGS.getDefaultInstance(), level));
      entity.setItemSlot(EquipmentSlot.FEET, trimAndEnchant(Items.NETHERITE_BOOTS.getDefaultInstance(), level));
      entity.setItemSlot(EquipmentSlot.OFFHAND, enchant(Items.SHIELD.getDefaultInstance(), level));
    } else {
      return null;
    }

    entity.tickCount = 20;
    var pos = Minecraft.getInstance().player.position();
    entity.absSnapTo(pos.x(), pos.y(), pos.z());
    ((ArmorConfigContraband) entity).show_me_your_skin$setArmorConfig(armorConfig);

    return entity;
  }

  private ItemStack trimAndEnchant(ItemStack stack, ClientLevel level) {
    try {
      var trimPatterns = level.registryAccess().lookupOrThrow(Registries.TRIM_PATTERN);
      var trimMaterials = level.registryAccess().lookupOrThrow(Registries.TRIM_MATERIAL);

      var armorTrim = new ArmorTrim(trimMaterials.getOrThrow(TrimMaterials.GOLD), trimPatterns.getOrThrow(TrimPatterns.FLOW));
      stack.set(DataComponents.TRIM, armorTrim);
    } catch (IllegalStateException e) {
      // Ignored
    }

    return enchant(stack, level);
  }

  private ItemStack enchant(ItemStack stack, ClientLevel level) {
    try {
      var enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

      var enchantment = enchantments.getOrThrow(Enchantments.UNBREAKING);
      var mutableEnchantments = new ItemEnchantments.Mutable(stack.getEnchantments());
      mutableEnchantments.set(enchantment, 1);
      stack.set(DataComponents.ENCHANTMENTS, mutableEnchantments.toImmutable());
    } catch (IllegalStateException e) {
      // Ignored
    }

    return stack;
  }
}
