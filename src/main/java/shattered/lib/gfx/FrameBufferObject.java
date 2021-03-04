package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glFlush;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;

public final class FrameBufferObject {

	private static FrameBufferObject INSTANCE;
	private final int id;
	private final int texId;

	public FrameBufferObject() {
		this.id = glGenFramebuffers();
		this.texId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, this.texId);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Display.getWidth(), Display.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	}

	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, this.id);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texId, 0);
	}

	private void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	private void delete() {
		glDeleteFramebuffers(this.id);
	}

	private void checkStatus() {
		final int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		switch (status) {
			case GL_FRAMEBUFFER_COMPLETE:
				break;
			case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
			case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
			case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
			case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE");
			default:
				throw new RuntimeException("" + status);
		}
	}

	private void renderToDrawBuffer() {
		glBindFramebuffer(GL_READ_FRAMEBUFFER, this.id);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glBlitFramebuffer(0, 0, Display.getWidth(), Display.getHeight(), 0, 0, Display.getWidth(), Display.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
	}

	public void render() {
		this.checkStatus();
		glFlush();
		this.unbind();
		this.renderToDrawBuffer();
	}

	@NotNull
	public static FrameBufferObject get() {
		if (FrameBufferObject.INSTANCE == null) {
			FrameBufferObject.INSTANCE = new FrameBufferObject();
		}
		return FrameBufferObject.INSTANCE;
	}

	public static void recreate() {
		FrameBufferObject.get().delete();
		FrameBufferObject.INSTANCE = null;
	}
}