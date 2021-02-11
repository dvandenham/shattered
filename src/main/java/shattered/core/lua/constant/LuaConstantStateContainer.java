package shattered.core.lua.constant;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import shattered.core.lua.ILuaConstant;

public final class LuaConstantStateContainer extends ILuaConstant {

	private final LuaTable container;

	public LuaConstantStateContainer(@NotNull final LuaTable container) {
		super("state");
		this.container = container;
	}

	@Override
	@NotNull
	protected LuaValue create() {
		return this.container;
	}
}