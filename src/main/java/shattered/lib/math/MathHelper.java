package shattered.lib.math;

import org.jetbrains.annotations.NotNull;

public final class MathHelper {

	private static final float[] SINE = new float[65536];

	static {
		for (int i = 0; i < MathHelper.SINE.length; ++i) {
			MathHelper.SINE[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
		}
	}

	private MathHelper() {
	}

	public static float sin(final float value) {
		return MathHelper.SINE[(int) (value * 10430.378F) & 65535];
	}

	public static float cos(final float value) {
		return MathHelper.SINE[(int) (value * 10430.378F + 16384.0F) & 65535];
	}

	@NotNull
	public static Point min(@NotNull final Point a, @NotNull final Point b) {
		return Point.create(Math.min(a.getX(), b.getY()), Math.min(a.getX(), b.getY()));
	}

	@NotNull
	public static Dimension min(@NotNull final Dimension a, @NotNull final Dimension b) {
		return Dimension.create(Math.min(a.getWidth(), b.getWidth()), Math.min(a.getHeight(), b.getHeight()));
	}

	@NotNull
	public static Rectangle min(@NotNull final Rectangle a, @NotNull final Rectangle b) {
		return Rectangle.create(MathHelper.min(a.getPosition(), b.getPosition()), MathHelper.min(a.getSize(), b.getSize()));
	}

	@NotNull
	public static Point max(@NotNull final Point a, @NotNull final Point b) {
		return Point.create(Math.max(a.getX(), b.getY()), Math.max(a.getX(), b.getY()));
	}

	@NotNull
	public static Dimension max(@NotNull final Dimension a, @NotNull final Dimension b) {
		return Dimension.create(Math.max(a.getWidth(), b.getWidth()), Math.max(a.getHeight(), b.getHeight()));
	}

	@NotNull
	public static Rectangle max(@NotNull final Rectangle a, @NotNull final Rectangle b) {
		return Rectangle.create(MathHelper.max(a.getPosition(), b.getPosition()), MathHelper.max(a.getSize(), b.getSize()));
	}

	public static byte clamp(final byte value, final byte min, final byte max) {
		return value < min ? min : value > max ? max : value;
	}

	public static short clamp(final short value, final short min, final short max) {
		return value < min ? min : value > max ? max : value;
	}

	public static int clamp(final int value, final int min, final int max) {
		return value < min ? min : Math.min(value, max);
	}

	public static long clamp(final long value, final long min, final long max) {
		return value < min ? min : Math.min(value, max);
	}

	public static float clamp(final float value, final float min, final float max) {
		return value < min ? min : Math.min(value, max);
	}

	public static double clamp(final double value, final double min, final double max) {
		return value < min ? min : Math.min(value, max);
	}

	@NotNull
	public static Point clamp(@NotNull final Point value, @NotNull final Point min, @NotNull final Point max) {
		return Point.create(MathHelper.clamp(value.getX(), min.getX(), max.getX()), MathHelper.clamp(value.getY(), min.getY(), max.getY()));
	}

	@NotNull
	public static Dimension clamp(@NotNull final Dimension value, @NotNull final Dimension min, @NotNull final Dimension max) {
		return Dimension.create(MathHelper.clamp(value.getWidth(), min.getWidth(), max.getWidth()), MathHelper.clamp(value.getHeight(), min.getHeight(), max.getHeight()));
	}

	@NotNull
	public static Rectangle clamp(@NotNull final Rectangle value, @NotNull final Rectangle min, @NotNull final Rectangle max) {
		return Rectangle.create(MathHelper.clamp(value.getPosition(), min.getPosition(), max.getPosition()), MathHelper.clamp(value.getSize(), min.getSize(), max.getSize()));
	}
}