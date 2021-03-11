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

	private final ConcurrentHashMap<String, KeyBind> bindings = new ConcurrentHashMap<>();
	private final ObjectArrayList<IKeyListener> listeners = new ObjectArrayList<>();
	private final Logger logger = LogManager.getLogger("KeyManager");
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
		if (!Input.isEventBusModeEnabled()) {
			this.bindings.values().forEach(binding -> {
				if (binding.timeOut > 0) {
					--binding.timeOut;
					return;
				}
				if (!Input.isKeyDown(binding.getKeyCode())) {
					binding.isPressed = false;
					binding.timeOut = 0;
					return;
				}
				if (binding.isPressed && binding.isToggle) {
					return;
				}
				if (binding.hasShift() != Input.isKeyboardShiftDown()) {
					return;
				}
				if (binding.hasCtrl() != Input.isKeyboardControlDown()) {
					return;
				}
				if (binding.hasAlt() != Input.isKeyboardAltDown()) {
					return;
				}
				this.logger.debug("Key '{}' has been pressed", Localizer.localize(binding.getDisplayName()));
				binding.isPressed = true;
				binding.timeOut = binding.defaultTimeOut;
				this.listeners.forEach(listener -> listener.onKeybindChanged(binding));
			});
		}
	}

	public void registerListener(@NotNull final IKeyListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public void unregisterListener(@NotNull final IKeyListener listener) {
		this.listeners.remove(listener);
	}

	@NotNull
	public KeyBind addToggle(@NotNull final String identifier,
	                         @NotNull final String displayName,
	                         final int defaultKeyCode) {
		final KeyBind result = new KeyBind(this, identifier, displayName, defaultKeyCode);
		result.load(this.store);
		this.bindings.put(identifier, result);
		return result;
	}

	@NotNull
	public KeyBind addToggle(@NotNull final String identifier,
	                         @NotNull final String displayName,
	                         final int defaultKeyCode,
	                         final boolean defaultShift, final boolean defaultCtrl, final boolean defaultAlt) {
		final KeyBind result = new KeyBind(this, identifier, displayName, defaultKeyCode, defaultShift, defaultCtrl, defaultAlt);
		result.load(this.store);
		this.bindings.put(identifier, result);
		return result;
	}

	@NotNull
	public KeyBind addRepeating(@NotNull final String identifier,
	                            @NotNull final String displayName,
	                            final int defaultKeyCode, final int defaultTimeOut,
	                            final boolean defaultShift, final boolean defaultCtrl, final boolean defaultAlt) {
		final KeyBind result = new KeyBind(this, identifier, displayName, defaultKeyCode, defaultTimeOut, defaultShift, defaultCtrl, defaultAlt);
		result.load(this.store);
		this.bindings.put(identifier, result);
		return result;
	}

	@NotNull
	public KeyBind addRepeating(@NotNull final String identifier,
	                            @NotNull final String displayName,
	                            final int defaultKeyCode, final int defaultTimeOut) {
		return this.addRepeating(identifier, displayName, defaultKeyCode, defaultTimeOut, false, false, false);
	}

	public void saveToDisk() {
		this.bindings.values().forEach(binding -> binding.save(this.store));
		try {
			SDBHelper.serialize(this.store, this.file);
		} catch (final IOException e) {
			this.logger.error("Could not save data to disk!", e);
		}
	}
}