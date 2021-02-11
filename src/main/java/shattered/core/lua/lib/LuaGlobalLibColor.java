package shattered.core.lua.lib;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import shattered.core.lua.ILuaLib;
import shattered.core.lua.ReadOnlyLuaTable;

public final class LuaGlobalLibColor extends ILuaLib {

	private static final LuaValue CREATE = new _Create();
	public static final LuaTable TRANSPARENT = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.ZERO, LuaNumber.ZERO, LuaNumber.ZERO, LuaNumber.ZERO}));
	public static final LuaTable WHITE = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(255), LuaNumber.valueOf(255), LuaNumber.valueOf(255)}));
	public static final LuaTable LIGHT_GRAY = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(192), LuaNumber.valueOf(192), LuaNumber.valueOf(192)}));
	public static final LuaTable GRAY = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(128), LuaNumber.valueOf(128), LuaNumber.valueOf(128)}));
	public static final LuaTable DARK_GRAY = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(64), LuaNumber.valueOf(64), LuaNumber.valueOf(64)}));
	public static final LuaTable BLACK = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(0), LuaNumber.valueOf(0), LuaNumber.valueOf(0)}));
	public static final LuaTable RED = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(255), LuaNumber.valueOf(0), LuaNumber.valueOf(0)}));
	public static final LuaTable PINK = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(255), LuaNumber.valueOf(175), LuaNumber.valueOf(175)}));
	public static final LuaTable ORANGE = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(255), LuaNumber.valueOf(200), LuaNumber.valueOf(0)}));
	public static final LuaTable YELLOW = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(255), LuaNumber.valueOf(255), LuaNumber.valueOf(0)}));
	public static final LuaTable GREEN = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(0), LuaNumber.valueOf(255), LuaNumber.valueOf(0)}));
	public static final LuaTable MAGENTA = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(255), LuaNumber.valueOf(0), LuaNumber.valueOf(255)}));
	public static final LuaTable CYAN = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(0), LuaNumber.valueOf(255), LuaNumber.valueOf(255)}));
	public static final LuaTable BLUE = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(0), LuaNumber.valueOf(0), LuaNumber.valueOf(255)}));
	public static final LuaTable XEROS = (LuaTable) LuaGlobalLibColor.CREATE.invoke(LuaValue.varargsOf(new LuaValue[]{LuaNumber.valueOf(50), LuaNumber.valueOf(204), LuaNumber.valueOf(220)}));

	public LuaGlobalLibColor() {
		super("colors");
	}

	@Override
	protected void set(@NotNull final LuaTable object) {
		object.set("create", LuaGlobalLibColor.CREATE);
		object.set("transparent", LuaGlobalLibColor.TRANSPARENT);
		object.set("white", LuaGlobalLibColor.WHITE);
		object.set("light_gray", LuaGlobalLibColor.LIGHT_GRAY);
		object.set("light_grey", LuaGlobalLibColor.LIGHT_GRAY);
		object.set("gray", LuaGlobalLibColor.GRAY);
		object.set("grey", LuaGlobalLibColor.GRAY);
		object.set("dark_gray", LuaGlobalLibColor.DARK_GRAY);
		object.set("dark_grey", LuaGlobalLibColor.DARK_GRAY);
		object.set("black", LuaGlobalLibColor.BLACK);
		object.set("red", LuaGlobalLibColor.RED);
		object.set("pink", LuaGlobalLibColor.PINK);
		object.set("orange", LuaGlobalLibColor.ORANGE);
		object.set("yellow", LuaGlobalLibColor.YELLOW);
		object.set("green", LuaGlobalLibColor.GREEN);
		object.set("magenta", LuaGlobalLibColor.MAGENTA);
		object.set("cyan", LuaGlobalLibColor.CYAN);
		object.set("blue", LuaGlobalLibColor.BLUE);
		object.set("xeros", LuaGlobalLibColor.XEROS);
	}

	private static class _Create extends VarArgFunction {

		@Override
		public Varargs invoke(final Varargs args) {
			final int red, green, blue, alpha;
			if (args.narg() == 1) {
				red = args.checkint(1) >> 16 & 255;
				green = args.checkint(1) >> 8 & 255;
				blue = args.checkint(1) & 255;
				alpha = args.checkint(1) >> 24 & 255;
			} else if (args.narg() == 3 || args.narg() == 4) {
				red = args.checkint(1);
				green = args.checkint(2);
				blue = args.checkint(3);
				alpha = args.narg() == 3 ? 255 : args.checkint(4);
			} else {
				return this.argerror("create only accepts one RGBA-integer, Separate R,G,B-integers or Separate R,G,B,A-integers!");
			}
			return new ReadOnlyLuaTable(LuaTable.listOf(new LuaValue[]{
					LuaInteger.valueOf(red),
					LuaInteger.valueOf(green),
					LuaInteger.valueOf(blue),
					LuaInteger.valueOf(alpha)
			}));
		}
	}
}