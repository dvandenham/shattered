package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

final class VertexArrayObject {

	private static VertexArrayObject INSTANCE;
	private final int id;

	public VertexArrayObject() {
		this.id = glGenVertexArrays();
	}

	public void bind() {
		glBindVertexArray(this.id);
	}

	void delete() {
		glDeleteVertexArrays(this.id);
	}

	@NotNull
	public static VertexArrayObject get() {
		if (VertexArrayObject.INSTANCE == null) {
			VertexArrayObject.INSTANCE = new VertexArrayObject();
		}
		return VertexArrayObject.INSTANCE;
	}

	public static void recreate() {
		VertexArrayObject.get().delete();
		VertexArrayObject.INSTANCE = null;
	}
}