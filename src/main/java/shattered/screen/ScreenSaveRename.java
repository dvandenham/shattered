package shattered.screen;

import java.io.IOException;
import shattered.core.event.EventListener;
import shattered.game.SaveData;
import shattered.lib.Color;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.GuiHelper;
import shattered.lib.gui.GuiPopupNotice;
import shattered.lib.gui.IGuiScreen;
import shattered.lib.gui.Layout;
import shattered.lib.gui.component.GuiButton;
import shattered.lib.gui.component.GuiTextField;
import org.jetbrains.annotations.NotNull;

public final class ScreenSaveRename extends IGuiScreen {

	private final GuiButton buttonCancel = new GuiButton("screen.save_manager.rename.button.cancel", Color.BLACK);
	private final GuiButton buttonConfirm = new GuiButton("screen.save_manager.rename.button.confirm", Color.BLACK);
	private final GuiTextField nameField = new GuiTextField();

	@NotNull
	private final SaveData save;

	ScreenSaveRename(@NotNull final SaveData save) {
		this.save = save;
		this.setHasTitlebar(false);
		this.setBounds(-1, -1, -2, -2);
		this.setMaxSize(480, 320);
		this.add(this.buttonCancel);
		this.add(this.buttonConfirm);
		this.add(this.nameField);
		this.nameField.setText(save.getDisplayName());
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		layout.setInverted();
		layout.add(this.buttonCancel, this.buttonConfirm);
		layout.addEmptyRow();
		layout.add(this.nameField);
	}

	@Override
	protected void renderBackground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		GuiHelper.renderGuiPanel(tessellator, this.getBounds());
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
	}

	@EventListener(GuiButton.ButtonEvent.LeftClick.class)
	private void onButtonClicked(final GuiButton.ButtonEvent.LeftClick event) {
		if (event.get() == this.buttonCancel) {
			this.closeScreen();
		} else if (event.get() == this.buttonConfirm) {
			try {
				this.save.setDisplayName(this.nameField.getText());
				this.save.save();
				this.closeScreen();
			} catch (final IOException e) {
				this.openScreen(new GuiPopupNotice("screen.save_manager.rename.popup.error.io.description", true));
			}
		}
	}
}