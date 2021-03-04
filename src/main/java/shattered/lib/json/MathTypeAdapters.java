package shattered.lib.json;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

final class MathTypeAdapters {

	private MathTypeAdapters() {
	}

	public static void register(final GsonBuilder builder) {
		builder.registerTypeAdapter(Point.class, new PointTypeAdapter());
		builder.registerTypeAdapter(Dimension.class, new DimensionTypeAdapter());
		builder.registerTypeAdapter(Rectangle.class, new RectangleTypeAdapter());
	}

	private static class PointTypeAdapter extends TypeAdapter<Point> {

		private static final Pattern STRING_PATTERN = Pattern.compile("^(\\d+):(\\d+)$");

		@Override
		public void write(final JsonWriter writer, final Point value) throws IOException {
			writer.beginObject();
			writer.name("x").value(value.getX());
			writer.name("y").value(value.getY());
			writer.endObject();
		}

		@Override
		public Point read(final JsonReader reader) throws IOException {
			switch (reader.peek()) {
				case STRING: {
					final String data = reader.nextString();
					final Matcher matcher = PointTypeAdapter.STRING_PATTERN.matcher(data);
					if (!matcher.matches()) {
						throw new JsonSyntaxException("Invalid dimension format! Correct format: <x>x<y>");
					}
					final String x = matcher.group(1);
					final String y = matcher.group(2);
					return Point.create(Integer.parseInt(x), Integer.parseInt(y));
				}
				case BEGIN_OBJECT: {
					reader.beginObject();
					Integer x = null;
					Integer y = null;
					while (reader.hasNext()) {
						final String name = reader.nextName();
						switch (name) {
							case "x":
								x = reader.nextInt();
								break;
							case "y":
								y = reader.nextInt();
								break;
							default:
								throw new JsonSyntaxException("Invalid dimension object key: " + name + ", expected x or y");
						}
					}
					reader.endObject();
					if (x == null || y == null) {
						throw new JsonSyntaxException("Incomplete dimension format! Missing: " + (x == null ? "x" : "y"));
					}
					return Point.create(x, y);
				}
				default:
					throw new JsonSyntaxException("Expected STRING/BEGIN_OBJECT, got " + reader.peek());
			}
		}
	}

	private static class DimensionTypeAdapter extends TypeAdapter<Dimension> {

		private static final Pattern STRING_PATTERN = Pattern.compile("^(\\d+)x(\\d+)$");

		@Override
		public void write(final JsonWriter writer, final Dimension value) throws IOException {
			writer.beginObject();
			writer.name("width").value(value.getWidth());
			writer.name("height").value(value.getHeight());
			writer.endObject();
		}

		@Override
		public Dimension read(final JsonReader reader) throws IOException {
			switch (reader.peek()) {
				case STRING: {
					final String data = reader.nextString();
					final Matcher matcher = DimensionTypeAdapter.STRING_PATTERN.matcher(data);
					if (!matcher.matches()) {
						throw new JsonSyntaxException("Invalid dimension format! Correct format: <width>x<height>");
					}
					final String width = matcher.group(1);
					final String height = matcher.group(2);
					return Dimension.create(Integer.parseInt(width), Integer.parseInt(height));
				}
				case BEGIN_OBJECT: {
					reader.beginObject();
					Integer width = null;
					Integer height = null;
					while (reader.hasNext()) {
						final String name = reader.nextName();
						switch (name) {
							case "width":
							case "w":
								width = reader.nextInt();
								break;
							case "height":
							case "h":
								height = reader.nextInt();
								break;
							default:
								throw new JsonSyntaxException("Invalid dimension object key: " + name + ", expected width, w, height or h");
						}
					}
					reader.endObject();
					if (width == null || height == null) {
						throw new JsonSyntaxException("Incomplete dimension format! Missing: " + (width == null ? "width" : "height"));
					}
					return Dimension.create(width, height);
				}
				default:
					throw new JsonSyntaxException("Expected STRING/BEGIN_OBJECT, got " + reader.peek());
			}
		}
	}

	private static class RectangleTypeAdapter extends TypeAdapter<Rectangle> {

		private static final Pattern STRING_PATTERN = Pattern.compile("^(\\d+)x(\\d+)x(\\d+)x(\\d+)$");

		@Override
		public void write(final JsonWriter writer, final Rectangle value) throws IOException {
			writer.beginObject();
			writer.name("x").value(value.getX());
			writer.name("y").value(value.getY());
			writer.name("width").value(value.getWidth());
			writer.name("height").value(value.getHeight());
			writer.endObject();
		}

		@Override
		public Rectangle read(final JsonReader reader) throws IOException {
			switch (reader.peek()) {
				case STRING: {
					final String data = reader.nextString();
					final Matcher matcher = RectangleTypeAdapter.STRING_PATTERN.matcher(data);
					if (!matcher.matches()) {
						throw new JsonSyntaxException("Invalid rectangle format! Correct format: <x>x<y>x<width>x<height>");
					}
					final String x = matcher.group(1);
					final String y = matcher.group(2);
					final String width = matcher.group(3);
					final String height = matcher.group(4);
					return Rectangle.create(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(width), Integer.parseInt(height));
				}
				case BEGIN_OBJECT: {
					reader.beginObject();
					Integer x = null;
					Integer y = null;
					Integer width = null;
					Integer height = null;
					while (reader.hasNext()) {
						final String name = reader.nextName();
						switch (name) {
							case "x":
								x = reader.nextInt();
								break;
							case "y":
								y = reader.nextInt();
								break;
							case "width":
							case "w":
								width = reader.nextInt();
								break;
							case "height":
							case "h":
								height = reader.nextInt();
								break;
							default:
								throw new JsonSyntaxException("Invalid rectangle object key: " + name + ", expected x, y, width, w, height or h");
						}
					}
					reader.endObject();
					if (x == null || y == null || width == null || height == null) {
						throw new JsonSyntaxException(
								"Incomplete dimension format! Missing:" +
										(x == null ? " x" : "") +
										(y == null ? " y" : "") +
										(width == null ? "width" : "height")
						);
					}
					return Rectangle.create(x, y, width, height);
				}
				default:
					throw new JsonSyntaxException("Expected STRING/BEGIN_OBJECT, got " + reader.peek());
			}
		}
	}
}