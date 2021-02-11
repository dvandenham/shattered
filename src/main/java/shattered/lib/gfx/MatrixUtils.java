package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public final class MatrixUtils {

	private static final Matrix4f MATRIX_ORTHO = new Matrix4f();

	private MatrixUtils() {
	}

	@NotNull
	public static Matrix4f identity() {
		return new Matrix4f().identity();
	}

	@NotNull
	public static Matrix4f ortho() {
		MatrixUtils.MATRIX_ORTHO.identity().ortho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		return MatrixUtils.MATRIX_ORTHO;
	}
}