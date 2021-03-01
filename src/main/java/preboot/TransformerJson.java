package preboot;

import nl.appelgebakje22.preboot.ITransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
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
			public FieldVisitor visitField(final int access, final String name, final String descriptor, final String signature, final Object value) {
				return super.visitField((access & ~Opcodes.ACC_PRIVATE & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC, name, descriptor, signature, value);
			}
		});
		return writer.toByteArray();
	}

	@Override
	public int priority() {
		return 4;
	}
}