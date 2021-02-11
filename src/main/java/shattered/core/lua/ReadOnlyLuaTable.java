package shattered.core.lua;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public final class ReadOnlyLuaTable extends LuaTable {

	public ReadOnlyLuaTable(@NotNull final LuaValue table) {
		this.presize(table.length(), 0);
		for (Varargs next = table.next(LuaValue.NIL); !next.arg1().isnil(); next = table.next(next.arg1())) {
			final LuaValue key = next.arg1();
			final LuaValue value = next.arg(2);
			super.rawset(key, value.istable() ? new ReadOnlyLuaTable(value) : value);
		}
	}

	@Override
	public LuaValue setmetatable(final LuaValue metaTable) {
		return LuaValue.error("table is read-only");
	}

	@Override
	public void set(final int key, final LuaValue value) {
		LuaValue.error("table is read-only");
	}

	@Override
	public void rawset(final int key, final LuaValue value) {
		LuaValue.error("table is read-only");
	}

	@Override
	public void rawset(final LuaValue key, final LuaValue value) {
		LuaValue.error("table is read-only");
	}

	@Override
	public LuaValue remove(final int position) {
		return LuaValue.error("table is read-only");
	}
}