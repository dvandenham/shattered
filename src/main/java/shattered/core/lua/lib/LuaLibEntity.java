package shattered.core.lua.lib;

import shattered.core.lua.ILuaLib;
import shattered.game.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;

public final class LuaLibEntity extends ILuaLib {

	@NotNull
	private final Entity entity;

	public LuaLibEntity(@NotNull final Entity entity) {
		super("entity");
		this.entity = entity;
	}

	@Override
	protected void set(@NotNull final LuaTable object) {
//		object.set("posX", this.entity.getX());
//		object.set("posY", this.position.getY());
	}
}
