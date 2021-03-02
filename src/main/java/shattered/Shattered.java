package shattered;

import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import nl.appelgebakje22.preboot.PrebootRegistry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import preboot.BootManager;
import shattered.lib.Color;
import shattered.lib.IInput;
import shattered.lib.Input;
import shattered.lib.ReflectionHelper;
import shattered.lib.Workspace;
import shattered.lib.asset.FontGroup;
import shattered.lib.event.EventBus;
import shattered.lib.event.EventBusHandler;
import shattered.lib.event.EventBusSubscriber;
import shattered.lib.event.IEventBus;
import shattered.lib.event.MessageEvent;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.FontRendererImpl;
import shattered.lib.gfx.GLHelper;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gfx.TessellatorImpl;
import shattered.lib.gui.GuiManager;
import shattered.lib.registry.CreateRegistryEvent;
import shattered.pack.PluginManager;
import shattered.screen.ScreenMainMenu;
import static org.lwjgl.glfw.GLFW.glfwGetTimerFrequency;
import static org.lwjgl.glfw.GLFW.glfwGetTimerValue;

@BootManager
public final class Shattered {

	public static final boolean DEVELOPER_MODE = Boolean.getBoolean("shattered.developer");

	public static final String NAME = "Shattered";
	public static final String VERSION = Shattered.readManifest().getMainAttributes().getValue("VERSION");

	public static final Logger LOGGER = LogManager.getLogger(Shattered.NAME);
	public static final String SYSTEM_BUS_NAME = "SYSTEM";
	public static final IEventBus SYSTEM_BUS = EventBusHandler.createBus(Shattered.SYSTEM_BUS_NAME);
	public static final Workspace WORKSPACE = Objects.requireNonNull(
			ReflectionHelper.instantiate(Workspace.class, String.class, Shattered.NAME.toLowerCase(Locale.ROOT))
	);
	public static final PluginManager PLUGINS = new PluginManager(Shattered.WORKSPACE.getDataDir());

	private static final AtomicBoolean RUNNING = new AtomicBoolean(true);

	private static Shattered instance;
	public final Tessellator tessellator;
	public final FontRenderer fontRenderer;
	private final ThreadLoadingScreen loadingScreen;

	//Delegate methods
	private Runnable delegateGuiManagerTick;
	private BiConsumer<Tessellator, FontRenderer> delegateGuiManagerRender;
	private Runnable delegateInputPoller;

	@SuppressWarnings("unused")
	public static void start(final PrebootRegistry booter, final String[] args) {
		if (Shattered.DEVELOPER_MODE) {
			final LoggerContext context = (LoggerContext) LogManager.getContext(false);
			final Configuration configuration = context.getConfiguration();
			final LoggerConfig loggerConfig = configuration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
			loggerConfig.setLevel(Level.ALL);
			context.updateLoggers();
		}
		Shattered.LOGGER.info("Starting {} (version: {})!", Shattered.NAME, Shattered.VERSION);
		//Setup EventBus
		try {
			Shattered.LOGGER.debug("Setting up EventBus");
			final IEventBus defaultBus = ReflectionHelper.getField(EventBusHandler.class, null, IEventBus.class, ignored -> true);
			//noinspection ConstantConditions
			ReflectionHelper.setField(ReflectionHelper.findField(EventBus.class, IEventBus.class), null, defaultBus);
			//noinspection ConstantConditions
			ReflectionHelper.setField(ReflectionHelper.findField(EventBus.class, Function.class), null, (Function<String, IEventBus>) EventBusHandler::createBus);
			//noinspection ConstantConditions
			ReflectionHelper.setField(ReflectionHelper.findField(EventBus.class, BiConsumer.class), null, (BiConsumer<Object, String>) EventBusHandler::register);
		} catch (final Throwable e) {
			if (Shattered.DEVELOPER_MODE) {
				Shattered.LOGGER.fatal(e);
			}
			Shattered.crash("Could not setup EventBus!");
		}
		//Register all automatic EventBus subscribers
		Shattered.LOGGER.debug("Registering automatic EventBus subscribers");
		booter.getAnnotatedClasses(EventBusSubscriber.class).forEach(listener ->
				EventBus.register(listener, listener.getDeclaredAnnotation(EventBusSubscriber.class).value())
		);
		//Load config database
		Shattered.LOGGER.info("Loading config database!");
		Config.DISPLAY_SIZE.get(); //Force load to register all config options
		Shattered.SYSTEM_BUS.post(new MessageEvent("load_config"));
		//Create all registry instances
		Shattered.instance = new Shattered(args);
		Shattered.instance.startLoadingScreen();
		Shattered.instance.startLoading();
		//This call will block the program until a shutdown was requested
		Shattered.instance.startRuntime();
		Shattered.instance.cleanup();
		Shattered.LOGGER.info("Goodbye!");
	}

