package shattered.lib;

import shattered.core.sdb.SDBTable;
import org.jetbrains.annotations.NotNull;

public final class KeyBind {

	public static final int TOGGLE_TIMEOUT = -1;

	private final String identifier, displayName;
	private final KeyManager manager;
	private final int defaultKeyCode;
	private final boolean defaultShift, defaultCtrl, defaultAlt;
	final boolean isToggle;
	final int defaultTimeOut;
	private int keyCode;
	private boolean shift, ctrl, alt;
	int timeOut = 0;
	boolean isPressed = false;

	KeyBind(@NotNull final KeyManager manager,
	        @NotNull final String identifier,
	        @NotNull final String displayName,
	        final int defaultKeyCode, final int defaultTimeOut,
	        final boolean defaultShift, final boolean defaultControl, final boolean defaultAlt) {
		this.manager = manager;
		this.identifier = identifier;
		this.displayName = displayName;
		this.defaultTimeOut = defaultTimeOut;
		this.isToggle = this.defaultTimeOut == KeyBind.TOGGLE_TIMEOUT;
		this.defaultKeyCode = this.keyCode = defaultKeyCode;
		this.defaultShift = this.shift = defaultShift;
		this.defaultCtrl = this.ctrl = defaultControl;
		this.defaultAlt = this.alt = defaultAlt;
	}

	KeyBind(@NotNull final KeyManager manager,
	        @NotNull final String identifier,
	        @NotNull final String displayName,
	        final int defaultKeyCode,
	        final boolean defaultShift, final boolean defaultControl, final boolean defaultAlt) {
		this(manager, identifier, displayName, defaultKeyCode, KeyBind.TOGGLE_TIMEOUT, defaultShift, defaultControl, defaultAlt);
	}

	KeyBind(@NotNull final KeyManager manager,
	        @NotNull final String identifier,
	        @NotNull final String displayName,
	        final int defaultKeyCode) {
		this(manager, identifier, displayName, defaultKeyCode, false, false, false);
	}

	public void setKeyCode(final int keyCode) {
		this.keyCode = keyCode;
		this.manager.saveToDisk();
	}

	public void setShift(final boolean shift) {
		this.shift = shift;
		this.manager.saveToDisk();
	}

	public void setCtrl(final boolean ctrl) {
		this.ctrl = ctrl;
		this.manager.saveToDisk();
	}

	public void setAlt(final boolean alt) {
		this.alt = alt;
		this.manager.saveToDisk();
	}

	public void applyDefaults() {
		this.keyCode = this.defaultKeyCode;
		this.shift = this.defaultShift;
		this.ctrl = this.defaultCtrl;
		this.alt = this.defaultAlt;
		this.manager.saveToDisk();
	}

	void save(@NotNull final SDBTable store) {
		final SDBTable table = store.newTable(this.identifier);
		table.set("code", this.keyCode);
		table.set("shift", this.shift);
		table.set("ctrl", this.ctrl);
		table.set("alt", this.alt);
	}

	void load(@NotNull final SDBTable store) {
		if (!store.hasTable(this.identifier)) {
			this.save(store);
		}
		final SDBTable table = store.getTable(this.identifier);
		assert table != null;
		this.keyCode = table.getInteger("code");
		this.shift = table.getBoolean("shift");
		this.ctrl = table.getBoolean("ctrl");
		this.alt = table.getBoolean("alt");
	}

	public boolean isPressed() {
		return this.isPressed;
	}

	@NotNull
	public String getDisplayName() {
		return this.displayName;
	}

	public int getKeyCode() {
		return this.keyCode;
	}

	public boolean hasShift() {
		return this.shift;
	}

	public boolean hasCtrl() {
		return this.ctrl;
	}

	public boolean hasAlt() {
		return this.alt;
	}

	@NotNull
	public String getDisplayKeyCombination() {
		final StringBuilder builder = new StringBuilder();
		if (this.shift) {
			builder.append("SHIFT + ");
		}
		if (this.ctrl) {
			builder.append("CTRL + ");
		}
		if (this.alt) {
			builder.append("ALT + ");
		}
		return builder.append(Input.getKeyName(this.keyCode)).toString();
	}
}
