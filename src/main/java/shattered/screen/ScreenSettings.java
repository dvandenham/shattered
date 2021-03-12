package shattered.screen;

import shattered.Config;
import shattered.core.event.EventListener;
import shattered.lib.gui.Layout;
import shattered.lib.gui.component.GuiTab;
import shattered.lib.gui.component.GuiTabPanel;
import shattered.lib.gui.component.GuiToggleButton;
import org.jetbrains.annotations.NotNull;

public final class ScreenSettings extends AbstractScreen {

	private final GuiTabPanel tabPanel = new GuiTabPanel();

	ScreenSettings() {
		super("screen.settings.title");
		this.setSize(640, 480);
		this.add(this.tabPanel);
		this.tabPanel.addTab(new TabGeneral());
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		this.tabPanel.setBounds(this.getInternalBounds());
	}

	private static class TabGeneral extends GuiTab {

		private final GuiToggleButton toggleBootAnimation = new GuiToggleButton("screen.settings.tab.general.toggle.boot_animation");

		public TabGeneral() {
			super("screen.settings.tab.general.title");
			this.add(this.toggleBootAnimation);
			this.toggleBootAnimation.setState(Config.GLOBAL_BOOT_ANIMATION.get());
		}

		@Override
		public void setupComponents(@NotNull final Layout layout) {
			layout.add(this.toggleBootAnimation);
		}

		@EventListener(GuiToggleButton.ToggleButtonEvent.StateChanged.class)
		private void onToggleButtonSwitched(final GuiToggleButton.ToggleButtonEvent.StateChanged event) {
			final GuiToggleButton button = event.get();
			if (button == this.toggleBootAnimation) {
				Config.GLOBAL_BOOT_ANIMATION.set(button.isToggled());
			}
		}
	}
}