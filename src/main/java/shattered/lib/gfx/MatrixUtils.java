package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import shattered.lib.math.Dimension;

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
		final Dimension size = Display.getSize();
		MatrixUtils.MATRIX_ORTHO.identity().ortho(0, size.getWidth(), size.getHeight(), 0, 1, -1);
		return MatrixUtils.MATRIX_ORTHO;
	}
}