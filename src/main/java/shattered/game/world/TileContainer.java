package shattered.game.world;

import shattered.core.LuaMachine;
import shattered.core.LuaScript;
import shattered.core.lua.constant.LuaConstantStateContainer;
import shattered.core.lua.lib.LuaLibTile;
import shattered.core.lua.lib.LuaLibWorld;
import shattered.game.GameRegistries;
import shattered.game.tile.Tile;
import shattered.lib.ResourceLocation;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;

final class TileContainer {

	private final Object2ObjectArrayMap<Point, Tile> tiles = new Object2ObjectArrayMap<>();
	private final Object2ObjectArrayMap<Point, Rectangle> boundCache = new Object2ObjectArrayMap<>();
	private final Object2ObjectArrayMap<Point, LuaTable> states = new Object2ObjectArrayMap<>();
	private final Object2ObjectArrayMap<Point, LuaScript> updateScripts = new Object2ObjectArrayMap<>();
	private final Object2ObjectArrayMap<Point, LuaScript> renderScripts = new Object2ObjectArrayMap<>();

	@NotNull
	private final World world;

	public TileContainer(@NotNull final World world) {
		this.world = world;
	}

	public void tick() {

	}

	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		this.tiles.forEach((position, tile) -> tessellator.drawQuick(TileContainer.flipBounds(this.boundCache.get(position)), tile.getTexture()));
		this.renderScripts.values().forEach(LuaScript::executeScript);
	}

	public boolean addTile(@NotNull final Point position, @Nullable final ResourceLocation tileResource, @Nullable final LuaTable storedState) {
		this.tiles.remove(position);
		this.boundCache.remove(position);
		this.states.remove(position);
		if (tileResource == null) {
			return true;
		} else {
			final Tile tile = GameRegistries.TILE().get(tileResource);
			if (tile == null) {
				World.LOGGER.error("Trying to set tile at {} to unregistered tile {}!", position, tileResource);
				return false;
			} else {
				this.tiles.put(position, tile);
				this.boundCache.put(position, Rectangle.create(
						position.getX() * World.TILE_SIZE, position.getY() * World.TILE_SIZE,
						World.TILE_SIZE, World.TILE_SIZE
				));
				this.states.put(position, storedState != null ? storedState : new LuaTable());
				if (tile.getUpdateScript() != null) {
					final LuaScript updateScript = this.createScript(false, tile.getUpdateScript(), position, this.states.get(position));
					if (updateScript != null) {
						this.updateScripts.put(position, updateScript);
					}
				}
				if (tile.getRenderScript() != null) {
					final LuaScript renderScript = this.createScript(true, tile.getRenderScript(), position, this.states.get(position));
					if (renderScript != null) {
						this.renderScripts.put(position, renderScript);
					}
				}
				return true;
			}
		}
	}

	@Nullable
	private LuaScript createScript(final boolean canUseRenderUtils, @NotNull final ResourceLocation resource, @NotNull final Point position, @NotNull final LuaTable state) {
		final LuaScript result = canUseRenderUtils ? LuaMachine.loadRenderScript(resource) : LuaMachine.loadScript(resource);
		if (result != null) {
			result.register(new LuaConstantStateContainer(state));
			result.register(new LuaLibWorld(this.world));
			result.register(new LuaLibTile(this.world, position));
		}
		return result;
	}

	@NotNull
	private static Rectangle flipBounds(@NotNull final Rectangle bounds) {
		return bounds.toImmutable().setY(Display.getHeight() - bounds.getHeight() - bounds.getY());
	}
}