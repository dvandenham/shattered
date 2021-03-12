package shattered.screen;

import java.io.IOException;
import shattered.Assets;
import shattered.Shattered;
import shattered.core.event.EventListener;
import shattered.game.InvalidSaveException;
import shattered.game.SaveData;
import shattered.lib.Color;
import shattered.lib.Localizer;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.GuiPopupNotice;
import shattered.lib.gui.Layout;
import shattered.lib.gui.ScreenEvent;
import shattered.lib.gui.component.GuiButton;
import shattered.lib.gui.component.GuiList;
import org.jetbrains.annotations.NotNull;

public class ScreenSaveList extends AbstractScreen {

	private final GuiList listSaves = new GuiList().localize(false);

	ScreenSaveList() {
		super("screen.save_manager.list.title");
		this.add(this.listSaves);
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		this.listSaves.setBounds(this.getInternalBounds());
		this.makeList();
	}

	private void makeList() {
		this.listSaves.reset();
		try {
			final SaveData[] saves = Shattered.getInstance().getGameManager().getSaveManager().listSaves();
			for (int i = 0; i < saves.length; ++i) {
				this.listSaves.add(saves[i].getDisplayName(), new ButtonLoadVersions(saves[i], i));
			}
		} catch (final IOException e) {
			this.closeScreen();
			this.openScreen(new GuiPopupNotice(new StringData(Localizer.format("screen.save_manager.list.popup.error.io", e.getMessage())), true));
		} catch (final InvalidSaveException e) {
			final String translationKey;
			switch (e.getReason()) {
				case METADATA_MISSING:
					translationKey = "screen.save_manager.list.popup.error.meta_missing";
					break;
				case METADATA_CORRUPT:
					translationKey = "screen.save_manager.list.popup.error.meta_corrupt";
					break;
				default:
					translationKey = "screen.save_manager.list.popup.error.generic";
					break;

			}
			final StringData message = new StringData(Localizer.format(translationKey, e.getDetail()));
			this.closeScreen();
			this.openScreen(new GuiPopupNotice(message, true));
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
			super("screen.save_manager.list.button.load_versions", index % 2 == 0 ? Color.WHITE : Color.BLACK);
			this.setMaximumWidth(128);
			this.save = save;
		}

		@Override
		public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
			switch (this.state) {
				case ROLLOVER:
				case LEFT_PRESS:
				case RIGHT_PRESS:
				case LEFT_CLICK:
				case RIGHT_CLICK:
					tessellator.drawQuick(this.getBounds(), Color.XEROS);
					final Color color = this.getTextColor();
					this.setTextColor(Color.BLACK);
					this.renderForeground(tessellator, fontRenderer);
					this.setTextColor(color);
					break;
				default:
					this.renderForeground(tessellator, fontRenderer);
			}
		}

		private void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
			fontRenderer.setFont(Assets.FONT_SIMPLE);
			fontRenderer.setFontSize(Math.min(this.getHeight() / 4 * 3, 48));
			final int yOffset = 2;
			fontRenderer.writeQuickCentered(this.getBounds().moveY(yOffset), new StringData(this.getText(), this.getTextColor()).localize(this.doLocalize()));
			fontRenderer.revertFontSize();
			fontRenderer.resetFont();
		}
	}
}