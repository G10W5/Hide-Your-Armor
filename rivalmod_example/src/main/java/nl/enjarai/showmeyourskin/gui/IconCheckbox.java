package nl.enjarai.showmeyourskin.gui;

import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.widget.StatelessWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.SpriteWidget;
import io.wispforest.owo.braid.widgets.basic.Builder;
import io.wispforest.owo.braid.widgets.checkbox.Checkbox;
import io.wispforest.owo.braid.widgets.checkbox.TogglingClickable;
import io.wispforest.owo.braid.widgets.focus.Focusable;
import io.wispforest.owo.braid.widgets.stack.Stack;
import io.wispforest.owo.braid.widgets.stack.StackBase;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

public class IconCheckbox extends StatelessWidget {
  private final Identifier activeTexture;
  private final Identifier inactiveTexture;
  private final boolean checked;
  private final @Nullable TogglingClickable.CheckboxCallback onUpdate;

  public IconCheckbox(Identifier activeTexture, Identifier inactiveTexture, boolean checked, boolean active, @Nullable TogglingClickable.CheckboxCallback onUpdate) {
    this.activeTexture = activeTexture;
    this.inactiveTexture = inactiveTexture;
    this.checked = checked;
    this.onUpdate = active ? onUpdate : null;
  }

  @Override
  public Widget build(BuildContext context) {
    var background = new Builder(context2 ->
      new SpriteWidget(Focusable.shouldShowHighlight(context2) ? Checkbox.HIGHLIGHTED_TEXTURE : Checkbox.TEXTURE));

    return new TogglingClickable(
      this.checked,
      this.onUpdate,
      SoundEvents.UI_BUTTON_CLICK.value(),
      new Stack(new StackBase(background), new SpriteWidget(this.checked ? activeTexture : inactiveTexture))
    );
  }
}
