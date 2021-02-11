package shattered;

import shattered.lib.ReflectionHelper;
import shattered.lib.ResourceLocation;
import shattered.lib.asset.AssetRegistry;
import shattered.lib.asset.FontGroup;
import shattered.lib.asset.Texture;
import shattered.lib.gfx.Shader;

public final class StaticAssets {

	public static final ResourceLocation RESOURCE_SHADER_VERTEX = new ResourceLocation("vertex");
	public static final ResourceLocation RESOURCE_SHADER_FRAGMENT = new ResourceLocation("fragment");
	public static Shader SHADER;

	public static final ResourceLocation RESOURCE_TEXTURE_MISSING = new ResourceLocation("missing");
	public static Texture TEXTURE_MISSING;

	public static final ResourceLocation RESOURCE_TEXTURE_ARGON = new ResourceLocation("argon");
	public static Texture TEXTURE_ARGON;

	public static final ResourceLocation RESOURCE_FONT_DEFAULT = new ResourceLocation("default");
	public static FontGroup FONT_DEFAULT;

	private StaticAssets() {
	}

	static void loadAssets() {
		StaticAssets.SHADER = new Shader(StaticAssets.RESOURCE_SHADER_VERTEX, StaticAssets.RESOURCE_SHADER_FRAGMENT);
		StaticAssets.TEXTURE_MISSING = ReflectionHelper.invokeMethod(
				AssetRegistry.class,
				null,
				Texture.class,
				ResourceLocation.class,
				StaticAssets.RESOURCE_TEXTURE_MISSING
		);
		StaticAssets.TEXTURE_ARGON = ReflectionHelper.invokeMethod(
				AssetRegistry.class,
				null,
				Texture.class,
				ResourceLocation.class,
				StaticAssets.RESOURCE_TEXTURE_ARGON
		);
		StaticAssets.FONT_DEFAULT = ReflectionHelper.invokeMethod(
				AssetRegistry.class,
				null,
				FontGroup.class,
				ResourceLocation.class,
				StaticAssets.RESOURCE_FONT_DEFAULT
		);
	}
}