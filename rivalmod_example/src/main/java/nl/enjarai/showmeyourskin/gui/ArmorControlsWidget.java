package nl.enjarai.showmeyourskin.gui;

import io.wispforest.owo.braid.core.Alignment;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Align;
import io.wispforest.owo.braid.widgets.basic.Padding;
import io.wispforest.owo.braid.widgets.basic.Panel;
import io.wispforest.owo.braid.widgets.flex.Column;
import io.wispforest.owo.braid.widgets.scroll.*;
import net.minecraft.network.chat.Component;
import nl.enjarai.showmeyourskin.ShowMeYourSkinClient;
import nl.enjarai.showmeyourskin.config.ArmorConfig;
import nl.enjarai.showmeyourskin.config.ModConfig;

import java.util.List;

public class ArmorControlsWidget extends StatefulWidget {
  private final ArmorConfig armorConfig;
  private final boolean synced;

  public ArmorControlsWidget(ArmorConfig armorConfig, boolean synced) {
    this.armorConfig = armorConfig;
    this.synced = synced;
  }

  @Override
  public WidgetState<?> createState() {
    return new State();
  }

  static class State extends WidgetState<ArmorControlsWidget> {
    @Override
    public Widget build(BuildContext context) {
      return new Panel(
        Panel.VANILLA_INSET,
        new Padding(
          Insets.all(1),
          new ScrollableWithBars(
            null,
            null,
            null,
            4,
            ButtonScrollbar::new,
            new Padding(
              Insets.all(3),
              new Align(
                Alignment.TOP_LEFT,
                new Column(
                  new Padding(Insets.all(1)),
                  List.of(
                    new PieceConfigWidget(widget().armorConfig.head, Component.translatable("gui.showmeyourskin.armorScreen.piece.head"), this::update),
                    new PieceConfigWidget(widget().armorConfig.chest, Component.translatable("gui.showmeyourskin.armorScreen.piece.chest"), this::update),
                    new PieceConfigWidget(widget().armorConfig.legs, Component.translatable("gui.showmeyourskin.armorScreen.piece.legs"), this::update),
                    new PieceConfigWidget(widget().armorConfig.feet, Component.translatable("gui.showmeyourskin.armorScreen.piece.feet"), this::update),
                    new OtherConfigWidget(widget().armorConfig.elytra, Component.translatable("gui.showmeyourskin.armorScreen.piece.elytra"), this::update, "elytra"),
                    new OtherConfigWidget(widget().armorConfig.shield, Component.translatable("gui.showmeyourskin.armorScreen.piece.shield"), this::update, "shield"),
                    new OtherConfigWidget(widget().armorConfig.hat, Component.translatable("gui.showmeyourskin.armorScreen.piece.hat"), this::update, "hat")
                  )
                )
              )
            )
          )
        )
      );
    }

    public void update() {
      if (widget().synced) {
        ShowMeYourSkinClient.syncToServer(widget().armorConfig);
      } else {
        ModConfig.INSTANCE.save();
      }
    }
  }
}
