package shattered.lib.gfx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import shattered.Shattered;
import shattered.core.event.EventBus;
import shattered.core.event.EventListener;
import shattered.core.event.MessageEvent;
import shattered.lib.Color;
import shattered.lib.ResourceLocation;
import shattered.lib.asset.AssetRegistry;
import shattered.lib.asset.AtlasStitcher;
import shattered.lib.asset.IAsset;
import shattered.lib.asset.Texture;
import shattered.lib.asset.TextureAnimated;
import shattered.lib.asset.TextureAtlas;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

@SuppressWarnings("unused")
public final class TessellatorImpl implements Tessellator {

	public static Texture TEXTURE_MISSING;
	private final ConcurrentLinkedQueue<DrawCall> stack = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedDeque<Matrix4f> matrices = new ConcurrentLinkedDeque<>();
	private final PolygonBuilder polygonBuilder = new PolygonBuilderImpl(this);
	private DrawCall currentCall = null;
	private boolean drawing = false, caching = false;
	private Shader shader;
	private Matrix4f matUniform;

	private TessellatorImpl() {
		VertexArrayObject.get().bind();
		this.matrices.offer(MatrixUtils.identity());
		EventBus.register(this);
	}

	@EventListener
	private void onDisplayResized(final DisplayResizedEvent ignored) {
		this.shader.setUniformMat4("matProj", MatrixUtils.ortho());
	}

	public void startCaching() {
		this.caching = true;
	}

	public void stopCaching() {
		this.caching = false;
	}

	public void setShader(final Shader shader) {
		this.shader = shader;
	}

	public Shader getShader() {
		return this.shader;
	}

	public void reset() {
		this.stack.clear();
		this.currentCall = null;
		this.drawing = false;
	}

	public void dropMatrices() {
		this.matrices.clear();
		this.matrices.offer(MatrixUtils.identity());
	}

	@Override
	public void setUniformMatrix(@NotNull final Matrix4f matrix) {
		this.matUniform = matrix;
		this.shader.setUniformMat4("matCustom", matrix);
	}

	@Nullable
	Matrix4f getUniformMatrix() {
		return this.matUniform;
	}

	@Override
	public void resetUniformMatrix() {
		this.matUniform = null;
		this.shader.setUniformMat4("matCustom", MatrixUtils.identity());
	}

	@Override
	@NotNull
	public PolygonBuilder createPolygon() {
		return this.polygonBuilder;
	}

	@Override
	public void start() {
		if (this.isDrawing() && this.caching) {
			return;
		}
		if (this.isDrawing()) {
			throw new IllegalStateException("Already tessellating!");
		}
		this.reset();
		this.drawing = true;
	}

	@Override
	public void set(@NotNull final Point position, @NotNull final ResourceLocation resource) {
		final Texture texture = this.getTexture(resource);
		this.set(Rectangle.create(position, texture.getTextureSize()), resource);
	}

	@Override
	public void set(final int x, final int y, @NotNull final ResourceLocation resource) {
		final Texture Texture = this.getTexture(resource);
		this.set(Rectangle.create(x, y, Texture.getTextureSize()), resource);
	}

