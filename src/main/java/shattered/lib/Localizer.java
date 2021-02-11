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
	private static final Logger LOGGER = LogManager.getLogger(Localizer.class);
	private static final Object2ObjectArrayMap<ResourceLocation, URL> LOCATIONS = new Object2ObjectArrayMap<>();
	private static final ObjectArrayList<ResourceLocation> LANGUAGES = new ObjectArrayList<>();
	private static final FastNamedObjectMap<String> CACHE = new FastNamedObjectMap<>();
	private static final FastNamedObjectMap<String> CACHE_FORMATTED = new FastNamedObjectMap<>();
	private static ResourceLocation Language = null, Fallback = null;

	public static void AddLanguage(@NotNull final URL Location, @NotNull final ResourceLocation Language) {
		if (Localizer.LANGUAGES.contains(Language)) {
			Localizer.LOGGER.debug("Reloading language: {}", Language);
		} else {
			Localizer.LANGUAGES.add(Language);
			Localizer.LOGGER.debug("Registering language: {}", Language);
		}
		if (Localizer.Fallback == null) {
			Localizer.Fallback = Language;
			Localizer.Language = Language;
			Localizer.LOGGER.debug("Using language \"{}\" as fallback", Language);
		}
		if (!Localizer.LOCATIONS.containsKey(Language)) {
			Localizer.LOCATIONS.put(Language, Location);
		}
		try (final BufferedReader Reader = new BufferedReader(new InputStreamReader(Location.openStream()))) {
			String Line;
			while ((Line = Reader.readLine()) != null) {
				if (!Line.contains("=")) {
					continue;
				}
				final String[] LineData = Line.split("=", 2);
				Localizer.CACHE.put(Language.toString() + "." + LineData[0], LineData[1]);
				Localizer.LOGGER.trace("Registered translation: {}.{}={}", Language, LineData[0], LineData[1]);
			}
		} catch (final IOException e) {
			Localizer.LOGGER.error(e);
		}
	}

	@MessageListener("reload_localizer")
	private static void Reload(final MessageEvent ignored) {
		Localizer.CACHE.clear();
		Localizer.CACHE_FORMATTED.clear();
		for (final ResourceLocation LanguageCode : Localizer.LANGUAGES) {
			Localizer.AddLanguage(Localizer.LOCATIONS.get(LanguageCode), LanguageCode);
		}
	}

	@NotNull
	public static String Localize(@NotNull final String Key) {
		return Localizer.Localize(Localizer.GetCurrentLanguage(), Key);
	}

	@NotNull
	public static String Localize(@Nullable final ResourceLocation Language, @NotNull final String Key) {
		if (Language == null) {
			return Key;
		}
		if (Key.trim().isEmpty()) {
			return "";
		}
		final String LanguageKey = Language.toString() + "." + Key;
		String Result = Localizer.CACHE.get(LanguageKey);
		if (Result != null) {
			return Result;
		}
		Localizer.LOGGER.debug("No entry found for: {}", LanguageKey);
		if (Localizer.Fallback == null) {
			Localizer.LOGGER.debug("No fallback language found for: {}", LanguageKey);
			Localizer.LOGGER.debug("\tRegistering: {}={}", LanguageKey, Key);
			Localizer.CACHE.put(LanguageKey, Key);
			return Key;
		}
		if (Localizer.Fallback.equals(Language)) {
			Localizer.LOGGER.debug("Already using fallback language for: {}", Language);
			Localizer.LOGGER.debug("\tRegistering: {}={}", LanguageKey, Key);
			Localizer.CACHE.put(LanguageKey, Key);
			return Key;
		}
		final String FallbackKey = Localizer.Fallback + "." + Key;
		Result = Localizer.CACHE.get(FallbackKey);
		if (Result == null) {
			Localizer.LOGGER.debug("No fallback-entry found for: " + LanguageKey + " and " + FallbackKey);
			Localizer.LOGGER.debug("\tRegistering: {}={}", FallbackKey, Key);
			Localizer.CACHE.put(FallbackKey, Key);
			Localizer.LOGGER.debug("\tRegistering: {}={}", LanguageKey, Key);
			Localizer.CACHE.put(LanguageKey, Key);
			return Key;
		}
		Localizer.LOGGER.debug("Registering: {}={}", FallbackKey, Result);
		Localizer.CACHE.put(LanguageKey, Result);
		return Result;
	}

	@NotNull
	public static String Format(@NotNull final String Key, final Object... FormatData) {
		return Localizer.Format(Localizer.GetCurrentLanguage(), Key, FormatData);
	}

	@NotNull
	public static String Format(@Nullable final ResourceLocation Language, @NotNull final String Key, final Object... FormatData) {
		if (Language == null) {
			return Key;
		}
		final String LanguageKey = Language.toString() + "." + Key;
		String Result = Localizer.CACHE_FORMATTED.get(LanguageKey);
		if (Result != null) {
			return Result;
		}
		final String Unformatted = Localizer.Localize(Language, Key);
		Result = String.format(Unformatted, FormatData);
		Localizer.CACHE_FORMATTED.put(LanguageKey, Result);
		return Result;
	}

	public static void SetCurrentLanguage(@NotNull final ResourceLocation Language) {
		Localizer.Language = Language;
	}

	@NotNull
	public static ResourceLocation GetCurrentLanguage() {
		return Localizer.Language;
	}

	@NotNull
	public static ResourceLocation GetDefaultLanguage() {
		return Localizer.DEFAULT_LANGUAGE;
	}

	private Localizer() {
	}
}
