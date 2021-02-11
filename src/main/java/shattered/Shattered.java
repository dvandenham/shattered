package shattered;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import preboot.AnnotationRegistry;
import preboot.BootManager;
import shattered.core.event.EventBus;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.IEventBus;
import shattered.core.event.MessageEvent;
import shattered.lib.ReflectionHelper;
import shattered.lib.asset.FontGroup;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.FontRendererImpl;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gfx.TessellatorImpl;
import shattered.lib.registry.CreateRegistryEvent;

@BootManager
public final class Shattered {

	public static final boolean DEVELOPER_MODE = Boolean.getBoolean("shattered.developer");
	public static final String NAME = "Shattered";
	public static final Logger LOGGER = LogManager.getLogger(Shattered.NAME);
	public static final String SYSTEM_BUS_NAME = "SYSTEM";
	public static final IEventBus SYSTEM_BUS = EventBus.createBus(Shattered.SYSTEM_BUS_NAME);
	private static Shattered instance;
	public final Tessellator tessellator;
	public final FontRenderer fontRenderer;
	private final ThreadLoadingScreen loadingScreen;

	@SuppressWarnings("unused")
	public static void start(final String[] args) {
		if (Shattered.DEVELOPER_MODE) {
			Configurator.setRootLevel(Level.ALL);
		}
		Shattered.LOGGER.info("{} is starting!", Shattered.NAME);
		//Register all automatic EventBus subscribers
		AnnotationRegistry.getAnnotatedClasses(EventBusSubscriber.class).forEach(listener ->
				EventBus.register(listener, listener.getDeclaredAnnotation(EventBusSubscriber.class).value())
		);
		//Create all registry instances
		Shattered.instance = new Shattered(args);
		Shattered.instance.startLoadingScreen();
	}

	private Shattered(final String[] args) {
		this.initRegistries();

		Shattered.SYSTEM_BUS.post(new MessageEvent("init_glfw"));

		StaticAssets.loadAssets();
		final Tessellator tessellator = ReflectionHelper.instantiate(TessellatorImpl.class);
		if (tessellator == null) {
			Shattered.LOGGER.fatal("Could not initialize Tessellator!");
			Runtime.getRuntime().halt(-1);
		}
		this.tessellator = tessellator;
		final FontRenderer fontRenderer = ReflectionHelper.instantiate(FontRendererImpl.class, Tessellator.class, this.tessellator, FontGroup.class, StaticAssets.FONT_DEFAULT);
		if (fontRenderer == null) {
			Shattered.LOGGER.fatal("Could not initialize FontRenderer!");
			Runtime.getRuntime().halt(-1);
		}
		this.fontRenderer = fontRenderer;

		this.loadingScreen = new ThreadLoadingScreen(this);
	}

	private void initRegistries() {
		final CreateRegistryEvent event = ReflectionHelper.instantiate(CreateRegistryEvent.class);
		assert event != null;
		Shattered.SYSTEM_BUS.post(event);
	}

	private void startLoadingScreen() {
		((TessellatorImpl) this.tessellator).setShader(StaticAssets.SHADER);
		StaticAssets.SHADER.bind();
		Display.resetLogicalResolution();
		this.loadingScreen.start();
	}

	public static Shattered getInstance() {
		return Shattered.instance;
	}

	public static boolean isRunning() {
		return true;
	}
}