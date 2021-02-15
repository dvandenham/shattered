package shattered.core.lua.lib;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import shattered.core.lua.ILuaLib;
import shattered.lib.Color;
import shattered.lib.ResourceLocation;
import shattered.lib.gfx.Tessellator;
import shattered.lib.math.Rectangle;

public final class LuaLibTessellator extends ILuaLib {

	public Tessellator tessellator;

	public LuaLibTessellator() {
		super("tessellator");
	}

	@Override
	protected void set(@NotNull final LuaTable object) {
		object.set("drawQuick", new _DrawQuick());
		object.set("start", new _Start());
		object.set("set", new _Set());
		object.set("setLocation", new _SetLocation());
		object.set("move", new _Move());
		object.set("scale", new _Scale());
		object.set("centerX", new _CenterX());
		object.set("centerY", new _CenterY());
		object.set("center", new _Center());
		object.set("umin", new _UMin());
		object.set("vmin", new _VMin());
		object.set("umax", new _UMax());
		object.set("vmax", new _VMax());
		object.set("uv", new _UV());
		object.set("translate", new _Translate());
		object.set("translateToCenter", new _TranslateToCenter());
		object.set("mirrorX", new _MirrorX());
		object.set("mirrorY", new _MirrorY());
		object.set("mirror", new _Mirror());
		object.set("rotate", new _Rotate());
		object.set("color", new _Color());
		object.set("colorTop", new _ColorTop());
		object.set("colorBottom", new _ColorBottom());
		object.set("colorLeft", new _ColorLeft());
		object.set("colorRight", new _ColorRight());
		object.set("colorTopLeft", new _ColorTopLeft());
		object.set("colorTopRight", new _ColorTopRight());
		object.set("colorBottomLeft", new _ColorBottomLeft());
		object.set("colorBottomRight", new _ColorBottomRight());
		object.set("pushMatrix", new _PushMatrix());
		object.set("popMatrix", new _PopMatrix());
		object.set("enableSmoothing", new _EnableSmoothing());
		object.set("disableSmoothing", new _DisableSmoothing());
		object.set("next", new _Next());
		object.set("draw", new _Draw());
		object.set("isDrawing", new _IsDrawing());
	}

	private class _DrawQuick extends VarArgFunction {

		@Override
		public Varargs invoke(final Varargs args) {
			final int posX = args.checkint(1);
			final int posY = args.checkint(2);
			final int width = args.checkint(3);
			final int height = args.checkint(4);
			final LuaValue arg5 = args.arg(5);
			if (arg5.isstring()) {
				// String means ResourceLocation
				final String resource = arg5.checkjstring();
				LuaLibTessellator.this.tessellator.drawQuick(posX, posY, width, height, new ResourceLocation(resource));
				return LuaValue.NIL;
			} else if (arg5.istable()) {
				// Table means color
				LuaLibTessellator.this.tessellator.drawQuick(posX, posY, width, height, LuaLibTessellator.checkColor(arg5.checktable()));
				return LuaValue.NIL;
			}
			return LuaValue.argerror(5, "Only Strings and Colors are accepted!");
		}
	}

