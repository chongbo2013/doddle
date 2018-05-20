package com.dodles.gdx.dodleengine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.BitmapFontAssets;
import com.dodles.gdx.dodleengine.assets.FontRenderer;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.editor.DensityManager;
import com.dodles.gdx.dodleengine.editor.EditorInterfaceManager;
import com.dodles.gdx.dodleengine.editor.EditorStateManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.input.DodleEngineGestureListener;
import com.dodles.gdx.dodleengine.logging.DodlesFPSLogger;
import com.dodles.gdx.dodleengine.scenegraph.BatchShapeRasterizer;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesSpriteBatch;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.dodles.gdx.dodleengine.util.ScreenGrabber;
import com.syl.test43.Toast;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * The core DodleEngine - handles rendering of Dodles and manages interaction and events.
 */
public class DodleEngine extends ApplicationAdapter {
    public static final float DODLE_SIDE = 750;

    private static Logger logger;

    private final AnimationManager animationManager;
    private final AnimationTool animationTool;
    private final BatchShapeRasterizer bsRasterizer;
    private final CameraManager cameraManager;
    private final CommandManager commandManager;
    private final DodleEngineGestureListener gestureListener;
    private final DodleEngineConfig engineConfig;
    private final EngineEventManager eventManager;
    private final EventBus eventBus;
    private final ObjectManager objectManager;
    private final DodleStageManager stageManager;
    private final ScreenGrabber screenGrabber;
    private final StateManager stateManager;
    private final AssetProvider assetProvider;
    private final OkCancelStackManager okCancelStack;
    private final ArrayList<StageRenderer> stageRenderers = new ArrayList<StageRenderer>();
    private final InputMultiplexer multiplexer = new InputMultiplexer();
    private final EditorStateManager editorStateManager;
    private final ToolRegistry toolRegistry;

    private SpriteBatch spriteBatch;
    private boolean initialized = false;
    private boolean screenshotRequested = false;
    private int width = -1;
    private int height = -1;

    private Toast toast;

    // Profiling
    private static final boolean ENABLE_FPS_LOGGING = true;
    private static final boolean LOG_FPS = false;
    private static final boolean ENABLE_PROFILING = true;
    private static final boolean DISPLAY_PROFILING = true;
    private DodlesFPSLogger fpsLogger;
    private FontRenderer profilingFont;


    @Inject
    public DodleEngine(
            AnimationManager animationManager,
            AnimationTool animationTool,
            BatchShapeRasterizer bsRasterizer,
            CameraManager cameraManager,
            CommandManager commandManager,
            DodleEngineGestureListener gestureListener,
            DodleEngineConfig engineConfig,
            EditorInterfaceManager interfaceManager,
            EngineEventManager eventManager,
            EventBus eventBus,
            ObjectManager objectManager,
            DodleStageManager stageManager,
            ScreenGrabber screenGrabber,
            StateManager stateManager,
            AssetProvider assetProvider,
            EagerInjector eagerInjector,
            OkCancelStackManager okCancelStackManager,
            EditorStateManager editorStateManager,
            ToolRegistry toolRegistry
    ) {
        this.animationManager = animationManager;
        this.animationTool = animationTool;
        this.bsRasterizer = bsRasterizer;
        this.cameraManager = cameraManager;
        this.commandManager = commandManager;
        this.eventManager = eventManager;
        this.eventBus = eventBus;
        this.gestureListener = gestureListener;
        this.engineConfig = engineConfig;
        this.objectManager = objectManager;
        this.screenGrabber = screenGrabber;
        this.stageManager = stageManager;
        this.stateManager = stateManager;
        this.assetProvider = assetProvider;
        this.okCancelStack = okCancelStackManager;
        this.editorStateManager = editorStateManager;
        this.toolRegistry = toolRegistry;

        stageRenderers.add(stageManager);
        stageRenderers.add(interfaceManager);
    }

    /**
     * Sets the singleton logger to use for all dodle engines.
     */
    public static void setLogger(Logger pLogger) {
        logger = pLogger;
    }

