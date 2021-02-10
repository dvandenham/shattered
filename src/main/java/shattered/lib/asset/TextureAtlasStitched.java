package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;

final class TextureAtlasStitched extends TextureAtlasDefault {

	private final int       usableWidth;
	private final Dimension spriteSize;

	public TextureAtlasStitched(
			@NotNull final ResourceLocation resource,
			@NotNull final AtlasStitcher atlas,
			final int atlasId,
			@NotNull final Dimension imageSize,
			final int usableWidth,
			@NotNull final Dimension spriteSize
	) {
		super(resource, atlas, atlasId, imageSize);
		this.usableWidth = usableWidth;
		this.spriteSize = spriteSize;
	}

	@NotNull
	public TextureAtlasSub getSubTexture(final int index) {
		final int x = (index % (this.usableWidth / this.spriteSize.getWidth())) * this.spriteSize.getWidth();
		final int y = (index / (this.usableWidth / this.spriteSize.getWidth())) * this.spriteSize.getHeight();
		return new TextureAtlasSub(this.getResource().toVariant(String.valueOf(index)), this, this.getImageSize(), Rectangle.create(x, y, this.spriteSize));
	}
}