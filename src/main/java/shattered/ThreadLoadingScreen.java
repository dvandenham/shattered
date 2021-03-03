package shattered;

import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL;
import shattered.lib.gfx.Display;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryUtil.NULL;

final class ThreadLoadingScreen implements Runnable {

	private static final AtomicBoolean RUNNING = new AtomicBoolean(true);
	private final Shattered shattered;
	private Thread thread;

	public ThreadLoadingScreen(@NotNull final Shattered shattered) {
		this.shattered = shattered;
	}

	public void start() {
		glfwShowWindow(Display.getWindowId());
		glfwMakeContextCurrent(NULL);
		this.thread = new Thread(this, "Loading screen");
		this.thread.start();
	}

	public void tryStop() {
		ThreadLoadingScreen.RUNNING.lazySet(false);
		try {
			if (this.thread.isAlive()) {
				this.thread.join();
			}
		} catch (final InterruptedException ignored) {
		}
		glfwMakeContextCurrent(Display.getWindowId());
	}

	@Override
	public void run() {
		glfwMakeContextCurrent(Display.getWindowId());
		GL.createCapabilities();
		try {
			while (ThreadLoadingScreen.RUNNING.get()) {
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				glEnable(GL_BLEND);
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				this.shattered.tessellator.start();
				this.shattered.tessellator.set(Display.getBounds(), Assets.TEXTURE_ARGON);
				this.shattered.tessellator.next();
				this.shattered.tessellator.set(0, 0, Assets.TEXTURE_LOADING);
				this.shattered.tessellator.center(Display.getSize());
				this.shattered.tessellator.draw();
				glfwSwapBuffers(Display.getWindowId());
			}
		} catch (final Throwable e) {
			e.printStackTrace();
			Runtime.getRuntime().halt(-1);
		}
		glfwMakeContextCurrent(NULL);
	}
}