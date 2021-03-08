package shattered.game.world;

import shattered.lib.ResourceLocation;
import shattered.lib.math.Point;
import org.jetbrains.annotations.NotNull;

public final class WorldType {

	@NotNull
	private final ResourceLocation resource;
	@NotNull
	private final String displayName;
	@NotNull
	private final ResourceLocation wallpaperTexture;
	@NotNull
	private final Point playerPos;
	@NotNull
	private final Structure structure;

	WorldType(@NotNull final ResourceLocation resource,
	          @NotNull final String displayName,
	          @NotNull final ResourceLocation wallpaperTexture,
	          @NotNull final Point playerPos,
	          @NotNull final Structure structure) {
		this.resource = resource;
		this.displayName = displayName;
		this.wallpaperTexture = wallpaperTexture;
		this.playerPos = playerPos;
		this.structure = structure;
	}

	@NotNull
	public ResourceLocation getResource() {
		return this.resource;
	}

	@NotNull
	public String getDisplayName() {
		return this.displayName;
	}

	@NotNull
	public ResourceLocation getWallpaperTexture() {
		return this.wallpaperTexture;
	}

	@NotNull
	public Point getPlayerPos() {
		return this.playerPos;
	}

	@NotNull
	public Structure getStructure() {
		return this.structure;
	}
}