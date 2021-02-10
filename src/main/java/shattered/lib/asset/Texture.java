package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;

public abstract class Texture extends IAsset {

	Texture(@NotNull final ResourceLocation resource) {
		super(resource);
	}

	public abstract int getTextureId();

	@NotNull
	public abstract Rectangle getUv();

	@NotNull
	public abstract Dimension getImageSize();

	@NotNull
	public abstract Dimension getTextureSize();
}