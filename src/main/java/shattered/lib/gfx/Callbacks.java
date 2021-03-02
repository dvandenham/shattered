package shattered.lib.gfx;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.lwjgl.glfw.GLFW;
import shattered.Shattered;
import shattered.lib.event.MessageEvent;

final class Callbacks {

	private Callbacks() {
	}

	public static void register(final long windowId) {
		GLFW.glfwSetFramebufferSizeCallback(windowId, Callbacks::onFramebufferSizeChanged);
		Callbacks.setInputCallbacks(windowId);
		GLFW.glfwSetWindowCloseCallback(windowId, id -> Shattered.getInstance().stop());
	}

	private static void onFramebufferSizeChanged(final long windowId, final int width, final int height) {
		GLFWSetup.DISPLAY_SIZE.setWidth(width);
		GLFWSetup.DISPLAY_SIZE.setHeight(height);
		GLFWSetup.sendResizeEvent();
	}

	@SuppressWarnings("unchecked")
	private static void setInputCallbacks(final long windowId) {
		final MessageEvent event = new MessageEvent("input_setup_callbacks");
		Shattered.SYSTEM_BUS.post(event);
		final Object[] callbacks = (Object[]) Objects.requireNonNull(event.getResponse()).get();

		GLFW.glfwSetCursorPosCallback(windowId, (id, x, y) ->
				((BiConsumer<Double, Double>) callbacks[0]).accept(x, y)
		);
		GLFW.glfwSetCursorEnterCallback(windowId, (id, entered) ->
				((Consumer<Boolean>) callbacks[1]).accept(entered)
		);
		GLFW.glfwSetMouseButtonCallback(windowId, (id, button, action, mods) ->
				((Consumer<Boolean>) callbacks[2 + button]).accept(action == GLFW.GLFW_PRESS)
		);
		GLFW.glfwSetKeyCallback(windowId, (id, keycode, scancode, action, mods) -> {
			//TODO Implement
		});
	}
}