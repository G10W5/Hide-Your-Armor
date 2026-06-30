package nl.enjarai.showmeyourskin.gui;

import io.wispforest.owo.braid.core.Alignment;
import io.wispforest.owo.braid.core.Color;
import io.wispforest.owo.braid.core.Insets;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.label.Label;
import io.wispforest.owo.braid.widgets.label.LabelStyle;
import io.wispforest.owo.braid.widgets.object.EntityWidget;
import io.wispforest.owo.braid.widgets.stack.Stack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.entity.ClientMannequin;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.Mannequin;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;

import java.time.Duration;

public class PreviewWidget extends StatefulWidget {
  private final PlayerOption playerOption;

  public PreviewWidget(PlayerOption playerOption) {
    this.playerOption = playerOption;
  }

  @Override
  public WidgetState<?> createState() {
    return new State();
  }

  static class State extends WidgetState<PreviewWidget> {
    private Mannequin player;
    private boolean ready;

    @Override
    public void init() {
      player = widget().playerOption.constructMannequin();

      tickEntity();
    }

    public void tickEntity() {
      if (player != null) {
        player.tick();

        var nowReady = ((ClientMannequin) player).getSkin() != ClientMannequin.DEFAULT_SKIN;
        if (nowReady != ready) {
          setState(() -> {
            ready = nowReady;
          });
        }
      }

      scheduleDelayedCallback(Duration.ofMillis(50), this::tickEntity);
    }

    @Override
    public void didUpdateWidget(PreviewWidget oldWidget) {
      if (!widget().playerOption.equals(oldWidget.playerOption)) {
        player = widget().playerOption.constructMannequin();
        setState(() -> {
          ready = false;
        });
      }
    }

    @Override
    public Widget build(BuildContext context) {
      return new Panel(
        ShowMeYourSkin.id("panel/dark_inset"),
        new Stack(
          player != null ?
            new Padding(
              Insets.all(1),
              ready ? new EntityWidget(1.25, player, w -> {
                w.scaleToFit(true);
                w.displayMode(EntityWidget.DisplayMode.CURSOR);
                w.transform(m -> m.translate(0, -0.2f, 0));
              }) : EmptyWidget.INSTANCE
            ) :
            new Padding(
              Insets.all(4),
              new Sized(
                50, 50,
                new Label(
                  new LabelStyle(null, Color.formatting(ChatFormatting.DARK_GRAY), null, false),
                  true,
                  Component.translatable("gui.showmeyourskin.armorScreen.previewUnavailable")
                )
              )
            ),
          new Padding(
            Insets.all(4),
            new Align(
              Alignment.TOP_LEFT,
              new Label(
                new LabelStyle(null, Color.formatting(ChatFormatting.DARK_GRAY), null, false),
                true,
                widget().playerOption.title()
              )
            )
          )
        )
      );
    }
  }
}
