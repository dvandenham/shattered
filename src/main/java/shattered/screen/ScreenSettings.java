package shattered.screen;

import org.jetbrains.annotations.NotNull;
import shattered.core.event.EventListener;
import shattered.Config;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.Layout;
import shattered.lib.gui.component.GuiToggleButton;

public final class ScreenSettings extends AbstractScreen {

	private final GuiToggleButton toggleBootAnimation = new GuiToggleButton("screen.settings.toggle.boot_animation");

	ScreenSettings() {
		super("screen.settings.title");
		this.setSize(640, 480);
		this.add(this.toggleBootAnimation);

		this.toggleBootAnimation.setState(Config.GLOBAL_BOOT_ANIMATION.get());
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		layout.add(this.toggleBootAnimation);
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
	}

	@EventListener(GuiToggleButton.ToggleButtonEvent.StateChanged.class)
	private void onToggleButtonSwitched(final GuiToggleButton.ToggleButtonEvent.StateChanged event) {
		final GuiToggleButton button = event.get();
		if (button == this.toggleBootAnimation) {
			Config.GLOBAL_BOOT_ANIMATION.set(button.isToggled());
		}
	}
}