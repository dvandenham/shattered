package shattered.lib;

import java.util.function.Supplier;

public final class Lazy<T> implements Supplier<T> {

	private final Supplier<T> supplier;
	private boolean fetched = false;
	private T value;

	private Lazy(final Supplier<T> supplier) {
		this.supplier = supplier;
	}

	@Override
	public T get() {
		if (!this.fetched) {
			this.fetched = true;
			this.value = this.supplier.get();
		}
		return this.value;
	}

	public static <T> Lazy<T> of(final Supplier<T> supplier) {
		return new Lazy<>(supplier);
	}
}