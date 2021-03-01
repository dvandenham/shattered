package nl.appelgebakje22.preboot;

import java.lang.annotation.Annotation;
import java.util.List;

public interface PrebootRegistry {
	void registerTransformer(ITransformer transformer);

	void load();

	List<Class<?>> getAnnotatedClasses(Class<? extends Annotation> annotation);
}
