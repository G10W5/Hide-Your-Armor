package nl.enjarai.showmeyourskin.gui;

import io.wispforest.owo.braid.core.*;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.instance.WidgetTransform;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.Marquee;
import io.wispforest.owo.braid.widgets.SpriteWidget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.button.Clickable;
import io.wispforest.owo.braid.widgets.flex.CrossAxisAlignment;
import io.wispforest.owo.braid.widgets.flex.Flexible;
import io.wispforest.owo.braid.widgets.flex.MainAxisAlignment;
import io.wispforest.owo.braid.widgets.flex.Row;
import io.wispforest.owo.braid.widgets.label.Label;
import io.wispforest.owo.braid.widgets.label.LabelStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerListEntry extends StatefulWidget {
  private final PlayerOption playerOption;
  private final @Nullable Identifier skinTextureOverride;
  private final boolean selected;
  private final boolean deletable;
  private final Runnable onSelect;
  private final Runnable onDelete;

  public PlayerListEntry(PlayerOption playerOption, @Nullable Identifier skinTextureOverride, boolean selected, boolean deletable, Runnable onSelect, Runnable onDelete) {
    this.playerOption = playerOption;
    this.skinTextureOverride = skinTextureOverride;
    this.selected = selected;
    this.deletable = deletable;
    this.onSelect = onSelect;
    this.onDelete = onDelete;
  }

  @Override
  public WidgetState<?> createState() {
    return new State();
  }

  static class State extends WidgetState<PlayerListEntry> {
    Identifier skinTexture;

    public void init() {
      if (widget().skinTextureOverride == null) {
        if (widget().playerOption.player() != null) {
          //noinspection DataFlowIssue
          skinTexture = widget().playerOption.player().getSkin().body().texturePath();
        }

        var mc = Minecraft.getInstance();
        CompletableFuture
          .supplyAsync(() -> mc.services().profileResolver().fetchById(widget().playerOption.uuid()))
          .thenComposeAsync(profile -> mc.getSkinManager().get(profile.orElseThrow()))
          .thenAcceptAsync(skin -> mc.execute(() -> setState(() -> skinTexture = skin.orElseThrow().body().texturePath())));
      }
    }

    @Override
    public Widget build(BuildContext context) {
      return new Clickable(
        () -> {
          widget().onSelect.run();
          return true;
        },
        SoundEvents.UI_BUTTON_CLICK.value(),
        new Box(
          widget().selected ? new Color(0xff282828) : new Color(0x00000000),
          new Padding(
            Insets.all(2),
            new Row(
              MainAxisAlignment.START,
              CrossAxisAlignment.CENTER,
              new Padding(Insets.all(2)),
              List.of(
                new Sized(
                  16, 16,
                  new Box(
                    new Color(0xff161616),
                    new CustomDraw(this::drawHead)
                  )
                ),
                new Flexible(
                  new Marquee(
                    new Padding(
                      Insets.top(2),
                      new Label(
                        new LabelStyle(Alignment.LEFT, null, null, null),
                        false,
                        widget().playerOption.title()
                      )
                    )
                  )
                ),
                widget().deletable ? new Sized(
                  11, 11,
                  new Clickable(
                    () -> {
                      widget().onDelete.run();
                      return true;
                    },
                    SoundEvents.UI_BUTTON_CLICK.value(),
                    new HoverableBuilder(
                      (hoverableContext, hovered) -> new SpriteWidget(
                        ShowMeYourSkin.id(hovered ? "button/delete_override_highlighted" : "button/delete_override")
                      )
                    )
                  )
                ) : EmptyWidget.INSTANCE
              )
            )
          )
        )
      );
    }

    void drawHead(BraidGraphics graphics, WidgetTransform transform) {
      if (widget().skinTextureOverride != null) {
        //noinspection DataFlowIssue
        graphics.blit(RenderPipelines.GUI_TEXTURED, widget().skinTextureOverride, 0, 0, 0, 0, 16, 16, 16, 16);
      } else if (skinTexture != null) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, skinTexture, 0, 0, 8.0F, 8.0F, 16, 16, 8, 8, 64, 64);
        graphics.blit(RenderPipelines.GUI_TEXTURED, skinTexture, 0, 0, 40.0F, 8.0F, 16, 16, 8, 8, 64, 64);
      }
    }
  }
}