	private class _Start extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaLibTessellator.this.tessellator.start();
			return LuaValue.NIL;
		}
	}

	private class _Set extends VarArgFunction {

		@Override
		public Varargs invoke(final Varargs args) {
			final int posX = args.checkint(1);
			final int posY = args.checkint(2);
			final int width = args.checkint(3);
			final int height = args.checkint(4);
			if (args.arg(5).isstring()) {
				// String means ResourceLocation
				final String Resource = args.checkjstring(5);
				LuaLibTessellator.this.tessellator.set(posX, posY, width, height, new ResourceLocation(Resource));
				return LuaValue.NIL;
			} else if (args.arg(5).istable()) {
				//Table means color
				LuaLibTessellator.this.tessellator.set(posX, posY, width, height, LuaLibTessellator.checkColor(args.checktable(5)));
				return LuaValue.NIL;
			} else {
				return LuaValue.argerror(5, "Only Strings and Colors are accepted!");
			}
		}
	}

	private class _SetLocation extends TwoArgFunction {

		@Override
		public LuaValue call(final LuaValue arg1, final LuaValue arg2) {
			final int posX = arg1.checkint();
			final int posY = arg2.checkint();
			LuaLibTessellator.this.tessellator.setLocation(posX, posY);
			return LuaValue.NIL;
		}
	}

	private class _Move extends TwoArgFunction {

		@Override
		public LuaValue call(final LuaValue arg1, final LuaValue arg2) {
			final int deltaX = arg1.checkint();
			final int deltaY = arg2.checkint();
			LuaLibTessellator.this.tessellator.move(deltaX, deltaY);
			return LuaValue.NIL;
		}
	}

	private class _Scale extends TwoArgFunction {

		@Override
		public LuaValue call(final LuaValue arg1, final LuaValue arg2) {
			final double scaleX = arg1.checkdouble();
			final double scaleY = arg2.checkdouble();
			LuaLibTessellator.this.tessellator.scale((float) scaleX, (float) scaleY);
			return LuaValue.NIL;
		}
	}

	private class _CenterX extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			final int width = arg.checkint();
			LuaLibTessellator.this.tessellator.centerX(width);
			return LuaValue.NIL;
		}
	}

	private class _CenterY extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			final int height = arg.checkint();
			LuaLibTessellator.this.tessellator.centerY(height);
			return LuaValue.NIL;
		}
	}

	private class _Center extends TwoArgFunction {

		@Override
		public LuaValue call(final LuaValue arg1, final LuaValue arg2) {
			final int width = arg1.checkint();
			final int height = arg2.checkint();
			LuaLibTessellator.this.tessellator.center(width, height);
			return LuaValue.NIL;
		}
	}

	private class _UMin extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			final int uMin = arg.checkint();
			LuaLibTessellator.this.tessellator.uMin(uMin);
			return LuaValue.NIL;
		}
	}

	private class _VMin extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			final int vMin = arg.checkint();
			LuaLibTessellator.this.tessellator.vMin(vMin);
			return LuaValue.NIL;
		}
	}

	private class _UMax extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			final int uMax = arg.checkint();
			LuaLibTessellator.this.tessellator.uMax(uMax);
			return LuaValue.NIL;
		}
	}

	private class _VMax extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			final int vMax = arg.checkint();
			LuaLibTessellator.this.tessellator.vMax(vMax);
			return LuaValue.NIL;
		}
	}

	private class _UV extends VarArgFunction {

		@Override
		public Varargs invoke(final Varargs args) {
			final int uMin = args.checkint(1);
			final int uMax = args.checkint(2);
			final int uLength = args.checkint(3);
			final int vLength = args.checkint(4);
			LuaLibTessellator.this.tessellator.uv(Rectangle.create(uMin, uMax, uLength, vLength));
			return LuaValue.NIL;
		}
	}

	private class _Translate extends TwoArgFunction {

		@Override
		public LuaValue call(final LuaValue arg1, final LuaValue arg2) {
			final int deltaX = arg1.checkint();
			final int deltaY = arg2.checkint();
			LuaLibTessellator.this.tessellator.translate(deltaX, deltaY);
			return LuaValue.NIL;
		}
	}

	private class _TranslateToCenter extends TwoArgFunction {

		@Override
		public LuaValue call(final LuaValue arg1, final LuaValue arg2) {
			final int amountX = arg1.checkint();
			final int amountY = arg2.checkint();
			LuaLibTessellator.this.tessellator.translateToCenter(amountX, amountY);
			return LuaValue.NIL;
		}
	}

	private class _MirrorX extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaLibTessellator.this.tessellator.mirrorX();
			return LuaValue.NIL;
		}
	}

	private class _MirrorY extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaLibTessellator.this.tessellator.mirrorY();
			return LuaValue.NIL;
		}
	}

	private class _Mirror extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaLibTessellator.this.tessellator.mirrorX();
			return LuaValue.NIL;
		}
	}

	private class _Rotate extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			final double degrees = arg.checkdouble();
			LuaLibTessellator.this.tessellator.rotate((float) degrees);
			return LuaValue.NIL;
		}
	}

	private class _Color extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			LuaLibTessellator.this.tessellator.color(LuaLibTessellator.checkColor(arg));
			return LuaValue.NIL;
		}
	}

	private class _ColorTop extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			LuaLibTessellator.this.tessellator.colorTop(LuaLibTessellator.checkColor(arg));
			return LuaValue.NIL;
		}
	}

	private class _ColorBottom extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			LuaLibTessellator.this.tessellator.colorBottom(LuaLibTessellator.checkColor(arg));
			return LuaValue.NIL;
		}
	}

	private class _ColorLeft extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			LuaLibTessellator.this.tessellator.colorLeft(LuaLibTessellator.checkColor(arg));
			return LuaValue.NIL;
		}
	}

	private class _ColorRight extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			LuaLibTessellator.this.tessellator.colorRight(LuaLibTessellator.checkColor(arg));
			return LuaValue.NIL;
		}
	}

	private class _ColorTopLeft extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			LuaLibTessellator.this.tessellator.colorTopLeft(LuaLibTessellator.checkColor(arg));
			return LuaValue.NIL;
		}
	}

	private class _ColorTopRight extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			LuaLibTessellator.this.tessellator.colorTopRight(LuaLibTessellator.checkColor(arg));
			return LuaValue.NIL;
		}
	}

	private class _ColorBottomLeft extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			LuaLibTessellator.this.tessellator.colorBottomLeft(LuaLibTessellator.checkColor(arg));
			return LuaValue.NIL;
		}
	}

	private class _ColorBottomRight extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			LuaLibTessellator.this.tessellator.colorBottomRight(LuaLibTessellator.checkColor(arg));
			return LuaValue.NIL;
		}
	}

	private class _PushMatrix extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaLibTessellator.this.tessellator.pushMatrix();
			return LuaValue.NIL;
		}
	}

	private class _PopMatrix extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaLibTessellator.this.tessellator.popMatrix();
			return LuaValue.NIL;
		}
	}

	private class _EnableSmoothing extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaLibTessellator.this.tessellator.enableSmoothing();
			return LuaValue.NIL;
		}
	}

	private class _DisableSmoothing extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaLibTessellator.this.tessellator.disableSmoothing();
			return LuaValue.NIL;
		}
	}

	private class _Next extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaLibTessellator.this.tessellator.next();
			return LuaValue.NIL;
		}
	}

	private class _Draw extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaLibTessellator.this.tessellator.draw();
			return LuaValue.NIL;
		}
	}

	private class _IsDrawing extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			return LuaBoolean.valueOf(LuaLibTessellator.this.tessellator.isDrawing());
		}
	}

	public static Color checkColor(final LuaValue argument) {
		final LuaValue color = argument.checktable();
		final int red = color.get(1).checkint();
		final int green = color.get(2).checkint();
		final int blue = color.get(3).checkint();
		final int alpha = color.get(4).checkint();
		return Color.get(red, green, blue, alpha);
	}
}