package shattered.lib.asset;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import shattered.core.event.EventListener;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.Shattered;
import shattered.lib.FastNamedObjectMap;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;

public final class AtlasStitcher {

	private final HashMap<Integer, Rectangle> uvMapping = new HashMap<>();
	private final ArrayList<AtlasData> dataList = new ArrayList<>();
	private boolean complete = false;
	private int nextAtlasId = 0;
	private int texWidth = 0, texHeight = 0;
	BufferedImage image = null;
	int textureId = -1;

	AtlasStitcher() {
		Shattered.SYSTEM_BUS.register(this);
	}

	void reset() {
		GL11.glDeleteTextures(this.textureId);
		this.uvMapping.clear();
		this.dataList.clear();
		this.complete = false;
		this.nextAtlasId = 0;
		this.texWidth = this.texHeight = 0;
		this.image = null;
		this.textureId = -1;
	}

	@MessageListener(value = "atlas_stitch", listenerInfo = @EventListener(bus = "SYSTEM"))
	private void listenToSystemEvent(final MessageEvent event) {
		this.stitch();
	}

	@NotNull
	TextureAtlasDefault addImage(@NotNull final ResourceLocation resource, @NotNull final BufferedImage image) {
		final AtlasData data = this.addImageInternal(resource, image);
		return new TextureAtlasDefault(resource, this, data.id, Dimension.create(image.getWidth(), image.getHeight()));
	}

	@NotNull
	TextureAtlasStitched addImageStitched(@NotNull final ResourceLocation resource, @NotNull final BufferedImage image, final int usableWidth, @NotNull final Dimension spriteSize) {
		final AtlasData data = this.addImageInternal(resource, image);
		return new TextureAtlasStitched(resource, this, data.id, Dimension.create(image.getWidth(), image.getHeight()), usableWidth, spriteSize);
	}

	@NotNull
	TextureAtlasMapped addImageMapped(@NotNull final ResourceLocation resource, @NotNull final BufferedImage image, @NotNull final FastNamedObjectMap<Rectangle> mapping) {
		final AtlasData data = this.addImageInternal(resource, image);
		return new TextureAtlasMapped(resource, this, data.id, Dimension.create(image.getWidth(), image.getHeight()), mapping);
	}

	@NotNull
	TextureAtlasAnimated addImageAnimated(@NotNull final ResourceLocation resource, @NotNull final BufferedImage image, final double fps, final int frames, @Nullable final int[] frameMapping, final boolean horizontal) {
		final AtlasData data = this.addImageInternal(resource, image);
		return new TextureAtlasAnimated(resource, this, data.id, Dimension.create(image.getWidth(), image.getHeight()), fps, frames, frameMapping, horizontal);
	}

	@NotNull
	private AtlasData addImageInternal(@NotNull final ResourceLocation resource, @NotNull final BufferedImage image) {
		final AtlasData data = new AtlasData(resource, image, this.nextAtlasId++);
		this.dataList.add(data);
		return data;
	}

	void stitch() {
		if (this.complete || this.nextAtlasId <= 0) {
			return;
		}
		final long startTime = Shattered.getSystemTime();
		final Dimension atlasSize = Dimension.createMutable(64, 64);
		int tries = 0;
		while (!this.complete) {
			try {
				final ImagePacker stitcher = new ImagePacker(atlasSize.getWidth(), atlasSize.getHeight());
				for (final AtlasData data : this.dataList) {
					stitcher.addImage(data.resource, data.image);
				}
				for (final AtlasData data : this.dataList) {
					this.uvMapping.put(data.id, stitcher.getMapping().get(data.resource));
				}
				this.image = stitcher.getImage();
				final int[] texData = ImageLoader.loadTexture(this.image);
				this.textureId = texData[0];
				this.texWidth = texData[1];
				this.texHeight = texData[2];
				this.complete = true;
			} catch (final RuntimeException ignored) {
				if (tries % 2 == 0) {
					atlasSize.addWidth(atlasSize.getWidth());
				} else {
					atlasSize.addHeight(atlasSize.getHeight());
				}
				++tries;
				if (tries >= 25) {
					throw new RuntimeException("Could not create atlas after 25 tries!");
				}
			}
		}
		this.dataList.clear();
		final long duration = Shattered.getSystemTime() - startTime;
		Shattered.LOGGER.debug("Created a {}x{} atlas (stitching took {} milliseconds)", atlasSize.getWidth(), atlasSize.getHeight(), duration);
	}

	@Nullable
	Rectangle getRealUv(final int atlasId) {
		return this.uvMapping.get(atlasId);
	}

	public int getTexWidth() {
		return this.texWidth;
	}

	public int getTexHeight() {
		return this.texHeight;
	}
}