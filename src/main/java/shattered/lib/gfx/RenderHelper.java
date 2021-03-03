package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import shattered.lib.Color;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

public final class RenderHelper {

	private RenderHelper() {
	}

	public static void writeGlitched(@NotNull final FontRenderer fontRenderer, @NotNull final Point position, @NotNull final StringData data) {
		final Point immutablePos = position.toImmutable();
		fontRenderer.start();
		fontRenderer.add(immutablePos.moveX(-4), data.withColor(Color.RED));
		fontRenderer.add(immutablePos.moveX(4), data.withColor(Color.XEROS));
		fontRenderer.add(immutablePos, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitched(@NotNull final FontRenderer fontRenderer, final int x, final int y, @NotNull final StringData data) {
		fontRenderer.start();
		fontRenderer.add(x - 4, y, data.withColor(Color.RED));
		fontRenderer.add(x + 4, y, data.withColor(Color.XEROS));
		fontRenderer.add(x, y, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitched(@NotNull final FontRenderer fontRenderer, @NotNull final Point position, @NotNull final String text) {
		final Point immutablePos = position.toImmutable();
		fontRenderer.start();
		fontRenderer.add(immutablePos.moveX(-4), text, Color.RED);
		fontRenderer.add(immutablePos.moveX(4), text, Color.XEROS);
		fontRenderer.add(immutablePos, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void writeGlitched(@NotNull final FontRenderer fontRenderer, final int x, final int y, @NotNull final String text) {
		fontRenderer.start();
		fontRenderer.add(x - 4, y, text, Color.RED);
		fontRenderer.add(x + 4, y, text, Color.XEROS);
		fontRenderer.add(x, y, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, @NotNull final Rectangle bounds, @NotNull final StringData data) {
		final Rectangle immutableBounds = bounds.toImmutable();
		fontRenderer.start();
		fontRenderer.addCentered(immutableBounds.moveX(-4), data.withColor(Color.RED));
		fontRenderer.addCentered(immutableBounds.moveX(4), data.withColor(Color.XEROS));
		fontRenderer.addCentered(immutableBounds, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, @NotNull final Point position, @NotNull final Dimension centerSize, @NotNull final StringData data) {
		final Point immutablePos = position.toImmutable();
		fontRenderer.start();
		fontRenderer.addCentered(immutablePos.moveX(-4), centerSize, data.withColor(Color.RED));
		fontRenderer.addCentered(immutablePos.moveX(4), centerSize, data.withColor(Color.XEROS));
		fontRenderer.addCentered(immutablePos, centerSize, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, final int x, final int y, @NotNull final Dimension centerSize, @NotNull final StringData data) {
		fontRenderer.start();
		fontRenderer.addCentered(x - 4, y, centerSize, data.withColor(Color.RED));
		fontRenderer.addCentered(x + 4, y, centerSize, data.withColor(Color.XEROS));
		fontRenderer.addCentered(x, y, centerSize, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, @NotNull final Point position, final int centerWidth, final int centerHeight, @NotNull final StringData data) {
		final Point immutablePos = position.toImmutable();
		fontRenderer.start();
		fontRenderer.addCentered(immutablePos.moveX(-4), centerWidth, centerHeight, data.withColor(Color.RED));
		fontRenderer.addCentered(immutablePos.moveX(4), centerWidth, centerHeight, data.withColor(Color.XEROS));
		fontRenderer.addCentered(immutablePos, centerWidth, centerHeight, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, final int x, final int y, final int centerWidth, final int centerHeight, @NotNull final StringData data) {
		fontRenderer.start();
		fontRenderer.addCentered(x - 4, y, centerWidth, centerHeight, data.withColor(Color.RED));
		fontRenderer.addCentered(x + 4, y, centerWidth, centerHeight, data.withColor(Color.XEROS));
		fontRenderer.addCentered(x, y, centerWidth, centerHeight, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, @NotNull final Rectangle bounds, @NotNull final String text) {
		final Rectangle immutableBounds = bounds.toImmutable();
		fontRenderer.start();
		fontRenderer.addCentered(immutableBounds.moveX(-4), text, Color.RED);
		fontRenderer.addCentered(immutableBounds.moveX(4), text, Color.XEROS);
		fontRenderer.addCentered(immutableBounds, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, @NotNull final Point position, @NotNull final Dimension centerSize, @NotNull final String text) {
		final Point immutablePos = position.toImmutable();
		fontRenderer.start();
		fontRenderer.addCentered(immutablePos.moveX(-4), centerSize, text, Color.RED);
		fontRenderer.addCentered(immutablePos.moveX(4), centerSize, text, Color.XEROS);
		fontRenderer.addCentered(immutablePos, centerSize, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, final int x, final int y, final Dimension centerSize, @NotNull final String text) {
		fontRenderer.start();
		fontRenderer.addCentered(x - 4, y, centerSize, text, Color.RED);
		fontRenderer.addCentered(x + 4, y, centerSize, text, Color.XEROS);
		fontRenderer.addCentered(x, y, centerSize, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, @NotNull final Point position, final int centerWidth, final int centerHeight, @NotNull final String text) {
		final Point immutablePos = position.toImmutable();
		fontRenderer.start();
		fontRenderer.addCentered(immutablePos.moveX(-4), centerWidth, centerHeight, text, Color.RED);
		fontRenderer.addCentered(immutablePos.moveX(4), centerWidth, centerHeight, text, Color.XEROS);
		fontRenderer.addCentered(immutablePos, centerWidth, centerHeight, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, final int x, final int y, final int centerWidth, final int centerHeight, @NotNull final String text) {
		fontRenderer.start();
		fontRenderer.addCentered(x - 4, y, centerWidth, centerHeight, text, Color.RED);
		fontRenderer.addCentered(x + 4, y, centerWidth, centerHeight, text, Color.XEROS);
		fontRenderer.addCentered(x, y, centerWidth, centerHeight, text, Color.WHITE);
		fontRenderer.write();
	}
}