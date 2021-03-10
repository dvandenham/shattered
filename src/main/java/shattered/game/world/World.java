package shattered.game.world;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import shattered.core.LuaMachine;
import shattered.core.LuaScript;
import shattered.core.lua.LuaSerializer;
import shattered.core.lua.constant.LuaConstantStateContainer;
import shattered.core.lua.lib.LuaLibTile;
import shattered.core.lua.lib.LuaLibWorld;
import shattered.core.sdb.SDBArray;
import shattered.core.sdb.SDBTable;
import shattered.core.sdb.SDBTag;
import shattered.game.Direction;
import shattered.game.GameRegistries;
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
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;

public final class World {

	public static final int TILE_SIZE = 32;
	public static final ResourceLocation DEATH_BARRIER_RESOURCE = new ResourceLocation("death_barrier");
	static final Logger LOGGER = LogManager.getLogger("World");
	private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
	@NotNull
	private final ResourceLocation resource;
	@NotNull
	private final WorldType type;

	private final Object2ObjectArrayMap<Point, Tile> tiles = new Object2ObjectArrayMap<>();
	private final Object2ObjectArrayMap<Point, Rectangle> tilesBoundCache = new Object2ObjectArrayMap<>();
	private final Object2ObjectArrayMap<Point, LuaTable> tileStates = new Object2ObjectArrayMap<>();
	private final Object2ObjectArrayMap<Point, LuaScript> tileUpdateScripts = new Object2ObjectArrayMap<>();
	private final Object2ObjectArrayMap<Point, LuaScript> tileRenderScripts = new Object2ObjectArrayMap<>();

	private final ObjectArrayList<Entity> entities = new ObjectArrayList<>();
	@Nullable
	private Entity player;

	private World(@NotNull final ResourceLocation resource, @NotNull final WorldType type) {
		this.resource = resource;
		this.type = type;
		this.player = this.createPlayer();
		this.loadTiles(type.getStructure());
	}

	private Entity createPlayer() {
		final EntityType entityType = GameRegistries.ENTITY().get(new ResourceLocation("player"));
		if (entityType == null) {
			throw new RuntimeException("Player entity was not registered correctly!");
		}
		final Rectangle playerPos = Rectangle.create(this.type.getPlayerPos().getX() * World.TILE_SIZE, this.type.getPlayerPos().getY() * World.TILE_SIZE, entityType.getEntitySize());
		final Entity entity = ReflectionHelper.instantiate(Entity.class,
				EntityType.class, entityType,
				World.class, this,
				Rectangle.class, playerPos
		);
		assert entity != null;
		this.entities.add(entity);
		return entity;
	}

	private void loadTiles(@NotNull final Structure structure) {
		final Tile[][] tiles = structure.getStructure();
		for (int x = 0; x < structure.getWorldSize().getWidth(); ++x) {
			for (int y = 0; y < structure.getWorldSize().getHeight(); ++y) {
				if (y < tiles.length && x < tiles[y].length) {
					final Tile tile = tiles[y][x];
					if (tile != null) {
						this.addTile(Point.create(x, structure.getWorldSize().getHeight() - 1 - y), tile.getResource(), null);
					}
				}
			}
		}
	}

	public void tick() {
		while (!this.queue.isEmpty()) {
			this.queue.poll().run();
		}
		this.tileUpdateScripts.values().forEach(LuaScript::executeScript);
		this.entities.forEach(Entity::tick);
	}

	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		tessellator.drawQuick(Display.getBounds(), this.type.getWallpaperTexture());
		//TODO render environment here

		this.tiles.forEach((position, tile) -> {
			final Rectangle bounds = this.tilesBoundCache.get(position).toImmutable();
			final Rectangle renderBounds = bounds.setY(Display.getHeight() - bounds.getHeight() - bounds.getY());
			tessellator.drawQuick(renderBounds, tile.getTexture());
		});
		this.tileRenderScripts.values().forEach(LuaScript::executeScript);

