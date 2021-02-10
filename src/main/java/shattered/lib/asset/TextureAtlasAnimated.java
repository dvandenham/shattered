package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;

final class TextureAtlasAnimated extends TextureAtlasDefault implements TextureAnimated {

	private final double            fps;
	private final int[]             frameMapping;
	private final int               frames;
	private final TextureAtlasSub[] sprites;

	TextureAtlasAnimated(
			@NotNull final ResourceLocation resource,
			@NotNull final AtlasStitcher atlas,
			final int atlasId,
			@NotNull final Dimension imageSize,
			final double fps,
			final int frames,
			@Nullable final int[] frameMapping,
			final boolean horizontal
	) {
		super(resource, atlas, atlasId, imageSize);
		this.fps = fps;
		this.frameMapping = frameMapping;
		this.frames = frameMapping != null ? frameMapping.length : frames;
		this.sprites = new TextureAtlasSub[horizontal ? imageSize.getWidth() / imageSize.getHeight() : imageSize.getHeight() / imageSize.getWidth()];
		for (int i = 0; i < this.sprites.length; ++i) {
			final int       size     = horizontal ? imageSize.getHeight() : imageSize.getWidth();
			final int       indexPos = i * size;
			final Rectangle bounds   = Rectangle.create(horizontal ? indexPos : 0, horizontal ? 0 : indexPos, size, size);
			this.sprites[i] = new TextureAtlasSub(this.getResource().toVariant(String.valueOf(i)), this, this.getImageSize(), bounds);
		}
	}

	@NotNull
	@Override
	public Texture currentFrame() {
		return this.sprites[this.currentIndex()];
	}

	private int currentIndex() {
		final long   time         = System.currentTimeMillis() % 1000;
		final double timePerFrame = 1000.0 / this.fps;
		final int    frame        = (int) Math.floor((time % timePerFrame) / this.frames);
		return this.frameMapping == null ? frame : this.frameMapping[frame];
	}
}