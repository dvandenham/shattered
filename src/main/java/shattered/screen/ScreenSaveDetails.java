package shattered.screen;

import java.io.IOException;
import java.util.Date;
import shattered.Assets;
import shattered.Shattered;
import shattered.core.event.EventListener;
import shattered.game.SaveData;
import shattered.game.world.World;
import shattered.lib.Color;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.MatrixUtils;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gfx.TessellatorImpl;
import shattered.lib.gui.Layout;
import shattered.lib.gui.component.GuiButton;
import shattered.screen.component.GlitchButton;
import org.jetbrains.annotations.NotNull;

public final class ScreenSaveDetails extends AbstractScreen {

	private final GuiButton buttonLoad = new GlitchButton("screen.save_manager.details.button.load");
	@NotNull
	private final SaveData save;
	@NotNull
	private final String uuid;
	@NotNull
	private final World world;
	@NotNull
	private final String dateModified;

	ScreenSaveDetails(@NotNull final SaveData save, @NotNull final String uuid) {
		super("screen.save_manager.details.title");
		this.save = save;
		this.uuid = uuid;
		try {
			final World world = Shattered.getInstance().getGameManager().getSaveManager().deserializeWorld(save, uuid);
			if (world == null) {
				throw new NullPointerException();
			}
			this.world = world;
		} catch (final IOException e) {
			//TODO error popup
			throw new RuntimeException(e);
		}
		this.dateModified = new Date(this.save.listVersionData().get(uuid)).toString();
		this.add(this.buttonLoad);
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		layout.setInverted();
		layout.add(this.buttonLoad);
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		((TessellatorImpl) tessellator).setUniformMatrix(MatrixUtils.identity().translate(this.getInternalX(), this.getInternalY() + 48, 0).scale(0.5F));
		Display.setLogicalResolution(600, 480);
		this.world.render(tessellator, fontRenderer);
		Display.resetLogicalResolution();
		((TessellatorImpl) tessellator).resetUniformMatrix();

		//Render name
		fontRenderer.writeQuick(this.getInternalX(), this.getInternalY(), new StringData(this.save.getDisplayName()).centerX(this.getInternalWidth()).localize(false));
		//Render info
		fontRenderer.setFont(Assets.FONT_SIMPLE);
		//      Headers
		fontRenderer.setFontSize(32);
		fontRenderer.start();
		fontRenderer.add(this.getInternalBounds().getCenterX() + 48, this.getInternalY() + 48, "screen.save_manager.details.info.resource", Color.XEROS);
		fontRenderer.add(this.getInternalBounds().getCenterX() + 48, this.getInternalY() + 110, "screen.save_manager.details.info.date", Color.XEROS);
		fontRenderer.write();
		fontRenderer.revertFontSize();
		//      Data
		fontRenderer.setFontSize(24);
		fontRenderer.start();
		fontRenderer.add(this.getInternalBounds().getCenterX() + 64, this.getInternalY() + 80, new StringData(this.save.getWorldId().toString()).localize(false));
		fontRenderer.add(this.getInternalBounds().getCenterX() + 64, this.getInternalY() + 140, new StringData(this.dateModified).localize(false));
		fontRenderer.write();
		fontRenderer.revertFontSize();
		fontRenderer.resetFont();
	}

	@EventListener(GuiButton.ButtonEvent.LeftClick.class)
	private void onButtonClicked(final GuiButton.ButtonEvent.LeftClick event) {
		if (event.get() == this.buttonLoad) {
			final boolean didLoad = Shattered.getInstance().getGameManager().loadWorld(this.save, this.uuid);
			if (didLoad) {
				Shattered.getInstance().getGuiManager().closeAllScreens();
			} else {
				//TODO error popup
			}
		}
	}
}