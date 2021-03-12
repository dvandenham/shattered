package shattered.lib.gui.component;

import shattered.lib.ResourceLocation;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiComponent;
import org.jetbrains.annotations.NotNull;

public class GuiTextureLabel extends IGuiComponent {

	@NotNull
	private ResourceLocation texture;
	private boolean centerX = true, centerY = true;
	private boolean useAspectX = false, useAspectY = false;

	public GuiTextureLabel(@NotNull final ResourceLocation texture) {
		this.texture = texture;
	}

	@Override
	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		final int width = this.useAspectX ? -1 : this.getWidth();
		final int height = this.useAspectY ? -1 : this.getHeight();

		tessellator.start();
		tessellator.set(this.getX(), this.getY(), width, height, this.texture);
		if (this.centerX) {
			tessellator.centerX(this.getWidth());
		}
		if (this.centerY) {
			tessellator.centerY(this.getHeight());
		}
		tessellator.draw();
	}

	@NotNull
	public final GuiTextureLabel setTexture(@NotNull final ResourceLocation texture) {
		this.texture = texture;
		return this;
	}

	@NotNull
	public final GuiTextureLabel setCenterX(final boolean centerX) {
		this.centerX = centerX;
		return this;
	}

	@NotNull
	public final GuiTextureLabel setCenterY(final boolean centerY) {
		this.centerY = centerY;
		return this;
	}

	@NotNull
	public final GuiTextureLabel setUseAspectX(final boolean useAspectX) {
		this.useAspectX = useAspectX;
		return this;
	}

	@NotNull
	public final GuiTextureLabel setUseAspectY(final boolean useAspectY) {
		this.useAspectY = useAspectY;
		return this;
	}

	@NotNull
	public final ResourceLocation getTexture() {
		return this.texture;
	}

	public final boolean centerX() {
		return this.centerX;
	}

	public final boolean centerY() {
		return this.centerY;
	}

	public final boolean useAspectX() {
		return this.useAspectX;
	}

	public final boolean useAspectY() {
		return this.useAspectY;
	}
}