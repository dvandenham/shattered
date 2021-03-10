package shattered.lib;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import shattered.Shattered;
import shattered.core.sdb.SDBHelper;
import shattered.core.sdb.SDBTable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class KeyManager {

	private final ConcurrentHashMap<String, KeyBind> BINDINGS = new ConcurrentHashMap<>();
	private final ObjectArrayList<IKeyListener> LISTENERS = new ObjectArrayList<>();
	private final Logger LOGGER = LogManager.getLogger("KeyManager");
	private final File file;
	private SDBTable store;

	public KeyManager(@NotNull final Workspace workspace) {
		this.file = workspace.getDataFile("keybinds.db");
		try {
			this.load();
		} catch (final IOException e) {
			Shattered.crash("Could not initialize KeyManager", e);
		}
	}

	private void load() throws IOException {
		if (!this.file.exists()) {
			SDBHelper.serialize(new SDBTable(), this.file);
		}
		this.store = SDBHelper.deserialize(this.file);
	}

	void poll() {
		if (Input.isKeyManagerBlocked()) {
			return;
		}
		this.BINDINGS.values().forEach(keybind -> {
			if (keybind.timeOut > 0) {
				--keybind.timeOut;
				return;
			}
			if (!Input.isKeyDown(keybind.getKeyCode())) {
				keybind.isPressed = false;
				keybind.timeOut = 0;
				return;
			}
			if (keybind.isPressed && keybind.isToggle) {
				return;
			}
			if (keybind.hasShift() != Input.isKeyboardShiftDown()) {
				return;
			}
			if (keybind.hasCtrl() != Input.isKeyboardControlDown()) {
				return;
			}
			if (keybind.hasAlt() != Input.isKeyboardAltDown()) {
				return;
			}
			this.LOGGER.debug("Key \"{}\" has been pressed", Localizer.localize(keybind.getDisplayName()));
			keybind.isPressed = true;
			keybind.timeOut = keybind.defaultTimeOut;
			this.LISTENERS.forEach(listener -> listener.onKeybindChanged(keybind));
		});
	}

	public void registerListener(@NotNull final IKeyListener listener) {
		if (!this.LISTENERS.contains(listener)) {
			this.LISTENERS.add(listener);
		}
	}

	public void unregisterListener(@NotNull final IKeyListener listener) {
		this.LISTENERS.remove(listener);
	}

	@NotNull
	public KeyBind addToggle(@NotNull final String identifier,
	                         @NotNull final String displayName,
	                         final int defaultKeyCode) {
		final KeyBind result = new KeyBind(this, identifier, displayName, defaultKeyCode);
		result.load(this.store);
		this.BINDINGS.put(identifier, result);
		return result;
	}

	@NotNull
	public KeyBind addToggle(@NotNull final String identifier,
	                         @NotNull final String displayName,
	                         final int defaultKeyCode,
	                         final boolean defaultShift, final boolean defaultCtrl, final boolean defaultAlt) {
		final KeyBind result = new KeyBind(this, identifier, displayName, defaultKeyCode, defaultShift, defaultCtrl, defaultAlt);
		result.load(this.store);
		this.BINDINGS.put(identifier, result);
		return result;
	}

	@NotNull
	public KeyBind addRepeating(@NotNull final String identifier,
	                            @NotNull final String displayName,
	                            final int defaultKeyCode, final int defaultTimeOut,
	                            final boolean defaultShift, final boolean defaultCtrl, final boolean defaultAlt) {
		final KeyBind result = new KeyBind(this, identifier, displayName, defaultKeyCode, defaultTimeOut, defaultShift, defaultCtrl, defaultAlt);
		result.load(this.store);
		this.BINDINGS.put(identifier, result);
		return result;
	}

	@NotNull
	public KeyBind addRepeating(@NotNull final String identifier,
	                            @NotNull final String displayName,
	                            final int defaultKeyCode, final int defaultTimeOut) {
		return this.addRepeating(identifier, displayName, defaultKeyCode, defaultTimeOut, false, false, false);
	}

	public void saveToDisk() {
		this.BINDINGS.values().forEach(binding -> binding.save(this.store));
		try {
			SDBHelper.serialize(this.store, this.file);
		} catch (final IOException e) {
			this.LOGGER.error("Could not save data to disk!", e);
		}
	}
}