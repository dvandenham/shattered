package shattered.core.lua;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public abstract class ILuaConstant extends TwoArgFunction {

	protected final String constantName;

	public ILuaConstant(@NotNull final String constantName) {
		this.constantName = constantName;
	}

	@NotNull
	protected abstract LuaValue create();

	@Override
	@NotNull
	public final LuaValue call(@NotNull final LuaValue modName, @NotNull final LuaValue env) {
		final LuaValue result = this.create();
		env.set(this.constantName, result);
		if (!env.get("package").isnil()) {
			env.get("package").get("loaded").set(this.constantName, result);
		}
		return result;
	}
}
