package shattered.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.core.lua.LuaStringLib;
import shattered.core.lua.ReadOnlyLuaTable;

@EventBusSubscriber("SYSTEM")
public final class LuaMachine {

	static final Logger LOGGER = LogManager.getLogger("LuaMachine");
	public static final LuaMachine INSTANCE = new LuaMachine();
	private Globals globals;

	private LuaMachine() {
	}

	@MessageListener("init_lua_machine")
	private static void onSystemMessage(final MessageEvent ignored) {
		LuaMachine.LOGGER.debug("Starting LuaMachine");
		//Lua core
		LuaMachine.LOGGER.debug("Installing globals");
		LuaMachine.INSTANCE.globals = new Globals();
		LuaMachine.INSTANCE.globals.load(new JseBaseLib());
		LuaMachine.INSTANCE.globals.load(new PackageLib());
		LuaMachine.INSTANCE.globals.load(new LuaStringLib());
		LuaMachine.INSTANCE.globals.load(new JseMathLib());

		//Compiler
		LuaMachine.LOGGER.debug("Configuring compiler");
		LoadState.install(LuaMachine.INSTANCE.globals);
		LuaC.install(LuaMachine.INSTANCE.globals);
		LuaString.s_metatable = new ReadOnlyLuaTable(LuaString.s_metatable);
	}

	@NotNull
	public LuaValue parseScript(@NotNull final String script, @NotNull final Globals sandbox) {
		return this.globals.load(script, "main", sandbox);
	}
}