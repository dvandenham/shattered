package shattered;

import shattered.lib.ReflectionHelper;
import shattered.lib.ResourceLocation;
import shattered.lib.asset.AssetRegistry;
import shattered.lib.asset.Texture;
import shattered.lib.gfx.Shader;

public final class StaticAssets {

	public static Shader SHADER;
	public static Texture TEXTURE_MISSING;

	private StaticAssets() {
	}

	static void loadAssets() {
		StaticAssets.SHADER = new Shader(Assets.SHADER_VERTEX, Assets.SHADER_FRAGMENT);
		StaticAssets.TEXTURE_MISSING = ReflectionHelper.invokeMethod(
				AssetRegistry.class,
				null,
				Texture.class,
				ResourceLocation.class,
				new ResourceLocation("missing")
		);
		ReflectionHelper.invokeMethod(
				AssetRegistry.class,
				null,
				Texture.class,
				ResourceLocation.class,
				Assets.TEXTURE_ARGON
		);
		ReflectionHelper.invokeMethod(
				AssetRegistry.class,
				null,
				Texture.class,
				ResourceLocation.class,
				Assets.TEXTURE_LOADING
		);
	}
}