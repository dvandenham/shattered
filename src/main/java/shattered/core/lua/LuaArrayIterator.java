package shattered.core.lua;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public final class LuaArrayIterator extends LuaIterator {

	public LuaArrayIterator(@NotNull final LuaTable array) {
		super(array, LuaValue.ZERO);
	}

	@Override
	@NotNull
	protected Varargs peekNext(@NotNull final LuaTable array, @NotNull final LuaValue currentKey) {
		return array.inext(currentKey);
	}
}