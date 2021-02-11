package shattered;

import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.GLHelper;
import shattered.lib.gfx.StringData;
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

	private final AtomicBoolean RUNNING = new AtomicBoolean(false);
	private final AtomicBoolean STOPPED = new AtomicBoolean(false);
	private final Shattered shattered;
	private Thread thread;

	public ThreadLoadingScreen(@NotNull final Shattered shattered) {
		this.shattered = shattered;
	}

	public void start() {
		glfwShowWindow(Display.getWindowId());
		glfwMakeContextCurrent(NULL);
		this.RUNNING.set(true);
		this.thread = new Thread(this, "Loading screen");
		this.thread.start();
	}

	public void tryStop() {
		this.RUNNING.set(false);
		while (!this.STOPPED.get()) {
			try {
				this.thread.join(100);
			} catch (final InterruptedException ignored) {
			}
		}
		glfwMakeContextCurrent(Display.getWindowId());
	}

	@Override
	public void run() {
		try {
			while (this.RUNNING.get()) {
				//We need to use the lock so the main thread can request the context
				GLHelper.requestContext();
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				glEnable(GL_BLEND);
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				this.shattered.tessellator.drawQuick(Display.getBounds(), StaticAssets.RESOURCE_TEXTURE_ARGON);
				this.shattered.fontRenderer.setFontSize(48);
				this.shattered.fontRenderer.writeQuickCentered(Display.getBounds(), new StringData("LOADING").localize(false));
				this.shattered.fontRenderer.revertFontSize();
				glfwSwapBuffers(Display.getWindowId());
				GLHelper.releaseContext();
			}
			this.STOPPED.set(true);
		} catch (final Throwable e) {
			e.printStackTrace();
			Runtime.getRuntime().halt(-1);
		}
	}
}