package nl.enjarai.showmeyourskin.gui;

import io.wispforest.owo.braid.core.BraidScreen;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.StatelessWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.SpriteWidget;
import io.wispforest.owo.braid.widgets.basic.Builder;
import io.wispforest.owo.braid.widgets.basic.EmptyWidget;
import io.wispforest.owo.braid.widgets.basic.Padding;
import io.wispforest.owo.braid.widgets.basic.Tooltip;
import io.wispforest.owo.braid.widgets.button.Button;
import io.wispforest.owo.braid.widgets.button.Clickable;
import io.wispforest.owo.braid.widgets.checkbox.Checkbox;
import io.wispforest.owo.braid.widgets.flex.Column;
import io.wispforest.owo.braid.widgets.flex.Row;
import io.wispforest.owo.braid.widgets.focus.Focusable;
import io.wispforest.owo.braid.widgets.stack.Stack;
import io.wispforest.owo.braid.widgets.stack.StackBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.client.ModKeyBindings;
import nl.enjarai.showmeyourskin.config.ModConfig;

import java.util.List;

public class SyncedArmorConfigWidget extends StatefulWidget {
  private final PlayerOption playerOption;

  public SyncedArmorConfigWidget(PlayerOption playerOption) {
    this.playerOption = playerOption;
  }

  @Override
  public WidgetState<?> createState() {
    return new State();
  }

  static class State extends WidgetState<SyncedArmorConfigWidget> {
    @Override
    public Widget build(BuildContext context) {
      return new Column(
        new Padding(Insets.all(2)),
        List.of(
          new Row(
            new Padding(Insets.all(2)),
            List.of(
              new Tooltip(
                Component.translatable("gui.showmeyourskin.armorScreen.globalToggleTooltip", ModKeyBindings.GLOBAL_TOGGLE.getTranslatedKeyMessage()),
                new IconCheckbox(
                  ShowMeYourSkin.id("button/enabled"),
                  ShowMeYourSkin.id("button/enabled_disabled"),
                  ModConfig.INSTANCE.globalEnabled,
                  true,
                  value -> update(() -> ModConfig.INSTANCE.globalEnabled = value)
                )
              ),
              new Tooltip(
                Component.translatable("gui.showmeyourskin.armorScreen.overridesEnabled"),
                new IconCheckbox(
                  ShowMeYourSkin.id("button/enable_local_overrides"),
                  ShowMeYourSkin.id("button/enable_local_overrides_disabled"),
                  ModConfig.INSTANCE.overridesEnabledInServerMode,
                  true,
                  value -> update(() -> ModConfig.INSTANCE.overridesEnabledInServerMode = value)
                )
              ),
              ModConfig.INSTANCE.overridesEnabledInServerMode ? new Tooltip(
                Component.translatable("gui.showmeyourskin.armorScreen.overridesConfigure"),
                new Clickable(
                  () -> {
                    Minecraft.getInstance().setScreen(ArmorConfigScreen.createLocalConfigScreen(BraidScreen.maybeOf(context)));
                    return true;
                  },
                  SoundEvents.UI_BUTTON_CLICK.value(),
                  new Stack(
                    new StackBase(new Builder(context2 ->
                      new SpriteWidget(Focusable.shouldShowHighlight(context2) ? Checkbox.HIGHLIGHTED_TEXTURE : Checkbox.TEXTURE))
                    ),
                    new SpriteWidget(ShowMeYourSkin.id("button/edit_local_overrides"))
                  )
                )
              ) : EmptyWidget.INSTANCE
            )
          ),
          new ArmorConfigWidget(widget().playerOption, true)
        )
      );
    }

    void update(Runnable fn) {
      setState(fn);
      ModConfig.INSTANCE.save();
    }
  }
}