    /**
     * Gets the singleton logger to use for all dodle engines.
     */
    public static Logger getLogger() {
        if (logger == null) {
            logger = new DefaultLogger();
        }

        return logger;
    }

    @Override
    public final void create() {

        System.out.println("DodleEngine - create() started");

        // Global settings...

        //TODO causes trouble with build?
//        Pixmap.setFilter(Pixmap.Filter.BiLinear);
//        Pixmap.setBlending(Pixmap.Blending.None);

        if (width < 0) {
            throw new GdxRuntimeException("Please call resize right after creating the engine!");
        }

        assetProvider.loadAssets();

        spriteBatch = new DodlesSpriteBatch();

        for (StageRenderer renderer : stageRenderers) {
            renderer.initStage(spriteBatch, width, height);
        }

        eventManager.addListener(new EngineEventListener(EngineEventType.ENGINE_CONFIG_CHANGED) {
            @Override
            public void listen(EngineEventData data) {
                resize(width, height);
            }
        });

        /*eventManager.addListener(new EngineEventListener(EngineEventType.SHOW_TOAST_MESSAGE) {
            @Override
            public void listen(EngineEventData data) {
                String message = data.getFirstStringParam();
                if (message != null && message.length() > 0) {
                    addToastMessage(message);
                }
            }
        });*/

        for (int i = stageRenderers.size() - 1; i >= 0; i--) {
            multiplexer.addProcessor(stageRenderers.get(i).getStage());
        }

        multiplexer.addProcessor(gestureListener);
        multiplexer.addProcessor(new GestureDetector(gestureListener));
        Gdx.input.setInputProcessor(multiplexer);

        toast = new Toast(5, 5);

        // Set up profiling
        fpsLogger = new DodlesFPSLogger(ENABLE_FPS_LOGGING, LOG_FPS);
        if (ENABLE_PROFILING) {
            GLProfiler.enable();
            if (DISPLAY_PROFILING) {
                profilingFont = new FontRenderer(assetProvider.getFont(BitmapFontAssets.LUCIDA_CONSOLE));
                Color fpsColor = Color.BLUE;
                profilingFont.setColor(fpsColor);
                fpsColor.a = 0.5f;
                profilingFont.setFontSize(8 * DensityManager.getDensity().getScale());
                GLProfiler.enable();
            }
        }

        // Finalize initialization and notify listeners
        initialized = true;
        eventBus.publish(EventTopic.DEFAULT, EventType.ENGINE_INITIALIZED);

        System.out.println("DodleEngine - create() ended");
    }

    /**
     * Returns a value indicating whether the DodleEngine is initialized.
     */
    public final boolean isInitialized() {
        return initialized;
    }

    /**
     * Returns the engine configuration.
     */
    public final DodleEngineConfig getEngineConfig() {
        return engineConfig;
    }

    @Override
    public final void resize(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
        System.out.println("W: " + width + " H: " + height);

        for (StageRenderer renderer : stageRenderers) {
            renderer.resize(newWidth, newHeight);
        }
    }

    @Override
    public final void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        float deltaTime = Gdx.graphics.getDeltaTime();
        eventManager.fireEvent(EngineEventType.ENGINE_PRE_DRAW);

        if (!bsRasterizer.isOverloaded() && !engineConfig.hasOption(DodleEngineConfig.Options.RENDER_ANIMATION)) {
            animationManager.act(deltaTime);
        }