	@Override
	public void set(@NotNull final Rectangle bounds, @NotNull final ResourceLocation resource) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		final Texture Texture = this.getTexture(resource);
		this.currentCall = new DrawCall(bounds.toImmutable(), Texture);
	}

	//Internal method for the FontRenderer
	void set(final int x, final int y, final int width, final int height, final Texture texture) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		this.currentCall = new DrawCall(Rectangle.create(x, y, width, height), texture);
	}

	@Override
	public void set(@NotNull final Point position, @NotNull final Dimension size, @NotNull final ResourceLocation resource) {
		this.set(Rectangle.create(position, size), resource);
	}

	@Override
	public void set(@NotNull final Point position, final int width, final int height, @NotNull final ResourceLocation resource) {
		this.set(Rectangle.create(position, width, height), resource);
	}

	@Override
	public void set(final int x, final int y, @NotNull final Dimension size, @NotNull final ResourceLocation resource) {
		this.set(Rectangle.create(x, y, size), resource);
	}

	@Override
	public void set(final int x, final int y, final int width, final int height, @NotNull final ResourceLocation resource) {
		this.set(Rectangle.create(x, y, width, height), resource);
	}

	@Override
	public void set(@NotNull final Rectangle bounds, @NotNull final Color color) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		this.currentCall = new DrawCall(bounds.toImmutable(), color);
	}

	@Override
	public void set(@NotNull final Point position, @NotNull final Dimension size, @NotNull final Color color) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		this.currentCall = new DrawCall(Rectangle.create(position, size), color);
	}

	@Override
	public void set(@NotNull final Point position, final int width, final int height, @NotNull final Color color) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		this.currentCall = new DrawCall(Rectangle.create(position, width, height), color);
	}

	@Override
	public void set(final int x, final int y, @NotNull final Dimension size, @NotNull final Color color) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		this.currentCall = new DrawCall(Rectangle.create(x, y, size), color);
	}

	@Override
	public void set(final int x, final int y, final int width, final int height, @NotNull final Color color) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		this.currentCall = new DrawCall(Rectangle.create(x, y, width, height), color);
	}

	@Override
	public void setLocation(@NotNull final Point position) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.bounds = this.currentCall.bounds.setPosition(position);
	}

	@Override
	public void setLocation(final int x, final int y) {
		this.setLocation(Point.create(x, y));
	}

	@Override
	public void move(@NotNull final Dimension amount) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.bounds = this.currentCall.bounds.move(amount);
	}

	@Override
	public void move(final int amountX, final int amountY) {
		this.move(Dimension.create(amountX, amountY));
	}

	@Override
	public void scale(@NotNull final Vector2f scale) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		final Rectangle bounds = this.currentCall.bounds.toMutable();
		this.currentCall.bounds = bounds.setSize((int) (bounds.getWidth() * scale.x), (int) (bounds.getHeight() * scale.y));
	}

	@Override
	public void scale(final float scaleX, final float scaleY) {
		this.scale(new Vector2f(scaleX, scaleY));
	}

	@Override
	public void centerX(final int width) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.bounds = this.currentCall.bounds.moveX((width - this.currentCall.bounds.getWidth()) / 2.0);
	}

	@Override
	public void centerY(final int height) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.bounds = this.currentCall.bounds.moveY((height - this.currentCall.bounds.getHeight()) / 2.0);
	}

	@Override
	public void center(@NotNull final Dimension size) {
		this.center(size.getWidth(), size.getHeight());
	}

	@Override
	public void center(final int width, final int height) {
		this.centerX(width);
		this.centerY(height);
	}

	@Override
	public void uMin(final int uMin) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.uMin = uMin;
	}

	@Override
	public void vMin(final int vMin) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.vMin = vMin;
	}

	@Override
	public void uMax(final int uMax) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.uMax = uMax;
	}

	@Override
	public void vMax(final int vMax) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.vMax = vMax;
	}

	@Override
	public void uv(@NotNull final Rectangle uv) {
		this.uMin(uv.getX());
		this.vMin(uv.getY());
		this.uMax(uv.getMaxX());
		this.vMax(uv.getMaxY());
	}

	@Override
	public void translate(@NotNull final Point amount) {
		this.translate(amount.getX(), amount.getY());
	}

	@Override
	public void translate(final int x, final int y) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.action(DrawCall.CallType.TRANSLATE, x, y);
	}

	@Override
	public void translateToCenter(@NotNull final Dimension size) {
		this.translateToCenter(size.getWidth(), size.getHeight());
	}

	@Override
	public void translateToCenter(final int width, final int height) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.translate(width / 2, height / 2);
	}

	@Override
	public void mirrorX() {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.uMin += this.currentCall.uMax;
		this.currentCall.uMax = this.currentCall.uMin - this.currentCall.uMax;
	}

	@Override
	public void mirrorY() {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.vMin += this.currentCall.vMax;
		this.currentCall.vMax = this.currentCall.vMin - this.currentCall.vMax;
	}

	@Override
	public void mirror() {
		this.mirrorX();
		this.mirrorY();
	}

	@Override
	public void rotate(final float degrees) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.action(DrawCall.CallType.ROTATE, degrees);
	}

	@Override
	public void color(@NotNull final Color color) {
		this.colorTop(color);
		this.colorBottom(color);
	}

	@Override
	public void colorTop(@NotNull final Color color) {
		this.colorTopLeft(color);
		this.colorTopRight(color);
	}

	@Override
	public void colorBottom(@NotNull final Color color) {
		this.colorBottomLeft(color);
		this.colorBottomRight(color);
	}

	@Override
	public void colorLeft(@NotNull final Color color) {
		this.colorTopLeft(color);
		this.colorBottomLeft(color);
	}

	@Override
	public void colorRight(@NotNull final Color color) {
		this.colorTopRight(color);
		this.colorBottomRight(color);
	}

	@Override
	public void colorTopLeft(@NotNull final Color color) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.colors[0] = color;
	}

	@Override
	public void colorTopRight(@NotNull final Color color) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.colors[1] = color;
	}

	@Override
	public void colorBottomLeft(@NotNull final Color color) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.colors[3] = color;
	}

	@Override
	public void colorBottomRight(@NotNull final Color color) {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.colors[2] = color;
	}

	@Override
	public void pushMatrix() {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.action(DrawCall.CallType.MATRIX_PUSH);
	}

	@Override
	public void popMatrix() {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.action(DrawCall.CallType.MATRIX_POP);
	}

	@Override
	public void enableSmoothing() {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.action(DrawCall.CallType.SMOOTH, true);
	}

	@Override
	public void disableSmoothing() {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall == null) {
			throw new IllegalStateException("No call to modify! Use the set() method first!");
		}
		this.currentCall.action(DrawCall.CallType.SMOOTH, false);
	}

	@Override
	public void next() {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		if (this.currentCall != null) {
			this.stack.offer(this.currentCall);
			this.currentCall = null;
		}
	}

	public void drawCached() {
		if (!this.caching) {
			return;
		}
		try {
			this.caching = false;
			this.draw();
		} catch (final IllegalStateException ignored) {
		}
	}

	@Override
	public void draw() {
		if (!this.isDrawing()) {
			throw new IllegalStateException("Not Tessellating!");
		}
		this.next();
		if (this.caching) {
			return;
		}
		while (!this.stack.isEmpty()) {
			//Retrieve current draw call
			final DrawCall call = this.stack.poll();
			for (final Object[] data : call.data) {
				if (data == null) {
					break;
				}
				assert this.matrices.peekLast() != null;
				final Matrix4f matrix = this.matrices.peekLast();
				switch ((DrawCall.CallType) data[0]) {
					case MATRIX_PUSH: {
						this.matrices.offer(new Matrix4f(matrix));
						break;
					}
					case MATRIX_POP: {
						if (this.matrices.size() >= 2) {
							this.matrices.removeLast();
						}
						break;
					}
					case TRANSLATE: {
						this.matrices.offer(matrix.translate(new Vector3f((int) data[1], (int) data[2], 0f)));
						break;
					}
					case ROTATE: {
						this.matrices.offer(matrix.rotate((float) Math.toRadians((float) data[1]), new Vector3f(0f, 0f, 1f)));
						break;
					}
					case SMOOTH: {
						if ((boolean) data[1]) {
							GLHelper.enableSmoothing();
						} else {
							GLHelper.disableSmoothing();
						}
						break;
					}
				}
			}
			this.render(call, this.matrices.peekLast());
			this.dropMatrices();
		}
		this.drawing = false;
	}

	private void render(final DrawCall call, final Matrix4f matrix) {
		if (call.useTexture) {
			//Calculate all positions
			final float startX = call.bounds.getX();
			final float startY = call.bounds.getY();
			final float stopX;// = call.bounds.getWidth() < 0 ? -1 :
			final float stopY;// = call.bounds.getHeight() < 0 ? -1 : call.bounds.getMaxY();
			if (call.bounds.getWidth() > 0) {
				stopX = call.bounds.getMaxX();
			} else {
				if (call.bounds.getHeight() > 0) { //Keep aspect ratio
					final double ratio = call.bounds.getDoubleHeight() / call.texture.getTextureSize().getDoubleHeight();
					stopX = (float) (startX + (ratio * call.texture.getTextureSize().getDoubleWidth()));
				} else { //Ignore bounds
					stopX = startX + call.texture.getTextureSize().getWidth();
				}
			}
			if (call.bounds.getHeight() > 0) {
				stopY = call.bounds.getMaxY();
			} else {
				if (call.bounds.getWidth() > 0) { //Keep aspect ratio
					final double ratio = call.bounds.getDoubleWidth() / call.texture.getTextureSize().getDoubleWidth();
					stopY = (float) (startY + (ratio * call.texture.getTextureSize().getDoubleHeight()));
				} else { //Ignore bounds
					stopY = startY + call.texture.getTextureSize().getHeight();
				}
			}
			if (call.texture == TessellatorImpl.TEXTURE_MISSING) {
				call.uMin = 0;
				call.vMin = 0;
				call.uMax = call.texture.getImageSize().getWidth();
				call.vMax = call.texture.getImageSize().getHeight();
				Arrays.fill(call.colors, Color.WHITE);
				GLHelper.disableSmoothing();
			} else if (call.texture instanceof TextureAnimated) {
				call.texture = ((TextureAnimated) call.texture).currentFrame();
				final Rectangle uv = call.texture.getUv();
				call.uMin = uv.getX();
				call.vMin = uv.getY();
				call.uMax = uv.getMaxX();
				call.vMax = uv.getMaxY();
			}
			final int textureId = call.texture.getTextureId();
			float uMin;
			float vMin;
			float uMax;
			float vMax;
			if (call.texture instanceof TextureAtlas) {
				final TextureAtlas texAtlas = (TextureAtlas) call.texture;
				final Rectangle uv = texAtlas.getRealUv();
				if (uv == null) {
					throw new RuntimeException();
				}
				final AtlasStitcher atlas = texAtlas.getAtlas();
				final int texWidth = atlas.getTexWidth();
				final int texHeight = atlas.getTexHeight();
				uMin = 1f / texWidth * (uv.getX() + call.uMin);
				vMin = 1f / texHeight * (uv.getY() + call.vMin);
				uMax = 1f / texWidth * (uv.getX() + call.uMax);
				vMax = 1f / texHeight * (uv.getY() + call.vMax);
				final float UVOffset = 1f / texWidth;
				uMin += UVOffset;
				vMin += UVOffset;
				uMax -= UVOffset;
				vMax -= UVOffset;
			} else {
				final int texWidth = call.texture.getTextureSize().getWidth();
				final int texHeight = call.texture.getTextureSize().getHeight();
				final int texU = call.texture.getUv().getX();
				final int texV = call.texture.getUv().getY();
				uMin = 1f / texWidth * (texU + call.uMin);
				vMin = 1f / texHeight * (texV + call.vMin);
				uMax = 1f / texWidth * (texU + call.uMax);
				vMax = 1f / texHeight * (texV + call.vMax);
			}
			final BufferBuilder builder = new BufferBuilder(GeneralVertexFormats.FORMAT_TEXTURE, 4, GL11.GL_TRIANGLE_FAN, () -> {
				this.shader.bind();
				this.shader.setUniform1B("doTexture", true);
				this.shader.setUniformMat4("matModel", matrix);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
				GL11.glBindTexture(GL_TEXTURE_2D, textureId);
			});
			builder.position(startX, startY).color(call.colors[0]).uv(uMin, vMin).endVertex();
			builder.position(stopX, startY).color(call.colors[1]).uv(uMax, vMin).endVertex();
			builder.position(stopX, stopY).color(call.colors[2]).uv(uMax, vMax).endVertex();
			builder.position(startX, stopY).color(call.colors[3]).uv(uMin, vMax).endVertex();
			builder.draw();
		} else {

			//Calculate all positions
			final float startX = call.bounds.getX();
			final float startY = call.bounds.getY();
			final float stopX = call.bounds.getMaxX();
			final float stopY = call.bounds.getMaxY();
			final BufferBuilder builder = new BufferBuilder(GeneralVertexFormats.FORMAT_COLOR, 4, GL11.GL_TRIANGLE_FAN, () -> {
				this.shader.bind();
				this.shader.setUniform1B("doTexture", false);
				this.shader.setUniformMat4("matModel", matrix);
			});
			builder.position(startX, startY).color(call.colors[0]).endVertex();
			builder.position(stopX, startY).color(call.colors[1]).endVertex();
			builder.position(stopX, stopY).color(call.colors[2]).endVertex();
			builder.position(startX, stopY).color(call.colors[3]).endVertex();
			builder.draw();
		}
	}

	@Override
	public boolean isDrawing() {
		return this.drawing;
	}

	@NotNull
	Texture getTexture(@NotNull final ResourceLocation resource) {
		final IAsset asset = AssetRegistry.getAsset(resource);
		if (asset instanceof Texture) {
			return (Texture) asset;
		} else {
			if (TessellatorImpl.TEXTURE_MISSING == null) {
				TessellatorImpl.TEXTURE_MISSING = TessellatorImpl.createMissingTexture();
			}
			assert TessellatorImpl.TEXTURE_MISSING != null;
			return TessellatorImpl.TEXTURE_MISSING;
		}
	}

	private static Texture createMissingTexture() {
		final BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();
		graphics.setColor(java.awt.Color.MAGENTA);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		graphics.setColor(java.awt.Color.BLACK);
		graphics.fillRect(0, 0, image.getWidth() / 2, image.getHeight() / 2);
		graphics.fillRect(image.getWidth() / 2, image.getHeight() / 2, image.getWidth() / 2, image.getHeight() / 2);
		graphics.dispose();
		final MessageEvent event = new MessageEvent("glfw_create_gl_texture", new ResourceLocation("missing"), image);
		if (Shattered.SYSTEM_BUS.post(event)) {
			return null;
		}
		final Supplier<?> response = event.getResponse();
		assert response != null;
		return (Texture) response.get();
	}
}