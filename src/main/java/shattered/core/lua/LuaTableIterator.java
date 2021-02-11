package shattered.core.lua;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public final class LuaTableIterator extends LuaIterator {

	public LuaTableIterator(@NotNull final LuaTable table) {
		super(table, LuaValue.NIL);
	}

	@Override
	@NotNull
	protected Varargs peekNext(@NotNull final LuaTable table, @NotNull final LuaValue currentKey) {
		return table.next(currentKey);
	}
}