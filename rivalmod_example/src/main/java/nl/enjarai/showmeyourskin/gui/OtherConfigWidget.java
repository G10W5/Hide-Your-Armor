package nl.enjarai.showmeyourskin.gui;

import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.HoverableBuilder;
import io.wispforest.owo.braid.widgets.basic.Padding;
import io.wispforest.owo.braid.widgets.button.Clickable;
import io.wispforest.owo.braid.widgets.flex.Column;
import io.wispforest.owo.braid.widgets.flex.Row;
import io.wispforest.owo.braid.widgets.label.Label;
import io.wispforest.owo.braid.widgets.label.LabelStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ArmorConfig;

import java.util.List;

public class OtherConfigWidget extends StatefulWidget {
  private final ArmorConfig.PieceConfig pieceConfig;
  private final Component label;
  private final Runnable onUpdate;
  private final String texture;

  public OtherConfigWidget(ArmorConfig.PieceConfig pieceConfig, Component label, Runnable onUpdate, String texture) {
    this.pieceConfig = pieceConfig;
    this.label = label;
    this.onUpdate = onUpdate;
    this.texture = texture;
  }

  @Override
  public WidgetState<?> createState() {
    return new State();
  }

  static class State extends WidgetState<OtherConfigWidget> {
    @Override
    public Widget build(BuildContext context) {
      return new Column(
        new Padding(Insets.all(1)),
        List.of(
          new Padding(
            Insets.top(1),
            new HoverableBuilder((hoverableContext, hovered) ->
              new Clickable(
                this::toggleAll,
                SoundEvents.UI_BUTTON_CLICK.value(),
                new Label(new LabelStyle(null, null, Style.EMPTY.withUnderlined(hovered), true), false, widget().label)
              )
            )
          ),
          new Row(
            new Padding(Insets.all(1)),
            List.of(
              new IconCheckbox(
                ShowMeYourSkin.id("button/" + widget().texture),
                ShowMeYourSkin.id("button/" + widget().texture + "_disabled"),
                widget().pieceConfig.base,
                true,
                value -> update(() -> widget().pieceConfig.base = value)
              ),
              new IconCheckbox(
                ShowMeYourSkin.id("button/glint"),
                ShowMeYourSkin.id("button/glint_disabled"),
                widget().pieceConfig.glint && widget().pieceConfig.base,
                true,
                value -> update(() -> {
                  widget().pieceConfig.glint = value;
                  if (!widget().pieceConfig.base) {
                    widget().pieceConfig.setAll(true);
                  }
                })
              )
            )
          )
        )
      );
    }

    void update(Runnable fn) {
      setState(fn);
      widget().onUpdate.run();
    }

    boolean toggleAll() {
      setState(() -> {
        widget().pieceConfig.setAll(!widget().pieceConfig.base);
      });
      widget().onUpdate.run();
      return true;
    }
  }
}
