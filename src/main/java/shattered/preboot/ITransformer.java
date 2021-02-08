package shattered.preboot;

interface ITransformer {

	byte[] transform(String className, byte[] bytes);

	default int priority() {
		return 0;
	}
}