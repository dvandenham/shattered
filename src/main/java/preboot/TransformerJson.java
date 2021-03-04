package preboot;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;
import shattered.lib.json.Json;

final class TransformerJson implements ITransformer {

	private static final String DESCRIPTOR = Type.getDescriptor(Json.class);

	@Override
	public byte[] transform(final String className, final byte[] bytes) {
		final ClassReader reader = new ClassReader(bytes);
		final ClassNode node = new ClassNode();
		reader.accept(node, 0);
		final boolean hasAnnotation = node.visibleAnnotations != null && node.visibleAnnotations.stream().anyMatch(el ->
				el.desc != null && el.desc.equals(TransformerJson.DESCRIPTOR)
		);
		if (!hasAnnotation) {
			return null;
		}
		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final CheckClassAdapter adapter = new CheckClassAdapter(writer);
		node.accept(new ClassVisitor(Opcodes.ASM5, adapter) {

			@Override
			public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
				if (name.equals("<init>")) {
					final int newAccess = (access & ~Opcodes.ACC_PRIVATE & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC;
					return super.visitMethod(newAccess, name, descriptor, signature, exceptions);
				} else {
					return super.visitMethod(access, name, descriptor, signature, exceptions);
				}
			}

			@Override
			public FieldVisitor visitField(final int access, final String name, final String descriptor, final String signature, final Object value) {
				final int newAccess = (access & ~Opcodes.ACC_PRIVATE & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC;
				return super.visitField(newAccess, name, descriptor, signature, value);
			}
		});
		return writer.toByteArray();
	}

	@Override
	public int priority() {
		return 4;
	}
}