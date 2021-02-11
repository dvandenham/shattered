package shattered.lib.gfx;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.Color;
import shattered.lib.Localizer;
import shattered.lib.ResourceLocation;
import shattered.lib.asset.AssetRegistry;
import shattered.lib.asset.Font;
import shattered.lib.asset.FontGroup;
import shattered.lib.asset.IAsset;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

@SuppressWarnings("unused")
public final class FontRendererImpl implements FontRenderer {

	private final ConcurrentLinkedQueue<WriteCall> queue = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedDeque<Integer> fontSizeStack = new ConcurrentLinkedDeque<>();
	private final Tessellator tessellator;
	private final FontGroup fontDefault;
	private FontGroup fontCurrent;
	private boolean writing = false;

	private FontRendererImpl(@NotNull final Tessellator tessellator, @NotNull final FontGroup fontDefault) {
		this.tessellator = tessellator;
		this.fontDefault = fontDefault;
		this.fontSizeStack.offerLast(FontGroup.DEFAULT_SIZES[FontGroup.DEFAULT_SIZE_INDEX]);
	}

	@Override
	public void setFont(@NotNull final ResourceLocation font) {
		final IAsset asset = AssetRegistry.getAsset(font);
		this.fontCurrent = asset instanceof FontGroup ? (FontGroup) asset : this.fontDefault;
	}

	@Override
	public void resetFont() {
		this.fontCurrent = null;
	}

	@Override
	public void setFontSize(final int size) {
		this.fontSizeStack.offerLast(size);
	}

	@Override
	public void revertFontSize() {
		if (this.fontSizeStack.size() > 1) {
			this.fontSizeStack.pollLast();
		}
	}

	@Override
	public void start() {
		if (this.isWriting()) {
			throw new IllegalStateException("Already writing!");
		}
		this.writing = true;
	}

	@Override
	public void add(@NotNull final Point position, @NotNull final StringData data) {
		if (!this.isWriting()) {
			throw new IllegalStateException("Not writing!");
		}
		if (data.getText().trim().isEmpty()) {
			return;
		}
		this.queue.offer(new WriteCall(position.getX(), position.getY(), data));
	}

	@Override
	public void add(final int x, final int y, @NotNull final StringData data) {
		this.add(Point.create(x, y), data);
	}

	@Override
	public void add(@NotNull final Point position, @NotNull final String text, @NotNull final Color color) {
		this.add(position, new StringData(text, color));
	}

	@Override
	public void add(final int x, final int y, @NotNull final String text, @NotNull final Color color) {
		this.add(Point.create(x, y), new StringData(text, color));
	}

	@Override
	public void add(@NotNull final Point position, @NotNull final String text) {
		this.add(position, new StringData(text));
	}

	@Override
	public void add(final int x, final int y, @NotNull final String text) {
		this.add(Point.create(x, y), new StringData(text));
	}

	@Override
	public void addCentered(@NotNull final Rectangle bounds, @NotNull final StringData data) {
		this.add(bounds.getPosition(), data.center(bounds.getSize()));
	}

	@Override
	public void addCentered(@NotNull final Point position, @NotNull final Dimension centerSize, @NotNull final StringData data) {
		this.addCentered(Rectangle.create(position, centerSize), data);
	}

	@Override
	public void addCentered(final int x, final int y, @NotNull final Dimension centerSize, @NotNull final StringData data) {
		this.addCentered(Point.create(x, y), centerSize, data);
	}

	@Override
	public void addCentered(@NotNull final Point position, final int centerWidth, final int centerHeight, @NotNull final StringData data) {
		this.addCentered(position, Dimension.create(centerWidth, centerHeight), data);
	}

	@Override
	public void addCentered(final int x, final int y, final int centerWidth, final int centerHeight, @NotNull final StringData data) {
		this.addCentered(Point.create(x, y), Dimension.create(centerWidth, centerHeight), data);
	}

	@Override
	public void addCentered(@NotNull final Rectangle Bounds, @NotNull final String text, @NotNull final Color color) {
		this.addCentered(Bounds, new StringData(text, color));
	}

