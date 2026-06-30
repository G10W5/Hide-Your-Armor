package nl.enjarai.showmeyourskin.gui;

import io.wispforest.owo.braid.core.Constraints;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.core.LayoutAxis;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.flex.*;
import io.wispforest.owo.braid.widgets.scroll.ButtonScrollbar;
import io.wispforest.owo.braid.widgets.scroll.ScrollController;
import io.wispforest.owo.braid.widgets.scroll.Scrollable;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.config.ModConfig;

import java.util.List;
import java.util.function.Consumer;

public class PlayerListWidget extends StatefulWidget {
  private final PlayerOption globalOption;
  private final List<PlayerOption> options;
  private final PlayerOption selectedOption;
  private final Consumer<PlayerOption> onSelect;
  private final Consumer<PlayerOption> onDelete;

  public PlayerListWidget(PlayerOption globalOption, List<PlayerOption> options, PlayerOption selectedOption, Consumer<PlayerOption> onSelect, Consumer<PlayerOption> onDelete) {
    this.globalOption = globalOption;
    this.options = options;
    this.selectedOption = selectedOption;
    this.onSelect = onSelect;
    this.onDelete = onDelete;
  }

  @Override
  public WidgetState<?> createState() {
    return new State();
  }

  static class State extends WidgetState<PlayerListWidget> {
    private ScrollController verticalController;

    @Override
    public void init() {
      this.verticalController = new ScrollController(this);
    }

    @Override
    public Widget build(BuildContext context) {
      return new Sized(
        100,
        180,
        new Panel(
          Panel.VANILLA_LIGHT,
          new Padding(
            Insets.all(6),
            new Column(
              new Padding(Insets.all(2)),
              List.of(
                new Panel(
                  Panel.VANILLA_INSET,
                  new Padding(
                    Insets.all(1),
                    new PlayerListEntry(
                      widget().globalOption,
                      ShowMeYourSkin.id("textures/gui/global_icon.png"),
                      widget().globalOption == widget().selectedOption,
                      false,
                      () -> widget().onSelect.accept(widget().globalOption),
                      () -> {
                      }
                    )
                  )
                ),
                new Flexible(
                  new Panel(
                    Panel.VANILLA_INSET,
                    new Padding(
                      Insets.all(1),
                      new Row(
                        new Flexible(
                          new Scrollable(
                            false,
                            true,
                            null,
                            verticalController,
                            null,
                            new Column(
                              MainAxisAlignment.START,
                              CrossAxisAlignment.STRETCH,
                              widget().options.stream()
                                .map(option -> new PlayerListEntry(
                                  option,
                                  null,
                                  option == widget().selectedOption,
                                  ModConfig.INSTANCE.overrides.containsKey(option.uuid()),
                                  () -> widget().onSelect.accept(option),
                                  () -> widget().onDelete.accept(option)
                                ))
                                .toList()
                            )
                          )
                        ),
                        new Constrain(
                          Constraints.ofMaxWidth(4d),
                          new ButtonScrollbar(
                            LayoutAxis.VERTICAL,
                            verticalController
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      );
    }
  }
}
