package preboot;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class AnnotationRegistry {

	static final HashMap<Class<? extends Annotation>, List<Class<?>>> REGISTRY = new HashMap<>();

	public static List<Class<?>> getAnnotatedClasses(final Class<? extends Annotation> annotation) {
		if (annotation == null) {
			return null;
		} else {
			return AnnotationRegistry.REGISTRY.computeIfAbsent(annotation, k -> Collections.emptyList());
		}
	}
}