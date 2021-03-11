package preboot;

import java.lang.reflect.Method;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;

final class TransformerObjectInstantiation implements ITransformer {

	private static final String CONSTRUCTOR_TYPE = Type.getMethodDescriptor(Type.VOID_TYPE);
	private static final String CLASS_OBJECT_NAME = Type.getInternalName(Object.class);
	private static final String METHOD_GETCLASS_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(Class.class));
	private static final String CLASS_PREBOOT_CLASSLOADER_NAME = Type.getInternalName(PrebootClassLoader.class);
	private static final Method METHOD_ONINSTANTIATION = TransformerObjectInstantiation.findOnInstantiationMethod();
	private static final String METHOD_ONINSTANTIATION_NAME = TransformerObjectInstantiation.METHOD_ONINSTANTIATION.getName();
	private static final String METHOD_ONINSTANTIATION_DESCRIPTOR = Type.getMethodDescriptor(TransformerObjectInstantiation.METHOD_ONINSTANTIATION);

	@Override
	public byte[] transform(final String className, final byte[] bytes) {
		final ClassReader reader = new ClassReader(bytes);
		final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);

		final ClassVisitor classVisitor = new ClassVisitor(ASM5, writer) {
			@Override
			public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
				if (!name.equals("<init>")) {
					return super.visitMethod(access, name, descriptor, signature, exceptions);
				} else {
					return new MethodVisitor(ASM5, super.visitMethod(access, name, descriptor, signature, exceptions)) {

						@Override
						public void visitInsn(final int opcode) {
							if (opcode == RETURN) {
								this.visitVarInsn(ALOAD, 0);
								this.visitVarInsn(ALOAD, 0);
								this.visitMethodInsn(INVOKEVIRTUAL, TransformerObjectInstantiation.CLASS_OBJECT_NAME, "getClass", TransformerObjectInstantiation.METHOD_GETCLASS_DESCRIPTOR, false);
								this.visitMethodInsn(INVOKESTATIC, TransformerObjectInstantiation.CLASS_PREBOOT_CLASSLOADER_NAME, TransformerObjectInstantiation.METHOD_ONINSTANTIATION_NAME, TransformerObjectInstantiation.METHOD_ONINSTANTIATION_DESCRIPTOR, false);
							}
							super.visitInsn(opcode);
						}
					};
				}
			}
		};
		reader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
		return writer.toByteArray();
	}

	@Override
	public int priority() {
		return -1;
	}

	private static Method findOnInstantiationMethod() {
		for (final Method method : PrebootClassLoader.class.getDeclaredMethods()) {
			if (method.getReturnType().equals(Object.class) && method.getParameterCount() == 2) {
				final Class<?>[] params = method.getParameterTypes();
				if (params[0].equals(Object.class) && params[1].equals(Class.class)) {
					return method;
				}
			}
		}
		throw new RuntimeException(new NoSuchMethodException());
	}
}