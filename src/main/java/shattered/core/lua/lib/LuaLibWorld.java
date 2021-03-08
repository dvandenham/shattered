package shattered.core.lua.lib;

import shattered.core.lua.ILuaLib;
import shattered.game.world.World;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;

public class LuaLibWorld extends ILuaLib {
	
	@NotNull
	private final World world;

	public LuaLibWorld(@NotNull final World world) {
		super("world");
		this.world = world;
	}

	@Override
	protected void set(@NotNull final LuaTable object) {
	}
}
