package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;

final class TextureAtlasSub extends TextureAtlasDefault {

	private final TextureAtlasDefault original;

	public TextureAtlasSub(
			@NotNull final ResourceLocation resource,
			@NotNull final TextureAtlasDefault original,
			@NotNull final Dimension imageSize,
			@NotNull final Rectangle uv
	) {
		super(resource, original.atlas, original.atlasId, imageSize, uv);
		this.original = original;
	}

	@Override
	@Nullable
	public Rectangle getRealUv() {
		return this.original.getUv();
	}
}