package shattered.lib.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

public abstract class RegistryParser<T> {

	@NotNull
	protected abstract Class<?> getWrapperClass();

	@NotNull
	protected abstract T parse(@NotNull Object data);

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface RegistryParserMetadata {
		Class<?> value();
	}
}