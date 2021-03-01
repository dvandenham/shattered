package preboot;

import java.util.List;
import java.util.stream.Collectors;
import nl.appelgebakje22.preboot.ITransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;
import shattered.core.event.MessageListener;

final class TransformerMessageListener implements ITransformer {

	private static final String DESCRIPTOR = Type.getDescriptor(MessageListener.class);

	@Override
	public byte[] transform(final String className, final byte[] bytes) {
		final ClassReader reader = new ClassReader(bytes);
		final ClassNode node = new ClassNode();
		reader.accept(node, 0);
		final List<String> validMethods = node.methods.stream().filter(methodNode ->
				methodNode.visibleAnnotations != null && methodNode.visibleAnnotations.stream().anyMatch(annotationNode ->
						annotationNode.desc != null && annotationNode.desc.equals(TransformerMessageListener.DESCRIPTOR)
				)
		).map(methodNode -> methodNode.desc).collect(Collectors.toList());
		if (validMethods.isEmpty()) {
			return null;
		}
		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		final CheckClassAdapter adapter = new CheckClassAdapter(writer);
		node.accept(new ClassVisitor(Opcodes.ASM5, adapter) {

			@Override
			public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
				if (validMethods.contains(descriptor)) {
					return super.visitMethod((access & ~Opcodes.ACC_PRIVATE & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC, name, descriptor, signature, exceptions);
				} else {
					return super.visitMethod(access, name, descriptor, signature, exceptions);
				}
			}
		});
		return writer.toByteArray();
	}

	@Override
	public int priority() {
		return 3;
	}
}