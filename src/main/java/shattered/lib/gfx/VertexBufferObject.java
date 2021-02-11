package shattered.lib.gfx;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.jetbrains.annotations.NotNull;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

final class VertexBufferObject {

	private final int id;
	private final int target;

	/**
	 * Create a new VBO with the given target.
	 *
	 * @param target The target of the buffer.
	 * @see org.lwjgl.opengl.GL15#glBindBuffer(int, int) GL15.glBindBuffer() for a list of accepted options
	 */
	private VertexBufferObject(final int target) {
		this.id = glGenBuffers();
		this.target = target;
	}

	/**
	 * Same as <code>VertexBufferObject(org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER)</code>
	 */
	public VertexBufferObject() {
		this(GL_ARRAY_BUFFER);
	}

	public void bind() {
		glBindBuffer(this.target, this.id);
	}

	public void unbind() {
		glBindBuffer(this.target, 0);
	}

	public void uploadData(@NotNull final FloatBuffer buffer, final int usage) {
		glBufferData(this.target, buffer, usage);
	}

	public void uploadData(final long size, final int usage) {
		glBufferData(this.target, size, usage);
	}

	public void uploadData(@NotNull final IntBuffer buffer, final int usage) {
		glBufferData(this.target, buffer, usage);
	}

	public void uploadData(@NotNull final ByteBuffer buffer, final int usage) {
		glBufferData(this.target, buffer, usage);
	}

	public void uploadSubData(final long offset, @NotNull final FloatBuffer data) {
		glBufferSubData(this.target, offset, data);
	}

	public void delete() {
		glDeleteBuffers(this.id);
	}

	public int getId() {
		return this.id;
	}
}