	private Shattered(final String[] args) {
		Shattered.LOGGER.debug("Loading plugins");
		Shattered.SYSTEM_BUS.post(new MessageEvent("load_plugins"));

		Shattered.LOGGER.debug("Notifying registry holders for registry creation");
		final CreateRegistryEvent event = ReflectionHelper.instantiate(CreateRegistryEvent.class);
		assert event != null;
		Shattered.SYSTEM_BUS.post(event);

		Shattered.SYSTEM_BUS.post(new MessageEvent("init_glfw"));

		Shattered.LOGGER.debug("Loading static assets");
		StaticAssets.loadAssets();

		Shattered.LOGGER.debug("Initializing rendering system");
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

	private void startLoadingScreen() {
		((TessellatorImpl) this.tessellator).setShader(StaticAssets.SHADER);
		StaticAssets.SHADER.bind();
		Display.resetLogicalResolution();
		this.loadingScreen.start();
	}

	private void startLoading() {
		//Load assets
		Shattered.LOGGER.debug("Notifying AssetRegistry for initializing");
		Shattered.SYSTEM_BUS.post(new MessageEvent("init_assets"));
		//Initialize GuiHandler and main menu screen
		Shattered.LOGGER.debug("Initializing gui system");
		final MessageEvent eventSetupGui = new MessageEvent("init_gui");
		Shattered.SYSTEM_BUS.post(eventSetupGui);
		assert eventSetupGui.getResponse() != null;
		this.delegateGuiManagerTick = (Runnable) ((Object[]) eventSetupGui.getResponse().get())[0];
		//noinspection unchecked
		this.delegateGuiManagerRender = (BiConsumer<Tessellator, FontRenderer>) ((Object[]) eventSetupGui.getResponse().get())[1];
		GuiManager.INSTANCE.openScreen(new ScreenMainMenu());

		//Setup input handlers
		Shattered.LOGGER.debug("Initializing keyboard/mouse input handler");
		final MessageEvent handleInputSetupEvent = new MessageEvent("input_setup");
		Shattered.SYSTEM_BUS.post(handleInputSetupEvent);
		try {
			final Supplier<?> responseSupplier = handleInputSetupEvent.getResponse();
			assert responseSupplier != null;
			final Object[] response = (Object[]) responseSupplier.get();
			//noinspection ConstantConditions
			ReflectionHelper.setField(ReflectionHelper.findField(Input.class, IInput.class), null, response[0]);
			this.delegateInputPoller = (Runnable) response[1];
		} catch (final Throwable e) {
			if (Shattered.DEVELOPER_MODE) {
				Shattered.LOGGER.fatal(e);
			}
			Shattered.crash("Could not setup input handlers!");
		}

		//Initialize LuaMachine
		Shattered.LOGGER.debug("Initializing LuaMachine");
		Shattered.SYSTEM_BUS.post(new MessageEvent("init_lua_machine"));

		//Stop loading screen
		this.loadingScreen.tryStop();

		//Stitching textures
		Shattered.LOGGER.debug("Stitching all TextureAtlas subscribers");
		Shattered.SYSTEM_BUS.post(new MessageEvent("atlas_stitch"));
	}

	private void startRuntime() {
		final RuntimeTimer timer = new RuntimeTimer(this::runtimeTick, this::runtimeRender, this::runtimeCatchup);
		while (Shattered.isRunning()) {
			final Throwable cachedError = timer.execute();
			if (cachedError != null) {
				Shattered.RUNNING.set(false);
				Shattered.LOGGER.fatal("Fatal error during Runtime loop!", cachedError);
			}
		}
	}

	private void cleanup() {
		Shattered.LOGGER.debug("Cleaning up!");
		Shattered.SYSTEM_BUS.post(new MessageEvent("shutdown"));
	}

	@Nullable
	private Throwable runtimeTick(@NotNull final RuntimeTimer timer) {
		try {
			GLFW.glfwPollEvents();
			this.delegateInputPoller.run();

			this.delegateGuiManagerTick.run();

			return null;
		} catch (final Throwable e) {
			return e;
		}
	}

	@Nullable
	private Throwable runtimeRender(@NotNull final RuntimeTimer timer) {
		try {
			//Clear buffers
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			//Setup OpenGL
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			GLHelper.disableScissor();
			//Render gui
			GLHelper.resetScissor();
			this.delegateGuiManagerRender.accept(this.tessellator, this.fontRenderer);

			//Render runtime metrics
			if (Shattered.DEVELOPER_MODE) {
				final StringData metrics = new StringData(
						"FPS: " + timer.getCachedFps() + " TickLength (ms): " + String.format("%.2f", timer.getPrevIterationLength()),
						Color.YELLOW
				).localize(false);
				this.fontRenderer.setFont(StaticAssets.RESOURCE_FONT_SIMPLE);
				this.fontRenderer.setFontSize(24);
				final int width = this.fontRenderer.getWidth(metrics);
				final int height = this.fontRenderer.getHeight(metrics);
				this.tessellator.drawQuick(0, 0, width, height, Color.BLACK);
				this.fontRenderer.writeQuick(2, 0, metrics);
				this.fontRenderer.revertFontSize();
				this.fontRenderer.resetFont();
			}
			GLFW.glfwSwapBuffers(Display.getWindowId());
			return null;
		} catch (final Throwable e) {
			return e;
		}
	}

	@Nullable
	private Throwable runtimeCatchup(@NotNull final RuntimeTimer timer) {
		return null;
	}

	public void stop() {
		Shattered.LOGGER.debug("Shutdown has been requested");
		Shattered.RUNNING.lazySet(false);
	}

	public static Shattered getInstance() {
		return Shattered.instance;
	}

	public static boolean isRunning() {
		return Shattered.RUNNING.get();
	}

	public static long getSystemTime() {
		return glfwGetTimerValue() * 1000 / glfwGetTimerFrequency();
	}

	public static void crash(@NotNull final String reason) {
		Shattered.LOGGER.fatal(reason);
		Runtime.getRuntime().halt(-1);
	}

	private static final class RuntimeTimer {

		private static final int TICK_RATE = 60;
		private static final double SECONDS_PER_TICK = 1.0 / RuntimeTimer.TICK_RATE;

		private final RuntimeTimerExecutor tickAction;
		private final RuntimeTimerExecutor renderAction;
		private final RuntimeTimerExecutor catchupAction;

		//Runtime loop
		private long iterationStartTime = Shattered.getSystemTime();
		private long iterationLength;
		private double iterationAccumulator = 0;

		//FPS counter
		private long fpsCountStartTime = Shattered.getSystemTime();
		private int fpsAccumulator = 0;
		private int cachedFps = 0;

		private RuntimeTimer(
				@NotNull final RuntimeTimerExecutor tickAction,
				@NotNull final RuntimeTimerExecutor renderAction,
				@NotNull final RuntimeTimerExecutor catchupAction
		) {
			this.tickAction = tickAction;
			this.renderAction = renderAction;
			this.catchupAction = catchupAction;
		}

		@Nullable
		public Throwable execute() {
			this.iterationAccumulator += this.calcAndHandleDelta();
			Throwable cachedError = this.tickAction.execute(this);
			if (cachedError != null) {
				return cachedError;
			}
			while (this.iterationAccumulator >= RuntimeTimer.SECONDS_PER_TICK) {
				cachedError = this.catchupAction.execute(this);
				if (cachedError != null) {
					return cachedError;
				}
				this.iterationAccumulator -= RuntimeTimer.SECONDS_PER_TICK;
			}
			cachedError = this.renderAction.execute(this);
			if (cachedError != null) {
				return cachedError;
			}
			++this.fpsAccumulator;
			if (Shattered.getSystemTime() - this.fpsCountStartTime >= 1000) {
				this.cachedFps = this.fpsAccumulator;
				this.fpsAccumulator = 0;
				this.fpsCountStartTime = Shattered.getSystemTime();
			}
			this.iterationLength = Shattered.getSystemTime() - this.iterationStartTime;
			return null;
		}

		public int getCachedFps() {
			return this.cachedFps;
		}

		public double getPrevIterationLength() {
			return this.iterationLength;
		}

		private double calcAndHandleDelta() {
			final long time = Shattered.getSystemTime();
			final double delta = time - this.iterationStartTime;
			this.iterationStartTime = time;
			return delta;
		}

		@FunctionalInterface
		private interface RuntimeTimerExecutor {

			@Nullable
			Throwable execute(@NotNull RuntimeTimer timer);
		}
	}

	@NotNull
	private static Manifest readManifest() {
		try {
			final URL resource = Shattered.class.getClassLoader().getResource(JarFile.MANIFEST_NAME);
			assert resource != null;
			final Manifest manifest = new Manifest(resource.openStream());
			final Attributes attributes = manifest.getMainAttributes();
			if (attributes.getValue("VERSION") != null) {
				return manifest;
			}
			throw new RuntimeException();
		} catch (final Throwable e) {
			final Manifest dummy = new Manifest();
			dummy.getMainAttributes().putValue("VERSION", "-1");
			return dummy;
		}
	}
}