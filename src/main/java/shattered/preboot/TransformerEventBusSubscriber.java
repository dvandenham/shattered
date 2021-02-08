package shattered.preboot;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;
import shattered.core.event.EventBusSubscriber;

final class TransformerEventBusSubscriber implements ITransformer {

	private static final String DESCRIPTOR = Type.getDescriptor(EventBusSubscriber.class);

	@Override
	public byte[] transform(final String className, final byte[] bytes) {
		final ClassReader reader = new ClassReader(bytes);
		final ClassNode   node   = new ClassNode();
		reader.accept(node, 0);
		final boolean hasAnnotation = node.visibleAnnotations != null && node.visibleAnnotations.stream().anyMatch(el ->
				el.desc != null && el.desc.equals(TransformerEventBusSubscriber.DESCRIPTOR)
		);
		if (!hasAnnotation) {
			return null;
		}
		final ClassWriter       writer  = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final CheckClassAdapter adapter = new CheckClassAdapter(writer);
		node.accept(new ClassVisitor(Opcodes.ASM5, adapter) {

			@Override
			public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
				super.visit(version, (access & ~Opcodes.ACC_PRIVATE & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC, name, signature, superName, interfaces);
			}
		});
		return writer.toByteArray();
	}

	@Override
	public int priority() {
		return 1;
	}
}