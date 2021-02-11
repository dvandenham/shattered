package shattered.core.lua;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

abstract class LuaIterator implements Iterator<Varargs> {

	private final LuaTable table;
	private LuaValue currentKey;

	public LuaIterator(@NotNull final LuaTable table, @NotNull final LuaValue initialKey) {
		this.table = table;
		this.currentKey = initialKey;
	}

	@Override
	public boolean hasNext() {
		final Varargs entry = this.peekNext(this.table, this.currentKey);
		return !entry.arg1().isnil();
	}

	@Override
	@NotNull
	public Varargs next() {
		final Varargs entry = this.peekNext(this.table, this.currentKey);
		this.currentKey = entry.arg1();
		if (this.currentKey.isnil()) {
			throw new NoSuchElementException();
		}
		return entry;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove");
	}

	@NotNull
	protected abstract Varargs peekNext(@NotNull LuaTable table, @NotNull LuaValue currentKey);
}