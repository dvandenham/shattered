package shattered.lib.config;

import java.util.Objects;
import shattered.core.sdb.SDBTable;
import org.jetbrains.annotations.NotNull;

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

	abstract void serialize(@NotNull SDBTable store);

	@NotNull
	abstract T deserialize(@NotNull SDBTable store);

	public void reset() {
		this.set(this.defaultValue);
	}

	public void set(@NotNull final T value) {
		final T current = this.get();
		this.value = value;
		if (!Objects.equals(current, this.value)) {
			ConfigManager.save();
		}
	}

	public T get() {
		return this.value;
	}
}