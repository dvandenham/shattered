package shattered.game.world;

import shattered.core.nbtx.NBTX;
import shattered.game.GameRegistries;
import shattered.game.ISerializable;
import shattered.game.entity.Entity;
import shattered.game.entity.EntityType;
import shattered.game.tile.Tile;
import shattered.lib.ReflectionHelper;
import shattered.lib.ResourceLocation;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbox2d.dynamics.Body;
import org.jetbrains.annotations.NotNull;

public final class World implements ISerializable {

	public static final int TILE_SIZE = 32;
	public static final ResourceLocation DEATH_BARRIER_RESOURCE = new ResourceLocation("death_barrier");
	static final Logger LOGGER = LogManager.getLogger("World");

	private final TileContainer tiles = new TileContainer(this);
	private final ObjectArrayList<Entity> entities = new ObjectArrayList<>();
	@NotNull
	private final ResourceLocation resource;
	@NotNull
	private final WorldType type;
	@NotNull
	private final WorldPhysics physics;

	private World(@NotNull final ResourceLocation resource, @NotNull final WorldType type) {
		this.resource = resource;
		this.type = type;
		this.physics = new WorldPhysics();
		//Add death barrier and player
		this.physics.addWorldBody(World.DEATH_BARRIER_RESOURCE, Rectangle.create(0, -10, type.getStructure().getWorldSize().getWidth(), 10));
		this.createPlayer();
		this.physics.addCollisionListener((body1, body2, obj1, obj2) -> {
			if (obj1.resource.equals(World.DEATH_BARRIER_RESOURCE)) {
				this.physics.physics.destroyBody(body2);
			} else if (obj2.resource.equals(World.DEATH_BARRIER_RESOURCE)) {
				this.physics.physics.destroyBody(body1);
			}
		});
		this.loadTiles(type.getStructure());
	}

	private void createPlayer() {
		final EntityType entityType = GameRegistries.ENTITY().get(new ResourceLocation("player"));
		if (entityType == null) {
			throw new RuntimeException("Player entity was not registered correctly!");
		}
		final Body body = this.physics.addEntityBody(
				entityType.getResource(),
				Rectangle.create(this.type.getPlayerPos().getX() * World.TILE_SIZE, this.type.getPlayerPos().getY() * World.TILE_SIZE, entityType.getEntitySize())
		);
		final Entity entity = ReflectionHelper.instantiate(Entity.class,
				ResourceLocation.class, entityType.getResource(),
				EntityType.class, entityType,
				Body.class, body
		);
		assert entity != null;
		this.entities.add(entity);
	}

	private void loadTiles(@NotNull final Structure structure) {
		final Tile[][] tiles = structure.getStructure();
		for (int x = 0; x < structure.getWorldSize().getWidth(); ++x) {
			for (int y = 0; y < structure.getWorldSize().getHeight(); ++y) {
				if (y < tiles.length) {
					if (x < tiles[y].length) {
						final Tile tile = tiles[y][x];
						if (tile != null) {
							this.tiles.addTile(Point.create(x, y), tile.getResource(), null);
						}
					}
				}
			}
		}
	}

	public void tick() {
		this.physics.tick();
	}

	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		tessellator.drawQuick(Display.getBounds(), this.type.getWallpaperTexture());
		//TODO render environment here

		this.tiles.render(tessellator, fontRenderer);

		this.entities.forEach(entity -> entity.render(tessellator, fontRenderer));
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