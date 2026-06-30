package nl.enjarai.showmeyourskin.gui;

import io.wispforest.owo.braid.core.Constraints;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.flex.Row;
import net.minecraft.network.chat.Component;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.ModConfig;
import nl.enjarai.showmeyourskin.config.SyncedModConfig;

import java.util.List;
import java.util.function.Function;

public class AdditionalOptionsWidget extends StatefulWidget {
  private final ArmorConfig armorConfig;
  private final boolean synced;

  public AdditionalOptionsWidget(ArmorConfig armorConfig, boolean synced) {
    this.armorConfig = armorConfig;
    this.synced = synced;
  }

  @Override
  public WidgetState<?> createState() {
    return new State();
  }

  static class State extends WidgetState<AdditionalOptionsWidget> {
    @Override
    public Widget build(BuildContext context) {
      return new Constrain(
        Constraints.ofMinHeight(28),
        new Panel(
          Panel.VANILLA_INSET,
          new Padding(
            Insets.all(4),
            new Row(
              new Padding(Insets.all(1)),
              List.of(
                !widget().synced || configAllows(SyncedModConfig::allowNotShowInCombat) ? new Tooltip(
                  Component.translatable("gui.showmeyourskin.armorScreen.combatTooltip"),
                  new IconCheckbox(
                    ShowMeYourSkin.id("button/show_in_combat"),
                    ShowMeYourSkin.id("button/show_in_combat_disabled"),
                    widget().armorConfig.showInCombat,
                    true,
                    value -> update(() -> widget().armorConfig.showInCombat = value)
                  )
                ) : EmptyWidget.INSTANCE,
                !widget().synced || configAllows(SyncedModConfig::allowNotShowNameTag) ? new Tooltip(
                  Component.translatable("gui.showmeyourskin.armorScreen.nameTagTooltip"),
                  new IconCheckbox(
                    ShowMeYourSkin.id("button/show_nametag"),
                    ShowMeYourSkin.id("button/show_nametag_disabled"),
                    widget().armorConfig.showNameTag,
                    true,
                    value -> update(() -> widget().armorConfig.showNameTag = value)
                  )
                ) : EmptyWidget.INSTANCE,
                !widget().synced || configAllows(SyncedModConfig::allowNotForceElytraWhenFlying) ? new Tooltip(
                  Component.translatable("gui.showmeyourskin.armorScreen.forceElytraWhenFlyingTooltip"),
                  new IconCheckbox(
                    ShowMeYourSkin.id("button/force_elytra_when_flying"),
                    ShowMeYourSkin.id("button/force_elytra_when_flying_disabled"),
                    widget().armorConfig.forceElytraWhenFlying,
                    true,
                    value -> update(() -> widget().armorConfig.forceElytraWhenFlying = value)
                  )
                ) : EmptyWidget.INSTANCE
              )
            )
          )
        )
      );
    }

    void update(Runnable fn) {
      setState(fn);
      if (widget().synced) {
        ShowMeYourSkinClient.syncToServer(widget().armorConfig);
      } else {
        ModConfig.INSTANCE.save();
      }
    }

    boolean configAllows(Function<SyncedModConfig, Boolean> getter) {
      if (ShowMeYourSkinClient.serverConfig == null) {
        return true;
      }

      return getter.apply(ShowMeYourSkinClient.serverConfig);
    }
  }
}
