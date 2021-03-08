package shattered.game.tile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.ResourceLocation;

public final class Tile {

	@NotNull
	private final ResourceLocation resource;
	@NotNull
	private final ResourceLocation texture;
	@Nullable
	private final ResourceLocation updateScript;
	@Nullable
	private final ResourceLocation renderScript;

	Tile(@NotNull final ResourceLocation resource,
	     @NotNull final ResourceLocation texture,
	     @Nullable final ResourceLocation updateScript,
	     @Nullable final ResourceLocation renderScript) {
		this.resource = resource;
		this.texture = texture;
		this.updateScript = updateScript;
		this.renderScript = renderScript;
	}

	@NotNull
	public ResourceLocation getResource() {
		return this.resource;
	}

	@NotNull
	public ResourceLocation getTexture() {
		return this.texture;
	}

	@Nullable
	public ResourceLocation getUpdateScript() {
		return this.updateScript;
	}

	@Nullable
	public ResourceLocation getRenderScript() {
		return this.renderScript;
	}
}