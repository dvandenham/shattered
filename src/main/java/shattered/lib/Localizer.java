package shattered.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import shattered.Shattered;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.lib.asset.AssetRegistry;
import shattered.lib.asset.IAsset;
import shattered.lib.asset.LanguageAsset;
import shattered.lib.json.JsonUtils;
import shattered.lib.registry.Registry;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
public final class Localizer {

	private static final ResourceLocation DEFAULT_LANGUAGE = new ResourceLocation("en_us");
	private static final Logger LOGGER = LogManager.getLogger("Localizer");
	private static final FastNamedObjectMap<String> CACHE = new FastNamedObjectMap<>();
	private static ResourceLocation languageFallback = null;
	private static ResourceLocation languageCurrent = null;

	private Localizer() {
	}

	@NotNull
	public static String localize(@NotNull final String key) {
		return Localizer.localize(Localizer.getActiveLanguage(), key);
	}

	@NotNull
	public static String localize(@Nullable final ResourceLocation language, @NotNull final String key) {
		if (language == null) {
			return key;
		}
		if (key.trim().isEmpty()) {
			return "";
		}
		final String languageKey = language.toString() + "." + key;
		String result = Localizer.CACHE.get(languageKey);
		if (result != null) {
			return result;
		}
		Localizer.LOGGER.debug("No entry found for: {}", languageKey);
		if (Localizer.languageFallback == null) {
			Localizer.LOGGER.debug("No fallback language found for: {}", languageKey);
			Localizer.LOGGER.debug("\tRegistering: {}={}", languageKey, key);
			Localizer.CACHE.put(languageKey, key);
			return key;
		}
		if (Localizer.languageFallback.equals(language)) {
			Localizer.LOGGER.debug("Already using fallback language for: {}", language);
			Localizer.LOGGER.debug("\tRegistering: {}={}", languageKey, key);
			Localizer.CACHE.put(languageKey, key);
			return key;
		}
		final String fallbackKey = Localizer.languageFallback + "." + key;
		result = Localizer.CACHE.get(fallbackKey);
		if (result == null) {
			Localizer.LOGGER.debug("No fallback-entry found for: " + languageKey + " and " + fallbackKey);
			Localizer.LOGGER.debug("\tRegistering: {}={}", fallbackKey, key);
			Localizer.CACHE.put(fallbackKey, key);
			Localizer.LOGGER.debug("\tRegistering: {}={}", languageKey, key);
			Localizer.CACHE.put(languageKey, key);
			return key;
		}
		Localizer.LOGGER.debug("Registering: {}={}", fallbackKey, result);
		Localizer.CACHE.put(languageKey, result);
		return result;
	}

	@NotNull
	public static String format(@NotNull final String key, final Object... formatData) {
		return Localizer.format(Localizer.getActiveLanguage(), key, formatData);
	}

	@NotNull
	public static String format(@Nullable final ResourceLocation language, @NotNull final String key, final Object... formatData) {
		return String.format(Localizer.localize(language, key), formatData);
	}

	public static void setActiveLanguage(@NotNull final ResourceLocation language) {
		Localizer.languageCurrent = language;
	}

	@NotNull
	public static ResourceLocation getActiveLanguage() {
		return Localizer.languageCurrent;
	}

	@NotNull
	public static ResourceLocation getDefaultLanguage() {
		return Localizer.DEFAULT_LANGUAGE;
	}

	@SuppressWarnings("unchecked")
	private static void reload() {
		Localizer.languageFallback = null;
		final Field[] assetsField = ReflectionHelper.collectFields(AssetRegistry.class, field -> {
			final Type type = field.getGenericType();
			if (!(type instanceof ParameterizedType)) {
				return false;
			}
			final Type[] paramTypes = ((ParameterizedType) type).getActualTypeArguments();
			return paramTypes.length == 1 && paramTypes[0].equals(IAsset.class);
		});
		if (assetsField.length != 1) {
			throw new RuntimeException("Could not probe AssetRegistry!");
		}
		try {
			assetsField[0].setAccessible(true);
			final Registry<IAsset> registry = (Registry<IAsset>) assetsField[0].get(null);
			registry.forEach(entry -> {
				final ResourceLocation resource = entry.getKey();
				final IAsset asset = entry.getValue();
				if (asset instanceof LanguageAsset) {
					if (Localizer.languageFallback == null) {
						Localizer.languageFallback = resource;
						Localizer.LOGGER.debug("Using language \"{}\" as fallback", resource);
						if (Localizer.languageCurrent == null) {
							Localizer.languageCurrent = resource;
						}
					}
					final URL location = ((LanguageAsset) asset).location;
					if (location.toString().endsWith(".json")) {
						try (final InputStream in = location.openStream()) {
							final JsonObject obj = JsonUtils.deserialize(in, JsonObject.class);
							if (obj == null) {
								Localizer.LOGGER.error("Could not read language {}. Reason: unsupported format", resource);
								return;
							}
							final Map<String, String> lines = Localizer.flattenJsonToMap(obj);
							lines.forEach((key, value) -> Localizer.registerLine(resource, key, value));
						} catch (final IOException | JsonParseException e) {
							Localizer.LOGGER.error("Could not read language {}", resource);
							Localizer.LOGGER.error(e);
						}
					} else if (location.toString().endsWith(".lang")) {
						try (final BufferedReader reader = new BufferedReader(new InputStreamReader(location.openStream()))) {
							String line;
							while ((line = reader.readLine()) != null) {
								if (line.contains("=")) {
									final String[] values = line.split("=", 2);
									Localizer.registerLine(resource, values[0], values[1]);
								}
							}
						} catch (final IOException e) {
							Localizer.LOGGER.error("Could not read language {}", resource);
							Localizer.LOGGER.error(e);
						}
					} else {
						Localizer.LOGGER.error("Could not read language {}. Reason: unsupported format", resource);
					}
				}
			});
		} catch (final IllegalAccessException e) {
			throw new RuntimeException("Could not probe AssetRegistry!", e);
		}
	}

	private static void registerLine(@NotNull final ResourceLocation language, @NotNull final String key, @NotNull final String value) {
		Localizer.CACHE.put(language + "." + key, value);
		Localizer.LOGGER.trace("Registered translation: {}.{}={}", language, key, value);
	}

	@MessageListener("reload_localizer")
	private static void onSystemMessage(final MessageEvent ignored) {
		Localizer.CACHE.clear();
		Localizer.reload();
	}

	private static Map<String, String> flattenJsonToMap(@NotNull final JsonObject obj) {
		final HashMap<String, String> map = new HashMap<>();
		Localizer.flattenJsonToMap(obj, map, null);
		return map;
	}

	private static void flattenJsonToMap(@NotNull final JsonObject obj, @NotNull final Map<String, String> map, @Nullable String prefix) {
		prefix = prefix == null ? "" : prefix + ".";
		for (final String key : obj.keySet()) {
			if (JsonUtils.hasObject(obj, key)) {
				Localizer.flattenJsonToMap(JsonUtils.getObject(obj, key), map, prefix + key);
			} else {
				map.put(prefix + key, obj.get(key).getAsString());
			}
		}
	}
}