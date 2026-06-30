package nl.enjarai.showmeyourskin.gui;

import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Padding;
import io.wispforest.owo.braid.widgets.flex.CrossAxisAlignment;
import io.wispforest.owo.braid.widgets.flex.MainAxisAlignment;
import io.wispforest.owo.braid.widgets.flex.Row;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import nl.enjarai.showmeyourskin.config.ModConfig;

import java.util.List;

public class LocalArmorConfigWidget extends StatefulWidget {
  private final List<PlayerOption> options;

  public LocalArmorConfigWidget(List<PlayerOption> options) {
    this.options = options;
  }

  @Override
  public WidgetState<?> createState() {
    return new State();
  }

  static class State extends WidgetState<LocalArmorConfigWidget> {
    final PlayerOption globalOption = new PlayerOption(
      ModConfig.INSTANCE.global, Minecraft.getInstance().getGameProfile().id(),
      Minecraft.getInstance().player, Component.translatable("gui.showmeyourskin.armorScreen.global")
    );
    PlayerOption selectedOption = globalOption;

    @Override
    public Widget build(BuildContext context) {
      return new Row(
        MainAxisAlignment.CENTER,
        CrossAxisAlignment.CENTER,
        new Padding(Insets.all(4)),
        List.of(
          new PlayerListWidget(globalOption, widget().options, selectedOption, this::updateOption, this::deleteOption),
          new ArmorConfigWidget(selectedOption, false)
        )
      );
    }

    @Override
    public void didUpdateWidget(LocalArmorConfigWidget oldWidget) {
      if (!widget().options.contains(selectedOption) && selectedOption != globalOption) {
        setState(() -> {
          selectedOption = globalOption;
        });
      }
    }

    void updateOption(PlayerOption option) {
      setState(() -> {
        selectedOption = option;
        if (option != globalOption) {
          ModConfig.INSTANCE.overrides.put(option.uuid(), new ModConfig.LocalOverride(option.title().getString(), option.armorConfig()));
          ModConfig.INSTANCE.save();
        }
      });
    }

    void deleteOption(PlayerOption option) {
      setState(() -> {
        if (option == selectedOption) {
          selectedOption = globalOption;
        }
        option.armorConfig().clear();
        ModConfig.INSTANCE.overrides.remove(option.uuid());
        ModConfig.INSTANCE.save();
      });
    }
  }
}