	@Override
	public void addCentered(@NotNull final Point position, @NotNull final Dimension centerSize, @NotNull final String text, @NotNull final Color color) {
		this.addCentered(Rectangle.create(position, centerSize), text, color);
	}

	@Override
	public void addCentered(final int x, final int y, @NotNull final Dimension centerSize, @NotNull final String text, @NotNull final Color color) {
		this.addCentered(Point.create(x, y), centerSize, text, color);
	}

	@Override
	public void addCentered(@NotNull final Point position, final int centerWidth, final int centerHeight, @NotNull final String text, @NotNull final Color color) {
		this.addCentered(position, Dimension.create(centerWidth, centerHeight), text, color);
	}

	@Override
	public void addCentered(final int x, final int y, final int centerWidth, final int centerHeight, @NotNull final String text, @NotNull final Color color) {
		this.addCentered(Point.create(x, y), Dimension.create(centerWidth, centerHeight), text, color);
	}

	@Override
	public void addCentered(@NotNull final Rectangle bounds, @NotNull final String text) {
		this.addCentered(bounds, new StringData(text));
	}

	@Override
	public void addCentered(@NotNull final Point position, @NotNull final Dimension centerSize, @NotNull final String text) {
		this.addCentered(Rectangle.create(position, centerSize), text);
	}

	@Override
	public void addCentered(final int x, final int y, @NotNull final Dimension centerSize, @NotNull final String text) {
		this.addCentered(Point.create(x, y), centerSize, text);
	}

	@Override
	public void addCentered(@NotNull final Point position, final int centerWidth, final int centerHeight, @NotNull final String text) {
		this.addCentered(position, Dimension.create(centerWidth, centerHeight), text);
	}

	@Override
	public void addCentered(final int x, final int y, final int centerWidth, final int centerHeight, @NotNull final String text) {
		this.addCentered(Rectangle.createMutable(x, y, centerWidth, centerHeight), text);
	}

	@Override
	public void write() {
		if (!this.isWriting()) {
			throw new IllegalStateException("Not writing!");
		}
		while (!this.queue.isEmpty()) {
			//Retrieve current write call
			final WriteCall call = this.queue.poll();
			//Setup stuff
			int x = call.x;
			int y = call.y;
			final StringData data = call.data;
			//Store processed and current text
			final StringBuilder textDone = new StringBuilder();
			String text = data.getText();
			if (data.doLocalize()) {
				text = Localizer.Localize(text);
			}
			final String textFinal = text;
			if (data.doWrapText()) {
				text = this.getWrappedTextInternal(text, data.getWrapText());
			}
			if (text == null) {
				text = textFinal;
			}
			if (data.doCenterX()) {
				final int stringWidth = this.getWidthInternal(text);
				x = call.x + (data.getCenterX() - stringWidth) / 2;
			}
			if (data.doCenterY()) {
				final int stringHeight;
				if (data.doWrapText() && !data.doWrapTextStop()) {
					stringHeight = this.getHeightInternal(this.getTextRowsInternal(text, data.getWrapText()));
				} else {
					stringHeight = this.getHeightInternal(1);
				}
				y += (data.getCenterY() - stringHeight) / 2;
			}
			while (!textDone.toString().equals(textFinal)) {
				int totalWidth = 0;
				this.tessellator.start();
				final Font font = this.getFont();
				for (final char character : text.toCharArray()) {
					final Rectangle uv = font.getUv(character);
					if (uv != null) {
						final int charWidth = this.getWidthInternal(String.valueOf(character));
						assert !this.fontSizeStack.isEmpty();
						((TessellatorImpl) this.tessellator).set(x + totalWidth, y, charWidth, this.fontSizeStack.peekLast(), Objects.requireNonNull(font.getCharTexture(character)));
						this.tessellator.color(data.getColor());
						this.tessellator.next();
						totalWidth += charWidth;
					}
				}
				this.tessellator.draw();
				if (data.doWrapTextStop()) {
					break;
				}
				textDone.append(text);
				y += this.getHeightInternal(1);
				text = this.getWrappedTextInternal(textFinal.substring(textDone.length()), data.getWrapText());
				if (text == null) {
					break;
				}
				if (data.doCenterX()) {
					final int stringWidth = this.getWidthInternal(text);
					x = call.x + (data.getCenterX() - stringWidth) / 2;
				}
			}
		}
		this.writing = false;
	}

