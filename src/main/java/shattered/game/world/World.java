package shattered.game.world;

import org.jetbrains.annotations.NotNull;
import shattered.core.nbtx.NBTX;
import shattered.game.ISerializable;
import shattered.lib.ResourceLocation;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;

public final class World implements ISerializable {

	@NotNull
	private final ResourceLocation resource;
	@NotNull
	private final WorldType type;

	private World(@NotNull final ResourceLocation resource, @NotNull final WorldType type) {
		this.resource = resource;
		this.type = type;
	}

	public void tick() {
	}

	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		tessellator.drawQuick(Display.getBounds(), this.type.wallpaperTexture);
	}

	@Override
	public @NotNull NBTX serialize(@NotNull final NBTX store) {
		return store;
	}

	@Override
	public void deserialize(@NotNull final NBTX store) {
	}

	@NotNull
	public ResourceLocation getResource() {
		return this.resource;
	}

	@NotNull
	public WorldType getType() {
		return this.type;
	}
}