package shattered.core.lua;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public abstract class ILuaLib extends TwoArgFunction {

	protected final String libName;

	public ILuaLib(@NotNull final String libName) {
		this.libName = libName;
	}

	protected abstract void set(@NotNull LuaTable object);

	@Override
	@NotNull
	public final LuaValue call(@NotNull final LuaValue modName, @NotNull final LuaValue env) {
		final LuaTable object = new LuaTable();
		this.set(object);
		env.set(this.libName, object);
		if (!env.get("package").isnil()) {
			env.get("package").get("loaded").set(this.libName, object);
		}
		return object;
	}
}
