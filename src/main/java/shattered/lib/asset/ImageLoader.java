package shattered.lib.asset;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import org.jetbrains.annotations.NotNull;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

@EventBusSubscriber("SYSTEM")
final class ImageLoader {

	private static final int PIXEL_CHANNELS_RGB = 3;
	private static final int PIXEL_CHANNELS_RGBA = 4;
	private static final ComponentColorModel COLOR_MODEL_ALPHA = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8}, true, false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
	private static final ComponentColorModel COLOR_MODEL_DEFAULT = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 0}, false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);

	private ImageLoader() {
	}

	static int[] loadTexture(@NotNull final BufferedImage image) {
		final int textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		final Object[] data = ImageLoader.loadImage(image);
		final int pixelBytes = (int) data[3];
		glTexImage2D(GL_TEXTURE_2D, 0, pixelBytes == ImageLoader.PIXEL_CHANNELS_RGBA ? GL_RGBA : GL_RGB, image.getWidth(), image.getHeight(), 0, pixelBytes == ImageLoader.PIXEL_CHANNELS_RGBA ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) data[2]);
		return new int[]{textureId, image.getWidth(), image.getHeight()};
	}

	@NotNull
	private static Object[] loadImage(@NotNull BufferedImage image) {
		image = ImageLoader.convertImage(image);
		final int pixelBytes = ImageLoader.getPixelBytes(image);
		final byte[] data = (byte[]) image.getRaster().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
		final ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * pixelBytes);
		//noinspection RedundantCast
		((Buffer) buffer.put(data)).flip();
		return new Object[]{image.getWidth(), image.getHeight(), buffer, pixelBytes};
	}

	@NotNull
	private static BufferedImage convertImage(@NotNull final BufferedImage image) {
		if (image.getColorModel() == ImageLoader.COLOR_MODEL_ALPHA || image.getColorModel() == ImageLoader.COLOR_MODEL_DEFAULT) {
			return image;
		}
		final int pixelBytes = ImageLoader.getPixelBytes(image);
		final WritableRaster pixels = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, image.getWidth(), image.getHeight(), pixelBytes, null);
		final BufferedImage newImage = new BufferedImage(pixelBytes == ImageLoader.PIXEL_CHANNELS_RGBA ? ImageLoader.COLOR_MODEL_ALPHA : ImageLoader.COLOR_MODEL_DEFAULT, pixels, false, new Hashtable<String, Object>());
		final Graphics2D graphics = newImage.createGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		return newImage;
	}

	private static int getPixelBytes(@NotNull final BufferedImage image) {
		return image.getRaster().getNumBands() == 3 ? ImageLoader.PIXEL_CHANNELS_RGB : ImageLoader.PIXEL_CHANNELS_RGBA;
	}

	@MessageListener("glfw_create_texture")
	private static void onGlfwCreateTexture(final MessageEvent event) {
		final Object[] data = event.getData();
		if (data.length == 0 || !(data[0] instanceof BufferedImage)) {
			return;
		}
		event.setResponse(() -> ImageLoader.loadImage((BufferedImage) data[0]));
	}

	@MessageListener("glfw_create_gl_texture")
	private static void onGlfwCreateGlTexture(final MessageEvent event) {
		final Object[] data = event.getData();
		if (data.length == 0 || !(data[0] instanceof ResourceLocation) || !(data[1] instanceof BufferedImage)) {
			return;
		}
		final BufferedImage image = (BufferedImage) data[1];
		final int[] textureData = ImageLoader.loadTexture(image);
		final Dimension size = Dimension.create(image.getWidth(), image.getHeight());
		final TextureSimple result = new TextureSimple(
				(ResourceLocation) data[0],
				textureData[0],
				size,
				Dimension.create(textureData[1], textureData[2]),
				Rectangle.create(0, 0, size)
		);
		event.setResponse(() -> result);
	}
}