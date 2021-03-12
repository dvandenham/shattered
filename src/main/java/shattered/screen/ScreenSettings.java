package shattered.screen;

import shattered.Config;
import shattered.Shattered;
import shattered.core.event.EventListener;
import shattered.lib.Color;
import shattered.lib.Input;
import shattered.lib.KeyEvent;
import shattered.lib.Localizer;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.Layout;
import shattered.lib.gui.ScreenEvent;
import shattered.lib.gui.component.GuiButton;
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
		this.tabPanel.addTab(new TabInput());
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

	private static class TabInput extends GuiTab {

		private final GuiButton buttonKeyMoveLeft = new GuiButton("screen.settings.tab.input.button.key.move_left");
		private final GuiButton buttonKeyMoveRight = new GuiButton("screen.settings.tab.input.button.key.move_right");

		public TabInput() {
			super("screen.settings.tab.input.title");
			this.add(this.buttonKeyMoveLeft);
			this.add(this.buttonKeyMoveRight);
			this.cache();
		}

		private void cache() {
			this.buttonKeyMoveLeft.setText(Localizer.format("screen.settings.tab.input.button.key.move_left", Input.getKeyName(Config.KEY_GAME_LEFT.get())));
			this.buttonKeyMoveRight.setText(Localizer.format("screen.settings.tab.input.button.key.move_right", Input.getKeyName(Config.KEY_GAME_RIGHT.get())));
		}

		@Override
		public void setupComponents(@NotNull final Layout layout) {
			layout.add(this.buttonKeyMoveLeft, this.buttonKeyMoveRight);
		}

		@EventListener(GuiButton.ButtonEvent.LeftClick.class)
		private void onButtonClicked(final GuiButton.ButtonEvent.LeftClick event) {
			if (event.get() == this.buttonKeyMoveLeft) {
				Shattered.getInstance().getGuiManager().openScreen(
						new ScreenSetKeybind("key.game.move_left", Config.KEY_GAME_LEFT.get(), false, false, false)
				);
			} else if (event.get() == this.buttonKeyMoveRight) {
				Shattered.getInstance().getGuiManager().openScreen(
						new ScreenSetKeybind("key.game.move_right", Config.KEY_GAME_RIGHT.get())
				);
			}
		}

		@EventListener(ScreenEvent.Closing.class)
		private void onScreenClosing(final ScreenEvent.Closing event) {
			if (event.get() instanceof ScreenSetKeybind) {
				final ScreenSetKeybind screen = (ScreenSetKeybind) event.get();
				switch (screen.identifier) {
					case "key.game.move_left":
						Config.KEY_GAME_LEFT.set(screen.keyCode);
						break;
					case "key.game.move_right":
						Config.KEY_GAME_RIGHT.set(screen.keyCode);
						break;
				}
				this.cache();
			}
		}
	}

	private static final class ScreenSetKeybind extends AbstractScreen {

		private final GuiButton buttonStart = new GuiButton("screen.settings.tab.input.screen.keybind.button.start");
		private final GuiButton buttonStop = new GuiButton("screen.settings.tab.input.screen.keybind.button.stop");
		private final String identifier;
		private final boolean acceptMods;
		private int keyCode;
		private boolean shift, ctrl, alt;

		ScreenSetKeybind(@NotNull final String identifier, final int keyCode, final boolean shift, final boolean ctrl, final boolean alt) {
			super("screen.settings.tab.input.screen.keybind.title");
			this.identifier = identifier;
			this.keyCode = keyCode;
			this.acceptMods = true;
			this.shift = shift;
			this.ctrl = ctrl;
			this.alt = alt;
			this.setSize(640, 480);
			this.add(this.buttonStart);
		}

		ScreenSetKeybind(@NotNull final String identifier, final int keyCode) {
			super("screen.settings.tab.input.screen.keybind.title");
			this.identifier = identifier;
			this.keyCode = keyCode;
			this.acceptMods = false;
			this.setSize(640, 480);
			this.add(this.buttonStart);
		}

		@Override
		public void setupComponents(@NotNull final Layout layout) {
			layout.setInverted();
			layout.add(this.buttonStart);
			final Layout layout2 = layout.recreate();
			layout2.setInverted();
			layout2.add(this.buttonStop);
		}

		@Override
		protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
			if (this.acceptMods) {
				final StringBuilder text = new StringBuilder();
				if (this.shift) {
					text.append("SHIFT + ");
				}
				if (this.ctrl) {
					text.append("CTRL + ");
				}
				if (this.alt) {
					text.append("CTRL + ");
				}
				text.append(Input.getKeyName(this.keyCode));
				fontRenderer.writeQuickCentered(this.getBounds(), new StringData(text.toString(), Color.WHITE).localize(false));
			} else {
				fontRenderer.writeQuickCentered(this.getBounds(), new StringData(Input.getKeyName(this.keyCode), Color.WHITE).localize(false));
			}
		}

		@EventListener(value = GuiButton.ButtonEvent.LeftClick.class, bus = "DEFAULT")
		private void onButtonClicked(final GuiButton.ButtonEvent.LeftClick event) {
			if (event.get() == this.buttonStart) {
				this.remove(this.buttonStart);
				this.add(this.buttonStop);
				Input.INPUT_BUS.register(this);
				Input.enableEventBusMode();
				this.setHasTitlebar(false);
			} else if (event.get() == this.buttonStop) {
				this.add(this.buttonStart);
				this.remove(this.buttonStop);
				Input.INPUT_BUS.unregister(this);
				Input.disableEventBusMode();
				this.setHasTitlebar(true);
			}
		}

		@EventListener(ScreenEvent.Closing.class)
		private void onScreenClosing(final ScreenEvent.Closing event) {
			if (event.get() == this && !this.hasComponent(this.buttonStart)) {
				event.cancel();
			}
		}

		@EventListener(value = KeyEvent.class, bus = "INPUT")
		private void onKeyEvent(final KeyEvent event) {
			if (this.acceptMods) {
				if (event.isShift()) {
					this.shift = event.isPressed() || event.isRepeat();
					return;
				} else if (event.isCtrl()) {
					this.ctrl = event.isPressed() || event.isRepeat();
					return;
				} else if (event.isAlt()) {
					this.alt = event.isPressed() || event.isRepeat();
					return;
				}
			}
			this.keyCode = event.getKeyCode();
		}
	}
}