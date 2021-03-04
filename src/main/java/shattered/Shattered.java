package shattered;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.swing.SwingUtilities;
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
import preboot.AnnotationRegistry;
import preboot.BootManager;
import shattered.core.event.EventBus;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.IEventBus;
import shattered.core.event.MessageEvent;
import shattered.lib.Color;
import shattered.lib.ITimerListener;
import shattered.lib.Lazy;
import shattered.lib.ReflectionHelper;
import shattered.lib.Workspace;
import shattered.lib.asset.AssetRegistry;
import shattered.lib.asset.FontGroup;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.FontRendererImpl;
import shattered.lib.gfx.FrameBufferObject;
import shattered.lib.gfx.GLHelper;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gfx.TessellatorImpl;
import shattered.lib.gui.GuiManager;
import shattered.lib.registry.CreateRegistryEvent;
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
	public static final IEventBus SYSTEM_BUS = EventBus.createBus(Shattered.SYSTEM_BUS_NAME);
	public static final Workspace WORKSPACE = Objects.requireNonNull(
			ReflectionHelper.instantiate(Workspace.class, String.class, Shattered.NAME.toLowerCase(Locale.ROOT))
	);

	private static final AtomicBoolean RUNNING = new AtomicBoolean(true);
	private static final HashSet<Timer> TIMERS = new HashSet<>();

	private static Shattered INSTANCE;
	public final Tessellator tessellator;
	public final FontRenderer fontRenderer;
	private final ThreadLoadingScreen loadingScreen;
	private final BootAnimation bootAnimation = new BootAnimation();

	private GuiManager guiManager;

	//Delegate methods
	private Runnable delegateGuiManagerTick;
	private BiConsumer<Tessellator, FontRenderer> delegateGuiManagerRender;
	private Runnable delegateInputPoller;

	@SuppressWarnings("unused")
	public static void start(final String[] args) {
		if (Shattered.DEVELOPER_MODE) {
			final LoggerContext context = (LoggerContext) LogManager.getContext(false);
			final Configuration configuration = context.getConfiguration();
			final LoggerConfig loggerConfig = configuration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
			loggerConfig.setLevel(Level.ALL);
			context.updateLoggers();
		}
		Shattered.LOGGER.info("Starting {} (version: {})!", Shattered.NAME, Shattered.VERSION);
		//Register all automatic EventBus subscribers
		Shattered.LOGGER.debug("Registering automatic EventBus subscribers");
		AnnotationRegistry.getAnnotatedClasses(EventBusSubscriber.class).forEach(listener ->
				EventBus.register(listener, listener.getDeclaredAnnotation(EventBusSubscriber.class).value())
		);
		//Load config database
		Shattered.LOGGER.info("Loading config database!");
		Config.DISPLAY_SIZE.get(); //Force load class
		Shattered.SYSTEM_BUS.post(new MessageEvent("load_config"));
		//Create all registry instances
		Shattered.INSTANCE = new Shattered(args);
		Shattered.INSTANCE.startLoadingScreen();
		Shattered.INSTANCE.startLoading();
		//This call will block the program until a shutdown was requested
		Shattered.INSTANCE.startRuntime();
		Shattered.INSTANCE.cleanup();
		Shattered.LOGGER.info("Goodbye!");
	}

	private Shattered(final String[] args) {
		Shattered.LOGGER.debug("Notifying registry holders for registry creation");
		final CreateRegistryEvent createRegistryEvent = ReflectionHelper.instantiate(CreateRegistryEvent.class);
		assert createRegistryEvent != null;
		Shattered.SYSTEM_BUS.post(createRegistryEvent);

		Shattered.SYSTEM_BUS.post(new MessageEvent("init_glfw"));

		Shattered.LOGGER.debug("Loading static assets");
		StaticAssets.loadAssets();
		Shattered.SYSTEM_BUS.post(new MessageEvent("atlas_stitch"));

		Shattered.LOGGER.debug("Initializing rendering system");
		final Tessellator tessellator = ReflectionHelper.instantiate(TessellatorImpl.class);
		if (tessellator == null) {
			Shattered.crash("Could not initialize Tessellator!");
		}
		this.tessellator = tessellator;
		assert this.tessellator != null;
		final FontRenderer fontRenderer = ReflectionHelper.instantiate(
				FontRendererImpl.class,
				Tessellator.class, this.tessellator,
				Lazy.class, Lazy.of(() -> (FontGroup) AssetRegistry.getAsset(Assets.FONT_DEFAULT))
		);
		if (fontRenderer == null) {
			Shattered.crash("Could not initialize FontRenderer!");
		}
		this.fontRenderer = fontRenderer;
		assert this.fontRenderer != null;

		this.loadingScreen = new ThreadLoadingScreen(this);
	}

	private void startLoadingScreen() {
		((TessellatorImpl) this.tessellator).setShader(StaticAssets.SHADER);
		StaticAssets.SHADER.bind();
		Display.resetLogicalResolution();
		this.loadingScreen.start();
	}

	@SuppressWarnings("unchecked")
	private void startLoading() {
		final long startTime = Shattered.getSystemTime();

		//Load external registries
		Shattered.SYSTEM_BUS.post(new MessageEvent("load_registries"));

		//Load assets
		Shattered.LOGGER.debug("Notifying AssetRegistry for initializing");
		Shattered.SYSTEM_BUS.post(new MessageEvent("init_assets"));

		//Initialize GuiHandler and main menu screen
		Shattered.LOGGER.debug("Initializing gui system");
		final MessageEvent initGuiEvent = new MessageEvent("init_gui");
		Shattered.SYSTEM_BUS.post(initGuiEvent);
		final Supplier<?> initGuiResponse = initGuiEvent.getResponse();
		assert initGuiResponse != null;
		final Object[] initGuiResponseData = (Object[]) initGuiResponse.get();
		this.guiManager = (GuiManager) initGuiResponseData[0];
		this.delegateGuiManagerTick = (Runnable) initGuiResponseData[1];
		this.delegateGuiManagerRender = (BiConsumer<Tessellator, FontRenderer>) initGuiResponseData[2];

		//Setup input handlers
		Shattered.LOGGER.debug("Initializing keyboard/mouse input handler");
		final MessageEvent handleInputSetupEvent = new MessageEvent("input_setup");
		Shattered.SYSTEM_BUS.post(handleInputSetupEvent);
		this.delegateInputPoller = (Runnable) Objects.requireNonNull(handleInputSetupEvent.getResponse()).get();

		//Setup SoundSystem
		Shattered.LOGGER.debug("Initializing SoundSystem");
		Shattered.SYSTEM_BUS.post(new MessageEvent("init_sound_system"));

		//Initialize LuaMachine
		Shattered.LOGGER.debug("Initializing LuaMachine");
		Shattered.SYSTEM_BUS.post(new MessageEvent("init_lua_machine"));

		//Stop loading screen
		this.loadingScreen.tryStop();

		//Stitching textures
		Shattered.LOGGER.debug("Stitching all TextureAtlas subscribers");
		Shattered.SYSTEM_BUS.post(new MessageEvent("atlas_stitch"));

		//Loading audio
		Shattered.LOGGER.debug("Loading all audio into memory");
		Shattered.SYSTEM_BUS.post(new MessageEvent("load_audio"));

		//Freeze all registries
		Shattered.SYSTEM_BUS.post(new MessageEvent("freeze_registries"));

		Shattered.LOGGER.debug("Loading took {} milliseconds!", Shattered.getSystemTime() - startTime);
	}

	private void startRuntime() {
		this.guiManager.openScreen(new ScreenMainMenu());
		final RuntimeTimer timer = new RuntimeTimer(this::runtimeTick, this::runtimeRender, this::runtimeCatchup);
		this.bootAnimation.start();
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
		Shattered.SYSTEM_BUS.post(new MessageEvent("shutdown_glfw"));
	}

	@Nullable
	private Throwable runtimeTick(@NotNull final RuntimeTimer timer) {
		try {
			//Handle input events
			GLFW.glfwPollEvents();
			this.delegateInputPoller.run();

			//Update timers and animations
			this.tickTimers();
			//TODO animations

			if (!this.bootAnimation.isFinished()) {
				return null;
			}

			//Update gui
			this.delegateGuiManagerTick.run();

			return null;
		} catch (final Throwable e) {
			return e;
		}
	}

	private void tickTimers() {
		for (final Timer timer : new ArrayList<>(Shattered.TIMERS)) {
			if (timer.isDone()) {
				Shattered.TIMERS.remove(timer);
			} else {
				timer.tick();
			}
		}
	}

	@Nullable
	private Throwable runtimeRender(@NotNull final RuntimeTimer timer) {
		try {
			FrameBufferObject.get().bind();
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

			//Render boot animation
			if (!this.bootAnimation.isFinished()) {
				this.bootAnimation.render(this.tessellator);
			}

			//Render runtime metrics
			if (Shattered.DEVELOPER_MODE) {
				final StringData metrics = new StringData(
						"FPS: " + timer.getCachedFps() + " TickLength (ms): " + String.format("%.2f", timer.getPrevIterationLength()),
						Color.YELLOW
				).localize(false);
				this.fontRenderer.setFont(Assets.FONT_SIMPLE);
				this.fontRenderer.setFontSize(24);
				final int width = this.fontRenderer.getWidth(metrics);
				final int height = this.fontRenderer.getHeight(metrics);
				this.tessellator.drawQuick(0, 0, width, height, Color.BLACK);
				this.fontRenderer.writeQuick(2, 0, metrics);
				this.fontRenderer.revertFontSize();
				this.fontRenderer.resetFont();
			}
			FrameBufferObject.get().render();
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

	@NotNull
	public GuiManager getGuiManager() {
		return this.guiManager;
	}

	public static Shattered getInstance() {
		return Shattered.INSTANCE;
	}

	public static boolean isRunning() {
		return Shattered.RUNNING.get();
	}

	public static long getSystemTime() {
		return glfwGetTimerValue() * 1000 / glfwGetTimerFrequency();
	}

	@NotNull
	public static Timer addTimer(final int tickRate, @NotNull final ITimerListener listener) {
		return Shattered.addTimer(tickRate, 1, listener);
	}

	@NotNull
	public static Timer addTimer(final int tickRate, final int maxTicks, @NotNull final ITimerListener listener) {
		return Shattered.addTimerInternal(tickRate, maxTicks, listener, false);
	}

	@NotNull
	public static Timer addTimerRepeating(final int tickRate, @NotNull final ITimerListener listener) {
		return Shattered.addTimerInternal(tickRate, 1, listener, true);
	}

	private static Timer addTimerInternal(final int tickRate, final int maxTicks, @NotNull final ITimerListener listener, final boolean repeating) {
		if (tickRate <= 0) {
			throw new IllegalArgumentException("TickRate should be > 0");
		}
		if (!repeating && maxTicks <= 0) {
			throw new IllegalArgumentException("MaxTicks should be > 0 or Repeating should be true");
		}
		final Timer result = new Timer(listener, tickRate, repeating ? 1 : maxTicks, repeating);
		Shattered.TIMERS.add(result);
		return result;
	}

	public static void removeTimer(@NotNull final Timer timer) {
		Shattered.TIMERS.remove(timer);
	}

	public static void crash(@NotNull final String reason) {
		try {
			Shattered.getInstance().cleanup();
		} catch (final Throwable ignored) {
		}
		Shattered.LOGGER.fatal(reason);
		SwingUtilities.invokeLater(() -> CrashWindow.create(reason));
		try {
			Thread.currentThread().join();
			Runtime.getRuntime().halt(-1);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
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