package shattered.core;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import shattered.core.lua.LuaStringLib;
import shattered.core.lua.lib.LuaGlobalLibColor;
import shattered.core.lua.lib.LuaGlobalLibStringData;
import shattered.core.lua.lib.LuaLibFontRenderer;
import shattered.core.lua.lib.LuaLibTessellator;
import shattered.StaticAssets;
import shattered.lib.ReflectionHelper;
import shattered.lib.asset.FontGroup;
import shattered.lib.asset.LuaAsset;
import shattered.lib.gfx.FontRendererImpl;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gfx.TessellatorImpl;

public final class LuaScript {

	private static final String SCRIPT_DESTROY_MESSAGE = "Destroyed a script using more system resources than allowed";
	private final LuaAsset asset;
	private final Globals sandbox;
	private final LuaValue setHook;
	private final TessellatorImpl scriptTessellator;
	private final FontRendererImpl scriptFontRenderer;

	LuaScript(@NotNull final LuaAsset asset, final boolean canUseRenderUtils) {
		this.asset = asset;
		this.sandbox = LuaScript.createSandbox();
		this.setHook = this.createSetHook();
		if (canUseRenderUtils) {
			final TessellatorImpl tessellator = ReflectionHelper.instantiate(TessellatorImpl.class);
			assert tessellator != null;
			tessellator.setShader(StaticAssets.SHADER);
			StaticAssets.SHADER.bind();
			this.scriptTessellator = tessellator;
			this.scriptFontRenderer = ReflectionHelper.instantiate(FontRendererImpl.class, Tessellator.class, this.scriptTessellator, FontGroup.class, StaticAssets.FONT_DEFAULT);
		} else {
			this.scriptTessellator = null;
			this.scriptFontRenderer = null;
		}
	}

	public void register(@NotNull final TwoArgFunction object) {
		this.sandbox.load(object);
		if (object instanceof LuaLibTessellator) {
			((LuaLibTessellator) object).tessellator = this.scriptTessellator;
		} else if (object instanceof LuaLibFontRenderer) {
			((LuaLibFontRenderer) object).fontRenderer = this.scriptFontRenderer;
		}
	}

	public void executeScript() {
		if (this.scriptTessellator != null) {
			this.scriptTessellator.startCaching();
		}
		final LuaValue chunk = LuaMachine.INSTANCE.parseScript(this.asset.getScript(), this.sandbox);
		final LuaThread scriptThread = new LuaThread(this.sandbox, chunk);
		final LuaValue hookFunction = new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				throw new Error(LuaScript.SCRIPT_DESTROY_MESSAGE);
			}
		};
		final int maxInstructions = 100;
		this.setHook.invoke(LuaValue.varargsOf(new LuaValue[]{scriptThread, hookFunction, LuaValue.EMPTYSTRING, LuaValue.valueOf(maxInstructions)}));
		final Varargs result = scriptThread.resume(LuaValue.NIL);
		if (!result.checkboolean(1)) {
			final String message = result.checkjstring(2);
			if (message.equals(LuaScript.SCRIPT_DESTROY_MESSAGE)) {
				LuaMachine.LOGGER.fatal(message);
				return;
			}
			throw new RuntimeScriptException(this.asset.getResource(), message);
		}
		if (this.scriptTessellator != null) {
			this.scriptTessellator.drawCached();
		}
	}

	@NotNull
	private static Globals createSandbox() {
		final Globals result = new Globals();
		//Build-in libs
		result.load(new JseBaseLib());
		result.load(new PackageLib());
		result.load(new Bit32Lib());
		result.load(new TableLib());
		result.load(new LuaStringLib());
		result.load(new JseMathLib());
		//Custom libs
		result.load(new LuaGlobalLibColor());
		result.load(new LuaGlobalLibStringData());
		return result;
	}

	@NotNull
	private LuaValue createSetHook() {
		// The debug library must be loaded for hook functions to work, which
		// allow us to limit scripts to run a certain number of instructions at a time.
		// However we don't wish to expose the library in the user globals,
		// so it is immediately removed from the user globals once created.
		this.sandbox.load(new DebugLib());
		final LuaValue setHook = this.sandbox.get("debug").get("sethook");
		this.sandbox.set("debug", LuaValue.NIL);
		return setHook;
	}
}