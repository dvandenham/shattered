package shattered.lib.gfx;

import java.nio.ByteBuffer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;
import shattered.lib.Color;

final class BufferBuilder {

	private final VertexFormat      format;
	private final int               drawMode;
	private final VertexArrayObject vao;
	private final int               size;
	private final Runnable          callback;
	private       ByteBuffer        buffer;
	private       int               vertices = 0;

	public BufferBuilder(@NotNull final VertexFormat format, final int size, final int drawMode, final Runnable callback) {
		this.format = format;
		this.buffer = MemoryUtil.memAlloc(format.size * size);
		this.drawMode = drawMode;
		this.vao = VertexArrayObject.get();
		this.size = size;
		this.callback = callback;
	}

	public BufferBuilder(@NotNull final VertexFormat format, final int drawMode, final Runnable callback) {
		this(format, 1, drawMode, callback);
	}

	private void grow() {
		if (this.buffer.remaining() > this.format.size) {
			return;
		}
		final int        position  = this.buffer.position();
		final ByteBuffer newBuffer = MemoryUtil.memAlloc(this.buffer.capacity() + this.format.size * this.size);
		this.buffer.position(0);
		newBuffer.put(this.buffer);
		newBuffer.rewind();
		newBuffer.position(position);
		MemoryUtil.memFree(this.buffer);
		this.buffer = newBuffer;
	}

	@NotNull
	public BufferBuilder position(final float x, final float y) {
		this.buffer.putFloat(x);
		this.buffer.putFloat(y);
		this.buffer.putFloat(0f);
		return this;
	}

	@NotNull
	public BufferBuilder color(@NotNull final Color color) {
		this.buffer.putFloat(color.getRed());
		this.buffer.putFloat(color.getGreen());
		this.buffer.putFloat(color.getBlue());
		this.buffer.putFloat(color.getAlpha());
		return this;
	}

	@NotNull
	public BufferBuilder uv(final float u, final float v) {
		this.buffer.putFloat(u);
		this.buffer.putFloat(v);
		return this;
	}

	public void endVertex() {
		++this.vertices;
		this.grow();
	}

	public void draw() {
		this.buffer.flip();
		final VertexBufferObject vbo = new VertexBufferObject();
		vbo.bind();
		vbo.uploadData(this.buffer, GL15.GL_STREAM_DRAW);
		this.callback.run();
		this.vao.bind();
		for (final VertexFormatElement element : this.format.elements) {
			GL20.glEnableVertexAttribArray(element.index);
		}
		final int stride = this.format.size;
		for (int i = 0; i < this.format.elements.length; ++i) {
			final VertexFormatElement element = this.format.elements[i];
			GL20.glVertexAttribPointer(element.index, element.elements, element.type.glConstant, false, stride, this.format.offsets[i]);
		}
		GL11.glDrawArrays(this.drawMode, 0, this.vertices);
		for (final VertexFormatElement element : this.format.elements) {
			GL20.glDisableVertexAttribArray(element.index);
		}
		vbo.unbind();
		vbo.delete();
		MemoryUtil.memFree(this.buffer);
	}
}