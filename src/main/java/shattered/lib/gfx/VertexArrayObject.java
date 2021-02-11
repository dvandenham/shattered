package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

final class VertexArrayObject {

	private static VertexArrayObject instance;
	private final int id;

	public VertexArrayObject() {
		this.id = glGenVertexArrays();
	}

	public void bind() {
		glBindVertexArray(this.id);
	}

	public void delete() {
		glDeleteVertexArrays(this.id);
	}

	public int getId() {
		return this.id;
	}

	@NotNull
	public static VertexArrayObject get() {
		if (VertexArrayObject.instance == null) {
			VertexArrayObject.instance = new VertexArrayObject();
		}
		return VertexArrayObject.instance;
	}

	public static void recreate() {
		VertexArrayObject.get().delete();
		VertexArrayObject.instance = null;
	}
}