package nl.enjarai.showmeyourskin.gui;

import io.wispforest.owo.braid.core.*;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.flex.*;

import java.util.List;

public class ArmorConfigWidget extends StatefulWidget {
  private final PlayerOption playerOption;
  private final boolean synced;

  public ArmorConfigWidget(PlayerOption playerOption, boolean synced) {
    this.playerOption = playerOption;
    this.synced = synced;
  }

  @Override
  public WidgetState<?> createState() {
    return new State();
  }

  static class State extends WidgetState<ArmorConfigWidget> {
    @Override
    public Widget build(BuildContext buildContext) {
      return new Sized(
        200, 200,
        new Panel(
          Panel.VANILLA_LIGHT,
          new Padding(
            Insets.all(6),
            new Row(
              new Padding(Insets.all(2)),
              List.of(
                new IntrinsicWidth(
                  new Column(
                    MainAxisAlignment.CENTER,
                    CrossAxisAlignment.STRETCH,
                    new Padding(Insets.all(2)),
                    List.of(
                      new Flexible(
                        new Constrain(
                          Constraints.ofMinWidth(78),
                          new ArmorControlsWidget(widget().playerOption.armorConfig(), widget().synced)
                        )
                      ),
                      new AdditionalOptionsWidget(widget().playerOption.armorConfig(), widget().synced)
                    )
                  )
                ),
                new Flexible(
                  new PreviewWidget(widget().playerOption)
                )
              )
            )
          )
        )
      );
    }
  }
}