        for (StageRenderer renderer : stageRenderers) {
            // Set ENABLE_PROFILING to true to access GlProfiler metrics...
            GLProfiler.reset();

            if (renderer.actWhenOverloaded() || (!bsRasterizer.isOverloaded() && !engineConfig.hasOption(DodleEngineConfig.Options.RENDER_ANIMATION))) {
                renderer.act(deltaTime);
            }

            int preSwitches = GLProfiler.shaderSwitches;
            int preBindings = GLProfiler.textureBindings;
            renderer.draw();

            if (renderer instanceof DodleStageManager) {
                int postSwitches = GLProfiler.shaderSwitches;
                int postBindings = GLProfiler.textureBindings;
                int deltaSwitches = postSwitches - preSwitches;
                int deltaBindings = postBindings - preBindings;
                int calls = GLProfiler.calls;
                int drawCalls = GLProfiler.drawCalls;
                int bindings = GLProfiler.textureBindings;
                com.badlogic.gdx.math.FloatCounter vertices = GLProfiler.vertexCount;
            }
        }

        // Profiling
        int fps = fpsLogger.updateFPS();
        if (DISPLAY_PROFILING) {
            float fontX = Gdx.graphics.getWidth() - 75 * DensityManager.getScale();
            float fontY = Gdx.graphics.getHeight() - 5 * DensityManager.getScale();
            Tool tool = toolRegistry.getActiveTool();
            String profilingText = "DEN: " + DensityManager.getName()
                    + "\nFPS: " + fps
                    + "\nC  : " + GLProfiler.calls
                    + "\nDC : " + GLProfiler.drawCalls
                    + "\nTB : " + GLProfiler.textureBindings
                    + "\n" + (tool == null ? "(no tool)" : tool.getName())
                    + "\n" + animationTool.getToolState().toString();
            profilingFont.setText(profilingText, fontX, fontY);
        }

        // Sprite batch rendering
        spriteBatch.begin();
        spriteBatch.setColor(Color.WHITE);
        if (!screenshotRequested) {
            if (DISPLAY_PROFILING) {
                profilingFont.draw(spriteBatch);
            }
            toast.toaster(spriteBatch);
        }
        spriteBatch.end();

        // Trigger post draw events
        eventManager.fireEvent(EngineEventType.ENGINE_POST_DRAW);
    }

    /**
     * Returns the animation manager.
     */
    public final AnimationManager getAnimationManager() {
        return animationManager;
    }

    /**
     * Returns the event manager.
     */
    public final EngineEventManager getEventManager() {
        return eventManager;
    }

    /**
     * Returns the event bus.
     */
    public final EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Returns the stage manager.
     */
    public final DodleStageManager getStageManager() {
        return stageManager;
    }

    /**
     * Returns the state manager.
     */
    public final StateManager getStateManager() {
        return stateManager;
    }

    /**
     * Returns the object manager.
     */
    public final ObjectManager getObjectManager() {
        return objectManager;
    }

    /**
     * Returns the okCancelStack manager.
     */
    public final OkCancelStackManager getOkCancelStack() {
        return okCancelStack;
    }

    /**
     * Adds a toast message to the screen.
     */
    public final void addToastMessage(final String message) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                toast.makeText(message, 0, Toast.STYLE.NORMAL, Toast.TEXT_POS.middle_right, Toast.TEXT_POS.middle_up, Toast.LONG);
            }
        });
    }

    @Override
    public final void dispose() {
        stageManager.dispose();
    }

    /**
     * Prepares the stage to take a screenshot.
     */
    public final void takeScreenshot(final ScreenshotCallback callback) {
        if (!engineConfig.hasOption(DodleEngineConfig.Options.RENDER_ANIMATION)) {
            objectManager.setActiveScene(objectManager.getFirstScene().getName());
        }
        final Rectangle cropBounds = cameraManager.resetGlobalViewport();
        screenshotRequested = true;

        eventManager.addListener(new EngineEventListener(EngineEventType.ENGINE_POST_DRAW) {
            @Override
            public void listen(EngineEventData data) {
                if (!bsRasterizer.isOverloaded()) {
                    eventManager.removeListener(this);
                    callback.call(screenGrabber.takeBase64Screenshot(cropBounds));
                    screenshotRequested = false;
                }
            }
        });
    }

    /**
     * Callback interface for creating a screenshot.
     */
    public interface ScreenshotCallback {
        /**
         * Called when the screenshot data is ready.
         */
        void call(String screenshotData);
    }
}
