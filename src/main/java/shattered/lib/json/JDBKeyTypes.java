package shattered.lib.json;

import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public enum JDBKeyTypes {

	BOOLEAN(obj -> obj instanceof Boolean),
	BYTE(obj -> obj instanceof Number),
	SHORT(obj -> obj instanceof Number),
	INTEGER(obj -> obj instanceof Number),
	FLOAT(obj -> obj instanceof Number),
	DOUBLE(obj -> obj instanceof Number),
	LONG(obj -> obj instanceof Number),
	CHARACTER(obj -> obj instanceof String && ((String) obj).length() == 1),
	STRING(obj -> obj instanceof String),
	ARRAY(obj -> obj instanceof JDBArray),
	TABLE(obj -> obj instanceof JDBTable);

	private final Predicate<@NotNull Object> predicate;

	JDBKeyTypes(final Predicate<@NotNull Object> predicate) {
		this.predicate = predicate;
	}

	public boolean matchesObject(@NotNull final Object object) {
		return this.predicate.test(object);
	}
}