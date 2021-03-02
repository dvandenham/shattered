package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;

final class TextureSimple extends Texture {

	private final int textureId;
	private final Dimension imageSize, textureSize;
	private final Rectangle uv;

	public TextureSimple(@NotNull final ResourceLocation resource, final int textureId, @NotNull final Dimension imageSize, @NotNull final Dimension textureSize, @NotNull final Rectangle uv) {
		super(resource);
		this.textureId = textureId;
		this.imageSize = imageSize;
		this.textureSize = textureSize;
		this.uv = uv;
	}

	@Override
	public int getTextureId() {
		return this.textureId;
	}

	@NotNull
	@Override
	public Rectangle getUv() {
		return this.uv;
	}

	@NotNull
	@Override
	public Dimension getImageSize() {
		return this.imageSize;
	}

	@NotNull
	@Override
	public Dimension getTextureSize() {
		return this.textureSize;
	}
}