package shattered.lib.asset;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Rectangle;

final class ImagePacker {

	private final HashMap<ResourceLocation, Rectangle> bounds = new HashMap<>();
	private final Node                                 rootNode;
	private final BufferedImage                        image;

	ImagePacker(final int width, final int height) {
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.rootNode = new Node(width, height);
	}

	void addImage(@NotNull final ResourceLocation resource, @NotNull final BufferedImage image) {
		if (this.bounds.containsKey(resource)) {
			throw new RuntimeException("Key with ResourceLocation '" + resource + "' is already in map");
		}
		Rectangle  bounds = Rectangle.createMutable(0, 0, image.getWidth(), image.getHeight());
		final Node node   = this.insert(this.rootNode, bounds);
		if (node == null) {
			throw new RuntimeException("Image didn't fit");
		}
		node.resource = resource;
		bounds = node.bounds.copy();
		this.bounds.put(resource, bounds);
		final Graphics2D graphics = this.image.createGraphics();
		graphics.drawImage(image, bounds.getX(), bounds.getY(), null);
		graphics.dispose();
	}

	@Nullable
	private Node insert(@NotNull final Node node, @NotNull final Rectangle bounds) {
		if (node.resource == null && node.childLeft != null && node.childRight != null) {
			Node result = this.insert(node.childLeft, bounds);
			if (result == null) {
				result = this.insert(node.childRight, bounds);
			}
			return result;
		} else {
			if (node.resource != null) {
				return null;
			}
			if (node.bounds.getSize().equals(bounds.getSize())) {
				return node;
			}
			if (node.bounds.getWidth() < bounds.getWidth() || node.bounds.getHeight() < bounds.getHeight()) {
				return null;
			}
			node.childLeft = new Node();
			node.childRight = new Node();
			if (node.bounds.getWidth() - bounds.getWidth() > node.bounds.getHeight() - bounds.getHeight()) {
				node.childLeft.bounds = Rectangle.createMutable(node.bounds.getX(), node.bounds.getY(), bounds.getWidth(), node.bounds.getHeight());
				node.childRight.bounds = Rectangle.createMutable(node.bounds.getX() + bounds.getWidth(), node.bounds.getY(), node.bounds.getWidth() - bounds.getWidth(), node.bounds.getHeight());
			} else {
				node.childLeft.bounds = Rectangle.createMutable(node.bounds.getX(), node.bounds.getY(), node.bounds.getWidth(), bounds.getHeight());
				node.childRight.bounds = Rectangle.createMutable(node.bounds.getX(), node.bounds.getY() + bounds.getHeight(), node.bounds.getWidth(), node.bounds.getHeight() - bounds.getHeight());
			}
			return this.insert(node.childLeft, bounds);
		}
	}

	@NotNull
	BufferedImage getImage() {
		return this.image;
	}

	@NotNull
	Map<ResourceLocation, Rectangle> getMapping() {
		return this.bounds;
	}

	private static final class Node {

		private Node childLeft = null, childRight = null;
		private Rectangle        bounds;
		private ResourceLocation resource = null;

		private Node(final int width, final int height) {
			this.bounds = Rectangle.createMutable(0, 0, width, height);
			this.childLeft = null;
			this.childRight = null;
			this.resource = null;
		}

		private Node() {
			this.bounds = Rectangle.createMutable(0, 0, 0, 0);
		}
	}
}