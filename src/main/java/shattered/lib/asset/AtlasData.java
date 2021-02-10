package shattered.lib.asset;

import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;
import shattered.lib.ResourceLocation;

final class AtlasData {

	@NotNull
	public final ResourceLocation resource;
	@NotNull
	public final BufferedImage    image;
	public final int              id;

	public AtlasData(@NotNull final ResourceLocation resource, @NotNull final BufferedImage image, final int id) {
		this.resource = resource;
		this.image = image;
		this.id = id;
	}
}