package nl.appelgebakje22.preboot;

import org.objectweb.asm.Opcodes;

public interface ITransformer {

	int ASM_VERSION = Opcodes.ASM9;

	byte[] transform(String className, byte[] bytes);

	default int priority() {
		return 0;
	}
}