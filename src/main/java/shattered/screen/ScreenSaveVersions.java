package shattered.screen;

import java.util.Date;
import java.util.Map;
import shattered.core.event.EventListener;
import shattered.game.SaveData;
import shattered.lib.Color;
import shattered.lib.Localizer;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.GuiHelper;
import shattered.lib.gui.GuiPopupYesNo;
import shattered.lib.gui.Layout;
import shattered.lib.gui.component.GuiButton;
import shattered.lib.gui.component.GuiList;
import shattered.screen.component.GlitchButton;
import org.jetbrains.annotations.NotNull;

public final class ScreenSaveVersions extends AbstractScreen {

	private final GuiButton buttonDelete = new GlitchButton("screen.save_versions.button.delete");
	private final GuiButton buttonRename = new GlitchButton("screen.save_versions.button.rename");
	private final GuiList listVersions = new GuiList().localize(false);
	@NotNull
	private final SaveData save;

	ScreenSaveVersions(@NotNull final SaveData save) {
		super(save.getDisplayName());
		this.localizeTitle(false);
		this.save = save;
		this.add(this.buttonDelete);
		this.add(this.buttonRename);
		this.add(this.listVersions);
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		layout.setInverted();
		layout.add(this.buttonDelete, this.buttonRename);
		this.listVersions.setBounds(this.getInternalBounds().growY(-layout.getComponentHeight() - GuiHelper.BORDER_SIZE));
		this.makeList();
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
	}

	private void makeList() {
		this.listVersions.reset();
		final String[] versions = this.save.listVersionsSorted();
		final Map<String, Long> dates = this.save.listVersionData();
		for (int i = 0; i < versions.length; ++i) {
			if (i == 0) {
				this.listVersions.add(Localizer.localize("screen.save_versions.list.name_newest"), new ButtonSaveDetails(versions[i], i));
			} else {
				this.listVersions.add(new Date(dates.get(versions[i])).toString(), new ButtonSaveDetails(versions[i], i));
			}
		}
	}

	@EventListener(GuiButton.ButtonEvent.LeftClick.class)
	private void onButtonClicked(final GuiButton.ButtonEvent.LeftClick event) {
		if (event.get() == this.buttonDelete) {
			this.openScreen(new GuiPopupYesNo("screen.save_versions.popup.delete", "screen.save_versions.popup.delete.description"));
		} else if (event.get() == this.buttonRename) {
			//TODO rename here
		} else if (event.get() instanceof ButtonSaveDetails) {
			this.openScreen(new ScreenSaveDetails(this.save, ((ButtonSaveDetails) event.get()).uuid));
		}
	}

	@EventListener(GuiPopupYesNo.ResultEvent.class)
	private void onDeletePopupAnswered(final GuiPopupYesNo.ResultEvent event) {
		assert event.get() != null;
		if (event.get().equals("screen.save_versions.popup.delete") && event.answer) {
			this.save.delete();
			this.closeScreen();
		}
	}

	private static class ButtonSaveDetails extends GuiButton {

		@NotNull
		private final String uuid;

		private ButtonSaveDetails(@NotNull final String uuid, final int index) {
			super("screen.save_versions.button.details", index % 2 == 0 ? Color.WHITE : Color.BLACK);
			this.setMaximumWidth(128);
			this.uuid = uuid;
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