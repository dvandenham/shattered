package shattered.screen;

import shattered.Shattered;
import shattered.core.event.EventListener;
import shattered.game.SaveData;
import shattered.lib.Color;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.Layout;
import shattered.lib.gui.ScreenEvent;
import shattered.lib.gui.component.GuiButton;
import shattered.lib.gui.component.GuiList;
import org.jetbrains.annotations.NotNull;

public class ScreenSaveList extends AbstractScreen {

	private final GuiList listSaves = new GuiList().localize(false);

	ScreenSaveList() {
		super("screen.save_list.title");
		this.add(this.listSaves);
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		this.listSaves.setBounds(this.getInternalBounds());
		this.makeList();
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
	}

	private void makeList() {
		this.listSaves.reset();
		final SaveData[] saves = Shattered.getInstance().getGameManager().getSaveManager().listSaves();
		for (int i = 0; i < saves.length; ++i) {
			this.listSaves.add(saves[i].getDisplayName(), new ButtonLoadVersions(saves[i], i));
		}
	}

	@EventListener(GuiButton.ButtonEvent.LeftClick.class)
	private void onButtonClicked(final GuiButton.ButtonEvent.LeftClick event) {
		if (event.get() instanceof ButtonLoadVersions) {
			this.openScreen(new ScreenSaveVersions(((ButtonLoadVersions) event.get()).save));
		}
	}

	@EventListener(ScreenEvent.Closing.class)
	private void onSaveVersionsScreenClosing(final ScreenEvent.Closing event) {
		if (event.get() instanceof ScreenSaveVersions) {
			this.makeList();
		}
	}

	private static class ButtonLoadVersions extends GuiButton {

		@NotNull
		private final SaveData save;

		private ButtonLoadVersions(@NotNull final SaveData save, final int index) {
			super("screen.save_list.button.load_versions", index % 2 == 0 ? Color.WHITE : Color.BLACK);
			this.setMaximumWidth(128);
			this.save = save;
		}

		@Override
		public void renderBackground(@NotNull final Tessellator Tessellator, @NotNull final FontRenderer FontRenderer) {
			switch (this.state) {
				case DEFAULT:
					return;
				case ROLLOVER:
				case LEFT_PRESS:
				case RIGHT_PRESS:
				case LEFT_CLICK:
				case RIGHT_CLICK:
					Tessellator.drawQuick(this.getBounds(), Color.XEROS);
					break;
			}
		}

		@Override
		public void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
			switch (this.state) {
				case ROLLOVER:
				case LEFT_PRESS:
				case RIGHT_PRESS:
				case LEFT_CLICK:
				case RIGHT_CLICK: {
					final Color color = this.getTextColor();
					this.setTextColor(Color.BLACK);
					super.renderForeground(tessellator, fontRenderer);
					this.setTextColor(color);
					break;
				}
				default:
					super.renderForeground(tessellator, fontRenderer);
			}
		}
	}
}