package shattered.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.Shattered;

@SuppressWarnings("unused")
@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
public final class Localizer {

	private static final ResourceLocation DEFAULT_LANGUAGE = new ResourceLocation("en_us");
	private static final Logger LOGGER = LogManager.getLogger(Localizer.class.getSimpleName());
	private static final Object2ObjectArrayMap<ResourceLocation, URL> LOCATIONS = new Object2ObjectArrayMap<>();
	private static final ObjectArrayList<ResourceLocation> LANGUAGES = new ObjectArrayList<>();
	private static final FastNamedObjectMap<String> CACHE = new FastNamedObjectMap<>();
	private static final FastNamedObjectMap<String> CACHE_FORMATTED = new FastNamedObjectMap<>();
	private static ResourceLocation language = null, fallback = null;

	private Localizer() {
	}

	public static void addLanguage(@NotNull final URL location, @NotNull final ResourceLocation language) {
		if (Localizer.LANGUAGES.contains(language)) {
			Localizer.LOGGER.debug("Reloading language: {}", language);
		} else {
			Localizer.LANGUAGES.add(language);
			Localizer.LOGGER.debug("Registering language: {}", language);
		}
		if (Localizer.fallback == null) {
			Localizer.fallback = language;
			Localizer.language = language;
			Localizer.LOGGER.debug("Using language \"{}\" as fallback", language);
		}
		if (!Localizer.LOCATIONS.containsKey(language)) {
			Localizer.LOCATIONS.put(language, location);
		}
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(location.openStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.contains("=")) {
					continue;
				}
				final String[] lineData = line.split("=", 2);
				Localizer.CACHE.put(language.toString() + "." + lineData[0], lineData[1]);
				Localizer.LOGGER.trace("Registered translation: {}.{}={}", language, lineData[0], lineData[1]);
			}
		} catch (final IOException e) {
			Localizer.LOGGER.error(e);
		}
	}

	@MessageListener("reload_localizer")
	private static void onSystemMessage(final MessageEvent ignored) {
		Localizer.CACHE.clear();
		Localizer.CACHE_FORMATTED.clear();
		for (final ResourceLocation LanguageCode : Localizer.LANGUAGES) {
			Localizer.addLanguage(Localizer.LOCATIONS.get(LanguageCode), LanguageCode);
		}
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
		if (Localizer.fallback == null) {
			Localizer.LOGGER.debug("No fallback language found for: {}", languageKey);
			Localizer.LOGGER.debug("\tRegistering: {}={}", languageKey, key);
			Localizer.CACHE.put(languageKey, key);
			return key;
		}
		if (Localizer.fallback.equals(language)) {
			Localizer.LOGGER.debug("Already using fallback language for: {}", language);
			Localizer.LOGGER.debug("\tRegistering: {}={}", languageKey, key);
			Localizer.CACHE.put(languageKey, key);
			return key;
		}
		final String fallbackKey = Localizer.fallback + "." + key;
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
		if (language == null) {
			return key;
		}
		final String languageKey = language.toString() + "." + key;
		String result = Localizer.CACHE_FORMATTED.get(languageKey);
		if (result != null) {
			return result;
		}
		final String unformatted = Localizer.localize(language, key);
		result = String.format(unformatted, formatData);
		Localizer.CACHE_FORMATTED.put(languageKey, result);
		return result;
	}

	public static void setActiveLanguage(@NotNull final ResourceLocation language) {
		Localizer.language = language;
	}

	@NotNull
	public static ResourceLocation getActiveLanguage() {
		return Localizer.language;
	}

	@NotNull
	public static ResourceLocation getDefaultLanguage() {
		return Localizer.DEFAULT_LANGUAGE;
	}
}
