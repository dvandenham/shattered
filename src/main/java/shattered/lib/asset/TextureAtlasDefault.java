package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;

class TextureAtlasDefault extends Texture implements TextureAtlas {

	private final Dimension imageSize;
	private final Rectangle uv;
	final AtlasStitcher atlas;
	final int atlasId;

	public TextureAtlasDefault(@NotNull final ResourceLocation resource, @NotNull final AtlasStitcher atlas, final int atlasId, @NotNull final Dimension imageSize, @Nullable final Rectangle uv) {
		super(resource);
		this.atlas = atlas;
		this.atlasId = atlasId;
		this.imageSize = imageSize.toImmutable();
		this.uv = uv == null ? Rectangle.create(0, 0, imageSize) : uv.toImmutable();
	}

	public TextureAtlasDefault(@NotNull final ResourceLocation resource, @NotNull final AtlasStitcher atlas, final int atlasId, @NotNull final Dimension imageSize) {
		this(resource, atlas, atlasId, imageSize, null);
	}

	@Override
	@NotNull
	public final AtlasStitcher getAtlas() {
		return this.atlas;
	}

	@Override
	@Nullable
	public Rectangle getRealUv() {
		return this.atlas.getRealUv(this.atlasId);
	}

	@Override
	public int getTextureId() {
		return this.atlas.textureId;
	}

	@Override
	@NotNull
	public Rectangle getUv() {
		return this.uv;
	}

	@Override
	@NotNull
	public Dimension getImageSize() {
		return this.imageSize;
	}

	@Override
	@NotNull
	public Dimension getTextureSize() {
		return this.uv.getSize();
	}
}