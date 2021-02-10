package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.FastNamedObjectMap;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;

final class TextureAtlasMapped extends TextureAtlasDefault {

	private final FastNamedObjectMap<Rectangle> mapping;

	public TextureAtlasMapped(
			@NotNull final ResourceLocation resource,
			@NotNull final AtlasStitcher atlas,
			final int atlasId,
			@NotNull final Dimension imageSize,
			@NotNull final FastNamedObjectMap<Rectangle> mapping
	) {
		super(resource, atlas, atlasId, imageSize);
		this.mapping = mapping;
	}
	
	@Nullable
	public TextureAtlasSub getSubTexture(@NotNull final String name) {
		final Rectangle bounds = this.mapping.get(name);
		if (bounds == null) {
			return null;
		}
		return new TextureAtlasSub(this.getResource().toVariant(name), this, this.getImageSize(), bounds);
	}
}