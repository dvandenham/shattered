package shattered.lib.json;

import java.io.IOException;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
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

		@Override
		public void write(final JsonWriter writer, final Point value) throws IOException {
			writer.beginObject();
			writer.name("x").value(value.getX());
			writer.name("y").value(value.getY());
			writer.endObject();
		}

		@Override
		public Point read(final JsonReader reader) throws IOException {
			if (reader.peek() != JsonToken.BEGIN_OBJECT) {
				return null;
			}
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
						return null;
				}
			}
			reader.endObject();
			if (x == null || y == null) {
				return null;
			}
			return Point.create(x, y);
		}
	}

	private static class DimensionTypeAdapter extends TypeAdapter<Dimension> {

		@Override
		public void write(final JsonWriter writer, final Dimension value) throws IOException {
			writer.beginObject();
			writer.name("width").value(value.getWidth());
			writer.name("height").value(value.getHeight());
			writer.endObject();
		}

		@Override
		public Dimension read(final JsonReader reader) throws IOException {
			if (reader.peek() != JsonToken.BEGIN_OBJECT) {
				return null;
			}
			reader.beginObject();
			Integer width  = null;
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
						return null;
				}
			}
			reader.endObject();
			if (width == null || height == null) {
				return null;
			}
			return Dimension.create(width, height);
		}
	}

	private static class RectangleTypeAdapter extends TypeAdapter<Rectangle> {

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
			if (reader.peek() != JsonToken.BEGIN_OBJECT) {
				return null;
			}
			reader.beginObject();
			Integer x      = null;
			Integer y      = null;
			Integer width  = null;
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
						return null;
				}
			}
			reader.endObject();
			if (x == null || y == null || width == null || height == null) {
				return null;
			}
			return Rectangle.create(x, y, width, height);
		}
	}
}