package shattered.lib;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.NotNull;
import shattered.lib.math.MathHelper;

@SuppressWarnings("unused")
public final class Color {

	private static final Int2ObjectMap<Color> CACHE = new Int2ObjectArrayMap<>();

	private static final float COLOR_MULTIPLIER = 1f / 255f;
	private static final float COLOR_SHIFT_MULTIPLIER = 0.7f;

	public static final Color TRANSPARENT = Color.get(0, 0, 0, 0);
	public static final Color WHITE = Color.get(255, 255, 255);
	public static final Color LIGHT_GRAY = Color.get(192, 192, 192);
	public static final Color GRAY = Color.get(128, 128, 128);
	public static final Color DARK_GRAY = Color.get(64, 64, 64);
	public static final Color BLACK = Color.get(0, 0, 0);
	public static final Color RED = Color.get(255, 0, 0);
	public static final Color PINK = Color.get(255, 175, 175);
	public static final Color ORANGE = Color.get(255, 200, 0);
	public static final Color YELLOW = Color.get(255, 255, 0);
	public static final Color GREEN = Color.get(0, 255, 0);
	public static final Color MAGENTA = Color.get(255, 0, 255);
	public static final Color CYAN = Color.get(0, 255, 255);
	public static final Color BLUE = Color.get(0, 0, 255);
	public static final Color XEROS = Color.get(50, 204, 220);

	private final float red, green, blue, alpha;

	private Color(final float red, final float green, final float blue, final float alpha) {
		this.red = MathHelper.clamp(red, 0f, 1f);
		this.green = MathHelper.clamp(green, 0f, 1f);
		this.blue = MathHelper.clamp(blue, 0f, 1f);
		this.alpha = MathHelper.clamp(alpha, 0f, 1f);
	}

	private Color(final int red, final int green, final int blue, final int alpha) {
		this(Color.COLOR_MULTIPLIER * red, Color.COLOR_MULTIPLIER * green, Color.COLOR_MULTIPLIER * blue, Color.COLOR_MULTIPLIER * alpha);
	}

	public float getRed() {
		return this.red;
	}

	public float getGreen() {
		return this.green;
	}

	public float getBlue() {
		return this.blue;
	}

	public float getAlpha() {
		return this.alpha;
	}

	public float[] getFloats() {
		return new float[]{this.red, this.green, this.blue, this.alpha};
	}

	public int getRedByte() {
		return (int) (this.red * 255f);
	}

	public int getGreenByte() {
		return (int) (this.green * 255f);
	}

	public int getBlueByte() {
		return (int) (this.blue * 255f);
	}

	public int getAlphaByte() {
		return (int) (this.alpha * 255f);
	}

	public int getRGBA() {
		return (this.getAlphaByte() & 255) << 24 + (this.getRedByte() & 255) << 16 + (this.getGreenByte() & 255) << 8 + (this.getBlueByte() & 255);
	}

	@NotNull
	public Color brighter() {
		int byteRed = this.getRedByte();
		int byteGreen = this.getGreenByte();
		int byteBlue = this.getBlueByte();
		final int byteAlpha = this.getAlphaByte();
		final int multiplier = (int) (1.0 / (1.0 - Color.COLOR_SHIFT_MULTIPLIER));
		if (byteRed == 0 && byteGreen == 0 && byteBlue == 0) {
			return Color.get(multiplier, multiplier, multiplier, byteAlpha);
		}
		if (byteRed > 0 && byteRed < multiplier) {
			byteRed = multiplier;
		}
		if (byteGreen > 0 && byteGreen < multiplier) {
			byteGreen = multiplier;
		}
		if (byteBlue > 0 && byteBlue < multiplier) {
			byteBlue = multiplier;
		}
		return Color.get(Math.min((int) (byteRed / Color.COLOR_SHIFT_MULTIPLIER), 255), Math.min((int) (byteGreen / Color.COLOR_SHIFT_MULTIPLIER), 255), Math.min((int) (byteBlue / Color.COLOR_SHIFT_MULTIPLIER), 255), byteAlpha);
	}

	@NotNull
	public Color darker() {
		return Color.get(Math.max((int) (this.getRedByte() * Color.COLOR_SHIFT_MULTIPLIER), 0), Math.max((int) (this.getGreenByte() * Color.COLOR_SHIFT_MULTIPLIER), 0), Math.max((int) (this.getBlueByte() * Color.COLOR_SHIFT_MULTIPLIER), 0), this.getAlphaByte());
	}

	@NotNull
	public Color withRed(final int red) {
		return Color.get(red * Color.COLOR_MULTIPLIER, this.green, this.blue, this.alpha);
	}

	@NotNull
	public Color withGreen(final int green) {
		return Color.get(this.red, green * Color.COLOR_MULTIPLIER, this.blue, this.alpha);
	}

	@NotNull
	public Color withBlue(final int blue) {
		return Color.get(this.red, this.green, blue * Color.COLOR_MULTIPLIER, this.alpha);
	}

	@NotNull
	public Color withAlpha(final int alpha) {
		return Color.get(this.red, this.green, this.blue, alpha * Color.COLOR_MULTIPLIER);
	}

	@NotNull
	public Color withRGB(final int red, final int green, final int blue) {
		return Color.get(red * Color.COLOR_MULTIPLIER, green * Color.COLOR_MULTIPLIER, blue * Color.COLOR_MULTIPLIER, this.alpha);
	}

	@NotNull
	public Color withRed(final float red) {
		return Color.get(red, this.green, this.blue, this.alpha);
	}

	@NotNull
	public Color withGreen(final float green) {
		return Color.get(this.red, green, this.blue, this.alpha);
	}

	@NotNull
	public Color withBlue(final float blue) {
		return Color.get(this.red, this.green, blue, this.alpha);
	}

	@NotNull
	public Color withAlpha(final float alpha) {
		return Color.get(this.red, this.green, this.blue, alpha);
	}

	@NotNull
	public Color withRGB(final float red, final float green, final float blue) {
		return Color.get(red, green, blue, this.alpha);
	}

	@NotNull
	public static Color get(final int red, final int green, final int blue, final int alpha) {
		final int rgba = ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
		Color result = Color.CACHE.get(rgba);
		if (result == null) {
			result = new Color(red, green, blue, alpha);
			Color.CACHE.put(rgba, result);
		}
		return result;
	}

	@NotNull
	public static Color get(final int red, final int green, final int blue) {
		return Color.get(red, green, blue, 255);
	}

	@NotNull
	public static Color get(final float red, final float green, final float blue, final float alpha) {
		final int rgba = (((int) (alpha * 255f) & 0xFF) << 24) | (((int) (red * 255f) & 0xFF) << 16) | (((int) (green * 255f) & 0xFF) << 8) | ((int) (blue * 255f) & 0xFF);
		Color result = Color.CACHE.get(rgba);
		if (result == null) {
			result = new Color(red, green, blue, alpha);
			Color.CACHE.put(rgba, result);
		}
		return result;
	}

	@NotNull
	public static Color get(final float red, final float green, final float blue) {
		return Color.get(red, green, blue, 1f);
	}
}