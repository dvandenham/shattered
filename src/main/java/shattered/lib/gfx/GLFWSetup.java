package shattered.lib.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;
import shattered.core.event.EventBus;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.Config;
import shattered.Shattered;
import shattered.lib.math.Dimension;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_REFRESH_RATE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryUtil.NULL;

@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
final class GLFWSetup {

	private static final boolean DEVELOPER_MODE = Boolean.getBoolean("shattered.developer.glfw");
	private static final Logger LOGGER = LogManager.getLogger("GLFW");
	static final Dimension DISPLAY_SIZE = Dimension.createMutable(0, 0);
	static long windowId;
	static double scale = 1;

	private GLFWSetup() {
	}

	@MessageListener("init_glfw")
	private static void onInitializeGLFW(final MessageEvent event) {
		GLFWSetup.LOGGER.debug("Initialing GLFW");
		GLFWSetup.DISPLAY_SIZE.setWidth(Config.DISPLAY_SIZE.get().getWidth());
		GLFWSetup.DISPLAY_SIZE.setHeight(Config.DISPLAY_SIZE.get().getHeight());
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) {
			Shattered.crash("Could not initialize GLFW!");
		}

		GLFWSetup.LOGGER.debug("Applying window hints");
		GLFWSetup.applyWindowHints();
		GLFWSetup.LOGGER.debug("Applying window properties");
		GLFWSetup.applyWindowProperties();
		GLFWSetup.LOGGER.debug("Registering callbacks");
		Callbacks.register(GLFWSetup.windowId);
	}

	@MessageListener("shutdown_glfw")
	private static void onSystemShutdown(final MessageEvent ignored) {
		GLFWSetup.LOGGER.debug("Cleaning up used resources");
		VertexArrayObject.get().delete();
		FrameBufferObject.get().delete();
		GLFWSetup.LOGGER.debug("Destroying root window");
		GLFWSetup.destroyWindow(GLFWSetup.windowId);
		GLFWSetup.LOGGER.debug("Releasing error and debug callbacks");
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
		Objects.requireNonNull(GLUtil.setupDebugMessageCallback()).free();
		GLFWSetup.LOGGER.debug("Terminating GLFW");
		glfwTerminate();
	}

	private static void applyWindowHints() {
		glfwDefaultWindowHints();

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		glfwWindowHint(GLFW_REFRESH_RATE, GLFW_DONT_CARE);

		final GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		assert vidMode != null;
		glfwWindowHint(GLFW_RED_BITS, vidMode.redBits());
		glfwWindowHint(GLFW_GREEN_BITS, vidMode.greenBits());
		glfwWindowHint(GLFW_BLUE_BITS, vidMode.blueBits());

		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFWSetup.DEVELOPER_MODE ? GLFW_TRUE : GLFW_FALSE);
		glfwSetErrorCallback((error, description) -> GLFWSetup.LOGGER.error("[ErrorID={}]{}", error, description));
	}

	private static void applyWindowProperties() {
		GLFWSetup.windowId = glfwCreateWindow(GLFWSetup.DISPLAY_SIZE.getWidth(), GLFWSetup.DISPLAY_SIZE.getHeight(), Shattered.NAME, NULL, NULL);

		try (final MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer bufX = stack.mallocInt(1);
			final IntBuffer bufY = stack.mallocInt(1);

			glfwGetWindowSize(GLFWSetup.windowId, bufX, bufY);

			final int sizeX = bufX.get(0);
			final int sizeY = bufY.get(0);

			GLFWSetup.DISPLAY_SIZE.setWidth(sizeX);
			GLFWSetup.DISPLAY_SIZE.setHeight(sizeY);

			final GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			assert vidMode != null;
			glfwSetWindowPos(GLFWSetup.windowId, (vidMode.width() - sizeX) / 2, (vidMode.height() - sizeY) / 2);
		}

		glfwMakeContextCurrent(GLFWSetup.windowId);
		GL.createCapabilities();

		glfwSwapInterval(Config.DISPLAY_VSYNC.get() ? 1 : 0);

		if (GLFWSetup.DEVELOPER_MODE) {
			GLUtil.setupDebugMessageCallback(System.err);
			GL43.glDebugMessageControl(GL43.GL_DONT_CARE, GL43.GL_DEBUG_TYPE_OTHER, GL43.GL_DEBUG_SEVERITY_NOTIFICATION, (IntBuffer) null, false);
		}

		final Object[] icon = GLFWSetup.loadIcon();
		if (icon == null) {
			GLFWSetup.LOGGER.error("Could not load window icon!");
		} else {
			final GLFWImage iconImage = GLFWImage.malloc();
			final GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
			iconImage.set((int) icon[0], (int) icon[1], (ByteBuffer) icon[2]);
			iconBuffer.put(0, iconImage);
			glfwSetWindowIcon(GLFWSetup.windowId, iconBuffer);
		}
	}

	@Nullable
	private static Object[] loadIcon() {
		try (final InputStream stream = Shattered.class.getResourceAsStream("/assets/shattered/icon.png")) {
			if (stream == null) {
				return null;
			}
			final BufferedImage image = ImageIO.read(stream);
			final MessageEvent event = new MessageEvent("glfw_create_texture", image);
			if (Shattered.SYSTEM_BUS.post(event)) {
				return null;
			}
			final Supplier<?> response = event.getResponse();
			return response != null ? (Object[]) response.get() : null;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setScaledResolution(final int logicalWidth, final int logicalHeight, @NotNull final Shader shader) {
		final double scaleX = (double) GLFWSetup.DISPLAY_SIZE.getWidth() / logicalWidth;
		final double scaleY = (double) GLFWSetup.DISPLAY_SIZE.getHeight() / logicalHeight;
		GLFWSetup.setScaledResolution(Math.min(scaleX, scaleY), shader);
	}

	public static void setScaledResolution(final double scale, @NotNull final Shader shader) {
		GLFWSetup.scale = scale;
		shader.setUniformMat4("matProj", MatrixUtils.ortho());
	}

	static void sendResizeEvent() {
		Config.DISPLAY_SIZE.set(GLFWSetup.DISPLAY_SIZE.toImmutable());
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		FrameBufferObject.recreate();
		VertexArrayObject.recreate();
		EventBus.post(new DisplayResizedEvent());
	}

	private static void destroyWindow(final long id) {
		glfwFreeCallbacks(id);
		glfwDestroyWindow(id);
	}
}