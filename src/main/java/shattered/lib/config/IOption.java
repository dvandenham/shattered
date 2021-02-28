package shattered.lib.config;

import org.jetbrains.annotations.NotNull;
import shattered.lib.json.JDB;

abstract class IOption<T> {

	@NotNull
	protected final String preference;
	private final T defaultValue;
	T value;

	IOption(@NotNull final String preference, @NotNull final T defaultValue) {
		this.preference = preference;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	abstract void serialize(@NotNull JDB store);

	@NotNull
	abstract T deserialize(@NotNull JDB store);

	public void reset() {
		this.set(this.defaultValue);
	}

	public void set(@NotNull final T value) {
		this.value = value;
		ConfigManager.save();
	}

	public T get() {
		return this.value;
	}
}