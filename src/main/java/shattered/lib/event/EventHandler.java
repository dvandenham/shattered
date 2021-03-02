package shattered.lib.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

final class EventHandler implements IEventListener {

	private static final AtomicInteger IDENTIFIERS = new AtomicInteger(0);
	private static final String MY_DESCRIPTION = Type.getInternalName(IEventListener.class);
	private static final Method INVOKE_METHOD = Objects.requireNonNull(EventHandler.findInvokeMethod());
	private static final String INVOKE_METHOD_DESCRIPTION = Type.getMethodDescriptor(EventHandler.INVOKE_METHOD);
	private static final EventBusClassLoader LOADER = new EventBusClassLoader();
	private static final HashMap<Method, Class<?>> CACHE = new HashMap<>();
	private final IEventListener handler;
	private final EventListener listenerInfo;
	private final MessageListener messageInfo;
	private final Class<?> listenerEventType;
	private final String name;
	private java.lang.reflect.Type filter = null;

	@Nullable
	private static Method findInvokeMethod() {
		final Method[] methods = IEventListener.class.getDeclaredMethods();
		for (final Method method : methods) {
			if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == Event.class) {
				return method;
			}
		}
		return null;
	}

	EventHandler(@NotNull final Object instance, @NotNull final Method listener, final boolean hasTypeParams) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		if (Modifier.isStatic(listener.getModifiers())) {
			this.handler = (IEventListener) this.createWrapper(listener).getConstructor().newInstance();
		} else {
			this.handler = (IEventListener) this.createWrapper(listener).getConstructor(Object.class).newInstance(instance);
		}
		this.listenerEventType = listener.getParameters()[0].getType();
		this.messageInfo = listener.getAnnotation(MessageListener.class);
		if (this.messageInfo == null) {
			this.listenerInfo = listener.getAnnotation(EventListener.class);
		} else {
			this.listenerInfo = this.messageInfo.listenerInfo();
		}
		this.name = "ASM: " + instance + " " + listener.getName() + Type.getMethodDescriptor(listener);
		if (hasTypeParams) {
			final java.lang.reflect.Type type = listener.getGenericParameterTypes()[0];
			if (type instanceof ParameterizedType) {
				this.filter = ((ParameterizedType) type).getActualTypeArguments()[0];
				if (this.filter instanceof ParameterizedType) {
					this.filter = ((ParameterizedType) this.filter).getRawType();
				}
			}
		}
	}

	@Override
	public void invoke(final Event<?> event) {
		if (this.handler == null) {
			return;
		}
		if (!event.isCancelled()) {
			if (this.filterPasses(event)) {
				this.handler.invoke(event);
			}
		}
	}

	private boolean filterPasses(@NotNull final Event<?> event) {
		if (this.filter == null) {
			return true;
		}
		final java.lang.reflect.Type type = event.getClass().getGenericSuperclass();
		return type instanceof ParameterizedType && ((ParameterizedType) type).getActualTypeArguments()[0] == this.filter;
	}

	@Nullable
	MessageListener getMessageInfo() {
		return this.messageInfo;
	}

	@NotNull
	EventListener getListenerInfo() {
		return this.listenerInfo;
	}

	@NotNull
	Class<?> getListenerEventType() {
		return this.listenerEventType;
	}

	private Class<?> createWrapper(@NotNull final Method listener) {
		Class<?> result = EventHandler.CACHE.get(listener);
		if (result == null) {
			//Create transformers
			final ClassWriter writer = new ClassWriter(0);
			MethodVisitor visitor;
			//Get transformation parameters
			final boolean isStatic = Modifier.isStatic(listener.getModifiers());
			final String name = this.getUniqueName(listener);
			final String description = name.replace('.', '/');
			final String instructionType = Type.getInternalName(listener.getDeclaringClass());
			final String eventType = Type.getInternalName(listener.getParameterTypes()[0]);
			//Generate class
			writer.visit(V1_8, ACC_PUBLIC | ACC_SUPER, description, null, "java/lang/Object", new String[]{EventHandler.MY_DESCRIPTION});
			writer.visitSource(".dynamic", null);
			if (!isStatic) {
				writer.visitField(ACC_PUBLIC, "instance", "Ljava/lang/Object;", null, null).visitEnd();
			}
			//Generate constructor
			visitor = writer.visitMethod(ACC_PUBLIC, "<init>", isStatic ? "()V" : "(Ljava/lang/Object;)V", null, null);
			visitor.visitCode();
			visitor.visitVarInsn(ALOAD, 0);
			visitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			if (!isStatic) {
				visitor.visitVarInsn(ALOAD, 0);
				visitor.visitVarInsn(ALOAD, 1);
				visitor.visitFieldInsn(PUTFIELD, description, "instance", "Ljava/lang/Object;");
			}
			visitor.visitInsn(RETURN);
			visitor.visitMaxs(2, 2);
			visitor.visitEnd();
			//Generate Invoke method
			visitor = writer.visitMethod(ACC_PUBLIC, EventHandler.INVOKE_METHOD.getName(), EventHandler.INVOKE_METHOD_DESCRIPTION, null, null);
			visitor.visitCode();
			visitor.visitVarInsn(ALOAD, 0);
			if (!isStatic) {
				visitor.visitFieldInsn(GETFIELD, description, "instance", "Ljava/lang/Object;");
				visitor.visitTypeInsn(CHECKCAST, instructionType);
			}
			visitor.visitVarInsn(ALOAD, 1);
			visitor.visitTypeInsn(CHECKCAST, eventType);
			visitor.visitMethodInsn(isStatic ? INVOKESTATIC : INVOKEVIRTUAL, instructionType, listener.getName(), Type.getMethodDescriptor(listener), false);
			visitor.visitInsn(RETURN);
			visitor.visitMaxs(2, 2);
			visitor.visitEnd();
			//Create and load class
			writer.visitEnd();
			result = EventHandler.LOADER.defineClass(name, writer.toByteArray());
			EventHandler.CACHE.put(listener, result);
		}
		return result;
	}

	@NotNull
	private String getUniqueName(@NotNull final Method method) {
		return String.format("%s_%d_%s_%s_%s", this.getClass().getName(), EventHandler.IDENTIFIERS.getAndIncrement(), method.getDeclaringClass().getSimpleName(), method.getName(), method.getParameterTypes()[0].getSimpleName());
	}

	@Override
	public String toString() {
		return this.name;
	}

	private static class EventBusClassLoader extends ClassLoader {

		private EventBusClassLoader() {
			super(null);
		}

		@Override
		protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
			return Class.forName(name, resolve, EventBusClassLoader.class.getClassLoader());
		}

		private Class<?> defineClass(final String name, final byte[] bytes) {
			return this.defineClass(name, bytes, 0, bytes.length);
		}
	}
}