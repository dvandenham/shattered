package shattered.core.lua.lib;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import shattered.core.lua.ILuaLib;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.StringData;

public class LuaLibFontRenderer extends ILuaLib {

	public FontRenderer fontRenderer;

	public LuaLibFontRenderer() {
		super("fontRenderer");
	}

	@Override
	protected void set(@NotNull final LuaTable object) {
		object.set("writeQuick", new _WriteQuick());
		object.set("writeQuickCentered", new _WriteQuickCentered());
		object.set("start", new _Start());
		object.set("add", new _Add());
		object.set("addCentered", new _AddCentered());
		object.set("write", new _Write());
		object.set("isWriting", new _IsWriting());
		object.set("getWidth", new _GetWidth());
		object.set("getHeight", new _GetHeight());
		object.set("getTextRows", new _GetTextRows());
		object.set("getWrappedText", new _GetWrappedText());
	}

	private class _WriteQuick extends VarArgFunction {

		@Override
		public Varargs invoke(final Varargs arg) {
			final int posX = arg.checkint(1);
			final int posY = arg.checkint(2);
			final LuaValue arg3 = arg.arg(3);
			if (arg3.istable()) {
				//StringData
				LuaLibFontRenderer.this.fontRenderer.writeQuick(posX, posY, LuaLibFontRenderer.parseTable(arg3.checktable()));
			} else if (!arg3.isstring()) {
				if (!arg.arg(4).istable()) {
					//No color
					LuaLibFontRenderer.this.fontRenderer.writeQuick(posX, posY, arg3.checkjstring());
				} else {
					LuaLibFontRenderer.this.fontRenderer.writeQuick(posX, posY, arg3.checkjstring(), LuaLibTessellator.checkColor(arg.arg(4).checktable()));
				}
			}
			return LuaValue.NIL;
		}
	}

	private class _WriteQuickCentered extends VarArgFunction {

		@Override
		public Varargs invoke(final Varargs arg) {
			final int posX = arg.checkint(1);
			final int posY = arg.checkint(2);
			final int width = arg.checkint(3);
			final int height = arg.checkint(4);
			final LuaValue arg5 = arg.arg(5);
			if (arg5.istable()) {
				//StringData
				LuaLibFontRenderer.this.fontRenderer.writeQuickCentered(posX, posY, width, height, LuaLibFontRenderer.parseTable(arg5.checktable()));
			} else if (!arg5.isstring()) {
				if (!arg.arg(6).istable()) {
					//No color
					LuaLibFontRenderer.this.fontRenderer.writeQuickCentered(posX, posY, width, height, arg5.checkjstring());
				} else {
					LuaLibFontRenderer.this.fontRenderer.writeQuickCentered(posX, posY, width, height, arg5.checkjstring(), LuaLibTessellator.checkColor(arg.arg(6).checktable()));
				}
			}
			return LuaValue.NIL;
		}
	}

	private class _Start extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaLibFontRenderer.this.fontRenderer.start();
			return LuaValue.NIL;
		}
	}

	private class _Add extends VarArgFunction {

		@Override
		public Varargs invoke(final Varargs args) {
			final int posX = args.checkint(1);
			final int posY = args.checkint(2);
			final LuaValue arg3 = args.arg(3);
			if (arg3.istable()) {
				//StringData
				LuaLibFontRenderer.this.fontRenderer.add(posX, posY, LuaLibFontRenderer.parseTable(arg3.checktable()));
			} else if (!arg3.isstring()) {
				if (!args.arg(4).istable()) {
					//No color
					LuaLibFontRenderer.this.fontRenderer.add(posX, posY, arg3.checkjstring());
				} else {
					LuaLibFontRenderer.this.fontRenderer.add(posX, posY, arg3.checkjstring(), LuaLibTessellator.checkColor(args.arg(4).checktable()));
				}
			}
			return LuaValue.NIL;
		}
	}

	private class _AddCentered extends VarArgFunction {

		@Override
		public Varargs invoke(final Varargs args) {
			final int posX = args.checkint(1);
			final int posY = args.checkint(2);
			final int width = args.checkint(3);
			final int height = args.checkint(4);
			final LuaValue arg5 = args.arg(5);
			if (arg5.istable()) {
				//StringData
				LuaLibFontRenderer.this.fontRenderer.addCentered(posX, posY, width, height, LuaLibFontRenderer.parseTable(arg5.checktable()));
			} else if (!arg5.isstring()) {
				if (!args.arg(6).istable()) {
					//No color
					LuaLibFontRenderer.this.fontRenderer.addCentered(posX, posY, width, height, arg5.checkjstring());
				} else {
					LuaLibFontRenderer.this.fontRenderer.addCentered(posX, posY, width, height, arg5.checkjstring(), LuaLibTessellator.checkColor(args.arg(6).checktable()));
				}
			}
			return LuaValue.NIL;
		}
	}

	private class _Write extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaLibFontRenderer.this.fontRenderer.write();
			return LuaValue.NIL;
		}
	}

	private class _IsWriting extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			return LuaBoolean.valueOf(LuaLibFontRenderer.this.fontRenderer.isWriting());
		}
	}

	private class _GetWidth extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			if (arg.istable()) {
				return LuaNumber.valueOf(LuaLibFontRenderer.this.fontRenderer.getWidth(LuaLibFontRenderer.parseTable(arg.checktable())));
			} else if (arg.isstring()) {
				return LuaNumber.valueOf(LuaLibFontRenderer.this.fontRenderer.getWidth(arg.checkjstring()));
			}
			return this.argerror("getWidth only accepts StringData or String!");
		}
	}

	private class _GetHeight extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			if (arg.istable()) {
				return LuaNumber.valueOf(LuaLibFontRenderer.this.fontRenderer.getHeight(LuaLibFontRenderer.parseTable(arg.checktable())));
			} else if (arg.isnil()) {
				return LuaNumber.valueOf(LuaLibFontRenderer.this.fontRenderer.getHeight());
			}
			return this.argerror("getHeight only accepts StringData or nothing!");
		}
	}

	private class _GetTextRows extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			if (arg.istable()) {
				return LuaNumber.valueOf(LuaLibFontRenderer.this.fontRenderer.getTextRows(LuaLibFontRenderer.parseTable(arg.checktable())));
			}
			return this.argerror("getTextRows only accepts StringData!");
		}
	}

	private class _GetWrappedText extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			if (arg.istable()) {
				final String result = LuaLibFontRenderer.this.fontRenderer.getWrappedText(LuaLibFontRenderer.parseTable(arg.checktable()));
				return result != null ? LuaString.valueOf(result) : LuaValue.NIL;
			}
			return this.argerror("getWrappedText only accepts StringData!");
		}
	}

	@NotNull
	private static StringData parseTable(@NotNull final LuaTable table) {
		final LuaTable data = table.get("__Data").checktable();
		final StringData result = new StringData(data.get("text").checkjstring(), LuaLibTessellator.checkColor(data.get("color").checktable()));
		if (data.get("centerX").isint()) {
			result.centerX(data.get("centerX").checkint());
		}
		if (data.get("centerY").isint()) {
			result.centerY(data.get("centerY").checkint());
		}
		if (data.get("wrap").isint()) {
			result.wrap(data.get("wrap").checkint(), data.get("wrapStop").isboolean() && data.get("wrapStop").checkboolean());
		}
		if (data.get("localize").isboolean()) {
			result.localize(data.get("localize").checkboolean());
		}
		return result;
	}
}
