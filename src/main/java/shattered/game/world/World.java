package shattered.game.world;

import org.jetbrains.annotations.NotNull;
import shattered.core.nbtx.NBTX;
import shattered.game.GameRegistries;
import shattered.game.ISerializable;
import shattered.game.entity.EntityType;
import shattered.lib.ResourceLocation;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.math.Rectangle;

public final class World implements ISerializable {

	public static final ResourceLocation DEATH_BARRIER_RESOURCE = new ResourceLocation("death_barrier");

	@NotNull
	private final ResourceLocation resource;
	@NotNull
	private final WorldType type;
	@NotNull
	private final WorldPhysics physics;

	private World(@NotNull final ResourceLocation resource, @NotNull final WorldType type) {
		this.resource = resource;
		this.type = type;
		this.physics = new WorldPhysics(type.worldSize);
		//Add death barrier and player
		this.physics.addWorldBody(World.DEATH_BARRIER_RESOURCE, Rectangle.create(0, -10, type.worldSize.getWidth(), 10));
		final EntityType entityTypePlayer = GameRegistries.ENTITY().get(new ResourceLocation("player"));
		if (entityTypePlayer == null) {
			throw new RuntimeException("Player entity was not registered correctly!");
		}
		this.physics.addEntityBody(new ResourceLocation("player"), Rectangle.create(type.playerPos, entityTypePlayer.getEntitySize()));
		this.physics.addCollisionListener((body1, body2, obj1, obj2) -> {
			if (obj1.resource.equals(World.DEATH_BARRIER_RESOURCE)) {
				this.physics.physics.destroyBody(body2);
			} else if (obj2.resource.equals(World.DEATH_BARRIER_RESOURCE)) {
				this.physics.physics.destroyBody(body1);
			}
		});
	}

	public void tick() {
		this.physics.tick();
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