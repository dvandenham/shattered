package shattered.lib.asset;

import java.net.URL;
import shattered.lib.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class LanguageAsset extends IAsset {

	@NotNull
	public final URL location;

	LanguageAsset(@NotNull final ResourceLocation resource, @NotNull final URL location) {
		super(resource);
		this.location = location;
	}
}