	@Override
	public boolean isWriting() {
		return this.writing;
	}

	@Override
	public int getWidth(@NotNull final StringData data) {
		String text = data.getText();
		if (data.doLocalize()) {
			text = Localizer.Localize(text);
		}
		return this.getWidthInternal(text);
	}

	@Override
	public int getWidth(@NotNull final String text) {
		return this.getWidth(new StringData(text));
	}

	private int getWidthInternal(@NotNull final String text) {
		assert !this.fontSizeStack.isEmpty();
		final float fontSizeRatio = (float) this.fontSizeStack.peekLast() / (float) this.getFont().getSize();
		float totalWidth = 0f;
		for (final char character : text.toCharArray()) {
			final Rectangle uv = this.getFont().getUv(character);
			if (uv != null) {
				totalWidth += uv.getWidth() * fontSizeRatio;
			}
		}
		return (int) Math.ceil(totalWidth);
	}

	@Override
	public int getHeight(@NotNull final StringData data) {
		if (data.doWrapText() && !data.doWrapTextStop()) {
			String text = data.getText();
			if (data.doLocalize()) {
				text = Localizer.Localize(text);
			}
			final int rows = this.getTextRowsInternal(text, data.getWrapText());
			return this.getHeightInternal(rows);
		}
		return this.getHeightInternal(1);
	}

	@Override
	public int getHeight() {
		return this.getHeightInternal(1);
	}

	private int getHeightInternal(final int rows) {
		assert !this.fontSizeStack.isEmpty();
		return this.fontSizeStack.peekLast() * rows;
	}

	@Override
	public int getTextRows(@NotNull final StringData data) {
		if (!data.doWrapText() || data.doWrapTextStop()) {
			return 1;
		}
		String text = data.getText();
		if (data.doLocalize()) {
			text = Localizer.Localize(text);
		}
		return this.getTextRowsInternal(text, data.getWrapText());
	}

	private int getTextRowsInternal(@NotNull final String text, final int maxWidth) {
		if (text.trim().isEmpty()) {
			return 0;
		}
		final ArrayList<String> data = new ArrayList<>();
		String lineNew = text;
		while (!lineNew.isEmpty()) {
			final String lineWrapped = this.getWrappedTextInternal(lineNew, maxWidth);
			if (lineWrapped == null) {
				continue;
			}
			data.add(lineWrapped);
			lineNew = lineNew.substring(lineWrapped.length());
		}
		return data.size();
	}

	@Override
	@Nullable
	public String getWrappedText(@NotNull final StringData data) {
		String text = data.getText();
		if (data.doLocalize()) {
			text = Localizer.Localize(text);
		}
		if (!data.doWrapText()) {
			return text;
		}
		return this.getWrappedTextInternal(text, data.getWrapText());
	}

	@Nullable
	private String getWrappedTextInternal(@NotNull final String text, final int maxWidth) {
		if (text.trim().isEmpty()) {
			return null;
		}
		String result = "";
		if (text.contains(" ")) {
			final String[] words = text.split(" ");
			for (int i = 0; i < words.length; ++i) {
				final StringBuilder builder = new StringBuilder(result);
				if (i > 0) {
					builder.append(' ');
				}
				builder.append(words[i]);
				final String resultTest = builder.toString();
				if (this.getWidthInternal(resultTest) < maxWidth) {
					result = resultTest;
				} else {
					return result.isEmpty() ? text : result;
				}
			}
		} else {
			for (int i = 0; i < text.length(); ++i) {
				final String resultTest = result + text.charAt(i);
				if (this.getWidthInternal(resultTest) < maxWidth) {
					result = resultTest;
				} else {
					return result.isEmpty() ? text : result;
				}
			}
		}
		return text;
	}

	@NotNull
	private Font getFont() {
		FontGroup current = this.fontCurrent;
		if (current == null) {
			current = this.fontDefault;
		}
		assert !this.fontSizeStack.isEmpty();
		return current.getFont(this.fontSizeStack.peekLast());
	}
}