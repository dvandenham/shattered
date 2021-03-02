package preboot;

import nl.appelgebakje22.preboot.ITransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;

final class TransformerPrivateStaticFinal implements ITransformer {

	@Override
	public byte[] transform(final String className, final byte[] bytes) {
		final ClassReader reader = new ClassReader(bytes);
		final ClassNode node = new ClassNode();
		reader.accept(node, 0);

		if (node.fields.stream().noneMatch(fieldNode ->
				fieldNode.access == (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL)
						&& fieldNode.value == null
		)) {
			return null;
		}

		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final CheckClassAdapter adapter = new CheckClassAdapter(writer);
		node.accept(new ClassVisitor(ITransformer.ASM_VERSION, adapter) {

			@Override
			public FieldVisitor visitField(final int access, final String name, final String descriptor, final String signature, final Object value) {
				if (access == (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL)) {
					return super.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, name, descriptor, signature, null);
				}
				return super.visitField(access, name, descriptor, signature, value);
			}
		});
		return writer.toByteArray();
	}

	@Override
	public int priority() {
		return Integer.MAX_VALUE;
	}
}