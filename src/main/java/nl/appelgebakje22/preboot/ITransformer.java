package nl.appelgebakje22.preboot;

public interface ITransformer {

	byte[] transform(String className, byte[] bytes);

	default int priority() {
		return 0;
	}
}