package shattered.lib.gui.component;

import shattered.Assets;
import shattered.core.ICacheable;
import shattered.lib.Color;
import shattered.lib.ResourceLocation;
import shattered.lib.StringUtils;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiComponent;
import org.jetbrains.annotations.NotNull;

public class GuiLabel extends IGuiComponent implements ICacheable {

	@NotNull
	private ResourceLocation font = Assets.FONT_DEFAULT;
	@NotNull
	private String text;
	@NotNull
	private Color textColor = Color.BLACK;
	private boolean localize = true;
	private boolean centerX = true, centerY = true;
	@NotNull
	private StringData stringDataCached;

	public GuiLabel(@NotNull final ResourceLocation font, @NotNull final String text, @NotNull final Color textColor) {
		this.font = font;
		this.text = text;
		this.textColor = textColor;
	}

	public GuiLabel(@NotNull final String text, @NotNull final Color textColor) {
		this.text = text;
		this.textColor = textColor;
	}

	public GuiLabel(@NotNull final String text) {
		this.text = text;
	}

	@Override
	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		if (!StringUtils.isNullOrEmpty(this.text)) {
			fontRenderer.setFont(this.font);
			fontRenderer.setFontSize(this.getHeight() / 3 * 2);
			fontRenderer.writeQuick(this.getX(), this.getY(), this.stringDataCached);
			fontRenderer.revertFontSize();
			fontRenderer.revertFontSize();
		}
	}

	@Override
	public void cache() {
		final StringData data = new StringData(this.text, this.textColor).localize(this.localize);
		if (this.centerX) {
			data.centerX(this.getWidth());
		}
		if (this.centerY) {
			data.centerY(this.getHeight());
		}
		this.stringDataCached = data;
	}

	@NotNull
	public final GuiLabel setFont(@NotNull final ResourceLocation font) {
		this.font = font;
		return this;
	}

	@NotNull
	public final GuiLabel setText(@NotNull final String text) {
		this.text = text;
		this.cache();
		return this;
	}

	@NotNull
	public final GuiLabel setTextColor(@NotNull final Color textColor) {
		this.textColor = textColor;
		this.cache();
		return this;
	}

	@NotNull
	public final GuiLabel setLocalize(final boolean localize) {
		this.localize = localize;
		this.cache();
		return this;
	}

	@NotNull
	public final GuiLabel setCenterX(final boolean centerX) {
		this.centerX = centerX;
		this.cache();
		return this;
	}

	@NotNull
	public final GuiLabel setCenterY(final boolean centerY) {
		this.centerY = centerY;
		this.cache();
		return this;
	}

	@NotNull
	public final ResourceLocation getFont() {
		return this.font;
	}

	@NotNull
	public final String getText() {
		return this.text;
	}

	@NotNull
	public final Color getTextColor() {
		return this.textColor;
	}

	public final boolean localize() {
		return this.localize;
	}

	public final boolean centerX() {
		return this.centerX;
	}

	public final boolean centerY() {
		return this.centerY;
	}
}