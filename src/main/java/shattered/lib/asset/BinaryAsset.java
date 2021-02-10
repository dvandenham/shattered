package shattered.lib.asset;

import java.io.InputStream;
import java.net.URL;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.ResourceLocation;

public final class BinaryAsset extends IAsset {

	private final String path;

	BinaryAsset(@NotNull final ResourceLocation resource, @NotNull final String path) {
		super(resource);
		this.path = path;
	}

	@Nullable
	public URL getUrl() {
		return AssetRegistry.getPathUrl(this.path);
	}

	@Nullable
	public InputStream openStream() {
		return BinaryAsset.class.getResourceAsStream(this.path);
	}
}