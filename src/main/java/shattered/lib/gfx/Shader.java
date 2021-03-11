package shattered.lib.gfx;

import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;
import shattered.Shattered;
import shattered.lib.FileUtils;
import shattered.lib.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL20.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_FLOAT;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1fv;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform1iv;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

@SuppressWarnings("unused")
public final class Shader {

	private final int id;

	public Shader(@NotNull final ResourceLocation vertexResource, @NotNull final ResourceLocation fragmentResource) {
		final Function<ResourceLocation, String> pathGetter = resource -> String.format("/assets/%s/shaders/%s.glsl", resource.getNamespace(), resource.getResource());
		//Load asset streams
		final InputStream streamVertex = Shattered.class.getResourceAsStream(pathGetter.apply(vertexResource));
		final InputStream streamFragment = Shattered.class.getResourceAsStream(pathGetter.apply(fragmentResource));
		if (streamVertex == null) {
			throw new NullPointerException("Cannot load vertex shader");
		}
		if (streamFragment == null) {
			throw new NullPointerException("Cannot load fragment shader");
		}
		//Create ids
		final int idProgram = glCreateProgram();
		final int idVertex = glCreateShader(GL_VERTEX_SHADER);
		final int idFragment = glCreateShader(GL_FRAGMENT_SHADER);
		//Load and compile shaders
		glShaderSource(idVertex, Objects.requireNonNull(FileUtils.streamToString(streamVertex)));
		glCompileShader(idVertex);
		glShaderSource(idFragment, Objects.requireNonNull(FileUtils.streamToString(streamFragment)));
		glCompileShader(idFragment);
		//Create program and delete shaders
		glAttachShader(idProgram, idVertex);
		glAttachShader(idProgram, idFragment);
		glBindFragDataLocation(idProgram, 0, "outColor");
		glLinkProgram(idProgram);
		glValidateProgram(idProgram);
		GLHelper.checkError("Could not create shader");
		glDeleteShader(idVertex);
		glDeleteShader(idFragment);
		this.id = idProgram;
	}

	public void bind() {
		glUseProgram(this.id);
	}

	public static void unbind() {
		glUseProgram(0);
	}

	private int getAttributeLocation(@NotNull final String name) {
		return glGetAttribLocation(this.id, name);
	}

	public void enableAttribute(@NotNull final String name) {
		glEnableVertexAttribArray(this.getAttributeLocation(name));
	}

	public void disableAttribute(@NotNull final String name) {
		glDisableVertexAttribArray(this.getAttributeLocation(name));
	}

	public void setAttributePointer(@NotNull final String name, final int size, final int stride, final int offset) {
		glVertexAttribPointer(this.getAttributeLocation(name), size, GL_FLOAT, false, stride, offset);
	}

	private int getUniformLocation(@NotNull final String name) {
		return glGetUniformLocation(this.id, name);
	}

	public void setUniform1I(@NotNull final String name, final int value) {
		glUniform1i(this.getUniformLocation(name), value);
	}

	public void setUniform1Iv(@NotNull final String name, final int value, final int count) {
		glUniform1iv(this.getUniformLocation(name), new int[]{count, value});
	}

	public void setUniform1B(@NotNull final String name, final boolean value) {
		this.setUniform1I(name, value ? GL_TRUE : GL_FALSE);
	}

	public void setUniform1F(@NotNull final String name, final float value) {
		glUniform1f(this.getUniformLocation(name), value);
	}

	public void setUniform1Fv(@NotNull final String name, final float value, final float count) {
		glUniform1fv(this.getUniformLocation(name), new float[]{count, value});
	}

	public void setUniform2F(@NotNull final String name, @NotNull final Vector2f vector) {
		glUniform2f(this.getUniformLocation(name), vector.x, vector.y);
	}

	public void setUniform3F(@NotNull final String name, @NotNull final Vector3f vector) {
		glUniform3f(this.getUniformLocation(name), vector.x, vector.y, vector.z);
	}

	public void setUniform4F(@NotNull final String name, @NotNull final Vector4f vector) {
		glUniform4f(this.getUniformLocation(name), vector.x, vector.y, vector.z, vector.w);
	}

	public void setUniformMat4(@NotNull final String name, @NotNull final Matrix4f matrix) {
		glUniformMatrix4fv(this.getUniformLocation(name), false, matrix.get(new float[16]));
	}

	public int getId() {
		return this.id;
	}

	public void destroy() {
		glDeleteProgram(this.id);
	}
}