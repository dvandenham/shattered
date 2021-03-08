package shattered.core.lua.lib;

import shattered.core.lua.ILuaLib;
import shattered.game.world.World;
import shattered.lib.math.Point;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;

public class LuaLibTile extends ILuaLib {

	@NotNull
	private final World world;
	@NotNull
	private final Point position;

	public LuaLibTile(@NotNull final World world, @NotNull final Point position) {
		super("tile");
		this.world = world;
		this.position = position;
	}

	@Override
	protected void set(@NotNull final LuaTable object) {
		object.set("posX", this.position.getX());
		object.set("posY", this.position.getY());
	}
}
