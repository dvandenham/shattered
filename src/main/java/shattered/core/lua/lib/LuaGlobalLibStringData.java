package shattered.core.lua.lib;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import shattered.core.lua.ILuaLib;

public final class LuaGlobalLibStringData extends ILuaLib {

	public LuaGlobalLibStringData() {
		super("StringData");
	}

	@Override
	protected void set(@NotNull final LuaTable object) {
		object.set("create", new _Create());
	}

	private static class _Create extends TwoArgFunction {

		@Override
		public LuaValue call(final LuaValue arg1, final LuaValue arg2) {
			final LuaTable result = LuaGlobalLibStringData.createTable();
			result.get("__Data").checktable().set("text", arg1.checkstring());
			if (!arg2.isnil()) {
				result.get("__Data").checktable().set("color", arg2.checktable());
			} else {
				result.get("__Data").checktable().set("color", LuaGlobalLibColor.WHITE);
			}
			return result;
		}
	}

	private static LuaTable createTable() {
		final LuaTable result = new LuaTable();
		result.set("__Data", new LuaTable());
		result.set("centerX", new _CenterX(result));
		result.set("centerY", new _CenterY(result));
		result.set("center", new _Center(result));
		result.set("wrap", new _Wrap(result));
		result.set("localize", new _Localize(result));
		return result;
	}

	private static class _CenterX extends ReturnMeOne {

		private _CenterX(final LuaTable container) {
			super(container);
		}

		@Override
		protected void doInvoke(@NotNull final LuaValue arg) {
			this.getData().set("centerX", arg.checkint());
		}
	}

	private static class _CenterY extends ReturnMeOne {

		private _CenterY(final LuaTable container) {
			super(container);
		}

		@Override
		protected void doInvoke(@NotNull final LuaValue arg) {
			this.getData().set("centerY", arg.checkint());
		}
	}

	private static class _Center extends ReturnMeTwo {

		private _Center(final LuaTable container) {
			super(container);
		}

		@Override
		protected void doInvoke(@NotNull final LuaValue arg1, @NotNull final LuaValue arg2) {
			this.getData().set("centerX", arg1.checkint());
			this.getData().set("centerY", arg2.checkint());
		}
	}

	private static class _Wrap extends ReturnMeTwo {

		private _Wrap(final LuaTable container) {
			super(container);
		}

		@Override
		protected void doInvoke(@NotNull final LuaValue arg1, @NotNull final LuaValue arg2) {
			final boolean stopAtRow = arg2.isboolean() && arg2.checkboolean();
			this.getData().set("wrap", arg1.checkint());
			this.getData().set("wrapStop", LuaBoolean.valueOf(stopAtRow));
		}
	}

	private static class _Localize extends ReturnMeOne {

		private _Localize(final LuaTable container) {
			super(container);
		}

		@Override
		protected void doInvoke(@NotNull final LuaValue arg) {
			this.getData().set("localize", LuaBoolean.valueOf(arg.isboolean() && arg.checkboolean()));
		}
	}

	private static abstract class ReturnMeOne extends OneArgFunction {

		protected final LuaTable container;

		private ReturnMeOne(final LuaTable container) {
			this.container = container;
		}

		protected abstract void doInvoke(@NotNull LuaValue arg);

		protected final LuaTable getData() {
			return this.container.get("__Data").checktable();
		}

		@Override
		public final LuaValue call(final LuaValue arg) {
			this.doInvoke(arg);
			return this.container;
		}
	}

	private static abstract class ReturnMeTwo extends TwoArgFunction {

		protected final LuaTable container;

		private ReturnMeTwo(final LuaTable container) {
			this.container = container;
		}

		protected abstract void doInvoke(@NotNull LuaValue arg1, @NotNull LuaValue arg2);

		protected final LuaTable getData() {
			return this.container.get("__Data").checktable();
		}

		@Override
		public final LuaValue call(final LuaValue arg1, final LuaValue arg2) {
			this.doInvoke(arg1, arg2);
			return this.container;
		}
	}
}