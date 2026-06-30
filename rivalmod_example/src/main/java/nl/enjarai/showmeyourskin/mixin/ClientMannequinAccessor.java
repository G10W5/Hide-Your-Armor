package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.entity.ClientMannequin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientMannequin.class)
public interface ClientMannequinAccessor {
  @Invoker
  void invokeUpdateSkin();
}