		this.entities.forEach(entity -> entity.render(tessellator, fontRenderer));
	}

	@NotNull
	public SDBTable serialize(@NotNull final SDBTable store) {
		store.set("id", this.type.getResource().toString());
		store.set("tile_size", World.TILE_SIZE);

		final SDBArray tiles = store.newArray("tiles");
		this.tiles.forEach((position, tile) -> {
			final SDBTable table = tiles.addTable();
			table.set("id", tile.getResource().toString());
			table.set("position", position);
			table.set("bounds", this.tilesBoundCache.get(position));
			final SDBTag state = LuaSerializer.serializeTable(this.tileStates.get(position));
			if (state != null) {
				table.setTag("state", state);
			}
		});

		final SDBArray entities = store.newArray("entities");
		this.entities.stream()
				.filter(entity -> entity != this.player)
				.forEach(entity -> entities.addTag(entity.serialize(new SDBTable())));

		if (this.player != null) {
			store.setTag("player", this.player.serialize(new SDBTable()));
		}

		return store;
	}

	@SuppressWarnings("ConstantConditions")
	public void deserialize(@NotNull final SDBTable store) {
		//TODO handle tile_size

		this.tiles.clear();
		this.tilesBoundCache.clear();
		this.tileStates.clear();
		this.tileUpdateScripts.clear();
		this.tileRenderScripts.clear();
		final SDBArray tiles = store.getArray("tiles");
		for (int i = 0; i < tiles.getSize(); ++i) {
			final SDBTable table = tiles.getTable(i);
			final Point position = table.getPoint("position");
			final LuaTable state = LuaSerializer.deserializeTable(table.getTag("state"));
			this.addTile(position, new ResourceLocation(table.getString("id")), state);
		}

		this.entities.clear();
		final SDBArray entities = store.getArray("entities");
		for (int i = 0; i < entities.getSize(); ++i) {
			final SDBTable table = entities.getTable(i);
			final Entity entity = ReflectionHelper.invokeMethod(Entity.class, null, Entity.class, World.class, this, SDBTable.class, table);
			this.entities.add(entity);
		}

		if (store.hasTag("player")) {
			this.player.deserialize(store.getTable("player"));
		}
		this.entities.add(this.player);
	}

	public boolean addTile(@NotNull final Point position, @Nullable final ResourceLocation tileResource, @Nullable final LuaTable storedState) {
		this.tiles.remove(position);
		this.tilesBoundCache.remove(position);
		this.tileStates.remove(position);
		if (tileResource == null) {
			return true;
		} else {
			final Tile tile = GameRegistries.TILE().get(tileResource);
			if (tile == null) {
				World.LOGGER.error("Trying to set tile at {} to unregistered tile {}!", position, tileResource);
				return false;
			} else {
				this.tiles.put(position, tile);
				this.tilesBoundCache.put(position, Rectangle.create(
						position.getX() * World.TILE_SIZE, position.getY() * World.TILE_SIZE,
						World.TILE_SIZE, World.TILE_SIZE
				));
				this.tileStates.put(position, storedState != null ? storedState : new LuaTable());
				if (tile.getUpdateScript() != null) {
					final LuaScript updateScript = World.createTileScript(false, this, tile.getUpdateScript(), position, this.tileStates.get(position));
					if (updateScript != null) {
						this.tileUpdateScripts.put(position, updateScript);
					}
				}
				if (tile.getRenderScript() != null) {
					final LuaScript renderScript = World.createTileScript(true, this, tile.getRenderScript(), position, this.tileStates.get(position));
					if (renderScript != null) {
						this.tileRenderScripts.put(position, renderScript);
					}
				}
				return true;
			}
		}
	}

	public void removeEntity(@NotNull final Entity entity) {
		this.entities.remove(entity);
		if (entity == this.player) {
			this.player = null;
		}
	}

	public void addEntity(@NotNull final Entity entity) {
		this.entities.add(entity);
		if (entity.getResource().toVariant(ResourceLocation.DEFAULT_VARIANT).equals(new ResourceLocation("player"))) {
			this.player = entity;
		}
	}

	@Nullable
	public Point getTileAtPixels(final int x, final int y) {
		return this.tilesBoundCache.entrySet().stream().filter(entry ->
				entry.getValue().contains(x, y)
		).findFirst().map(Map.Entry::getKey).orElse(null);
	}

	public int rayCast(@NotNull final Rectangle origin, @NotNull final Direction direction, final int rayLength) {
		switch (direction) {
			case UP: {
				for (int i = 0; i < rayLength; ++i) {
					for (int x = origin.getX(); x < origin.getMaxX(); x += World.TILE_SIZE) {
						if (this.getTileAtPixels(x, origin.getMaxY() + 1 + i) != null) {
							return i;
						}
					}
					if (this.getTileAtPixels(origin.getMaxX(), origin.getMaxY() + 1 + i) != null) {
						return i;
					}
				}
				return rayLength;
			}
			case DOWN: {
				for (int i = 0; i < rayLength; ++i) {
					for (int x = origin.getX(); x < origin.getMaxX() - 1; x += World.TILE_SIZE) {
						if (this.getTileAtPixels(x, origin.getY() - 1 - i) != null) {
							return i;
						}
					}
					if (this.getTileAtPixels(origin.getMaxX() - 1, origin.getY() - 1 - i) != null) {
						return i;
					}
				}
				return rayLength;
			}
			case LEFT: {
				for (int i = 0; i < rayLength; ++i) {
					for (int y = origin.getY(); y < origin.getMaxY(); y += World.TILE_SIZE) {
						if (this.getTileAtPixels(origin.getX() - 1 - i, y) != null) {
							return i;
						}
					}
					if (this.getTileAtPixels(origin.getX() - 1 - i, origin.getMaxY()) != null) {
						return i;
					}
				}
				return rayLength;
			}
			case RIGHT: {
				for (int i = 0; i < rayLength; ++i) {
					for (int y = origin.getY(); y < origin.getMaxY(); y += World.TILE_SIZE) {
						if (this.getTileAtPixels(origin.getMaxX() + i, y) != null) {
							return i;
						}
					}
					if (this.getTileAtPixels(origin.getMaxX() + i, origin.getMaxY()) != null) {
						return i;
					}
				}
				return rayLength;
			}
		}
		return rayLength;
	}

	@NotNull
	public Rectangle getTileBounds(@NotNull final Point point) {
		return this.tilesBoundCache.get(point);
	}

	@NotNull
	public ResourceLocation getResource() {
		return this.resource;
	}

	@NotNull
	public WorldType getType() {
		return this.type;
	}

	@Nullable
	public Entity getPlayer() {
		return this.player;
	}

	public void queueEvent(@NotNull final Runnable event) {
		this.queue.offer(event);
	}

	@Nullable
	private static LuaScript createTileScript(final boolean canUseRenderUtils, @NotNull final World world, @NotNull final ResourceLocation resource, @NotNull final Point position, @NotNull final LuaTable state) {
		final LuaScript result = canUseRenderUtils ? LuaMachine.loadRenderScript(resource) : LuaMachine.loadScript(resource);
		if (result != null) {
			result.register(new LuaConstantStateContainer(state));
			result.register(new LuaLibWorld(world));
			result.register(new LuaLibTile(world, position));
		}
		return result;
	}

	@SuppressWarnings("ConstantConditions")
	@Nullable
	@ReflectionHelper.Reflectable
	private static World deserializeWorld(@NotNull final SDBTable table) {
		if (!table.hasString("id")) {
			return null;
		} else {
			final ResourceLocation resource = new ResourceLocation(table.getString("id"));
			final WorldType type = GameRegistries.WORLD().get(resource);
			final World result = new World(type.getResource(), type);
			result.deserialize(table);
			return result;
		}
	}
}