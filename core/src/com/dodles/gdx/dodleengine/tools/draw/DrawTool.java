package com.dodles.gdx.dodleengine.tools.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.brushes.Brush;
import com.dodles.gdx.dodleengine.brushes.BrushConfig;
import com.dodles.gdx.dodleengine.brushes.BrushRegistry;
import com.dodles.gdx.dodleengine.brushes.RulerMode;
import com.dodles.gdx.dodleengine.brushes.SelectBrushRowEditorView;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.CompoundCommand;
import com.dodles.gdx.dodleengine.commands.DrawStrokeCommand;
import com.dodles.gdx.dodleengine.commands.MergeCommand;
import com.dodles.gdx.dodleengine.editor.OkCancelStackFrame;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.editor.full.strokeconfigrow.FullEditorSelectStrokeConfigRow;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.input.GlobalCameraZoomHandler;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.input.InteractionData;
import com.dodles.gdx.dodleengine.input.SelectActorInputHandler;
import com.dodles.gdx.dodleengine.input.TouchInputHandler;
import com.dodles.gdx.dodleengine.input.TwoFingerGlobalCameraInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolHelper;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The "Draw" tool handles drawing of individual brush strokes.
 */
@PerDodleEngine
public class DrawTool extends AbstractTool implements Tool, TouchInputHandler {

    //region Properties & Variables

    //Constants
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "DRAW";
    public static final String ACTIVATED_COLOR = FullEditorViewState.TOOLBAR_TOP_ACTIVATED_COLOR;

    // Subsystem References
    private final BrushRegistry brushRegistry;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final EngineEventManager eventManager;
    private final EventBus eventBus;
    private final ObjectManager objectManager;
    private final OkCancelStackManager okCancelStack;
    private final ToolHelper toolHelper;
    private final ArrayList<InputHandler> inputHandlers = new ArrayList<InputHandler>();
    private final ToolRegistry toolRegistry;
    private final SelectActorInputHandler saih;
    private final DrawTool self;

    // Local Variables
    private EngineEventListener listener;
    private DrawStrokeCommand drawStrokeCommand;
    private DrawStrokeCommand drawLineCommand;
    private MergeCommand newGroupMergeCommand;

    private Vector2 startPoint;
    private Vector2 endPoint;
    private DodlesActor actor;
    private Brush activeBrush;
    private Vector2 lastMovePoint;
    private long lastMoveTime;
    private EngineEventListener preDrawEventListener;
    private EventSubscriber rulerSwitchListener;
    private boolean lineFlag = false;
    private String initialLayer;
    private boolean isDeactivating = false;

    //endregion Properties and Variables

    //region Constructor

    @Inject
    public DrawTool(
            AssetProvider assetProvider,
            BrushRegistry brushRegistry,
            CommandFactory commandFactory,
            CommandManager commandManager,
            EngineEventManager eventManager,
            EventBus eventBus,
            FullEditorViewState fullViewState,
            FullEditorSelectStrokeConfigRow strokeConfigRow,
            SelectBrushRowEditorView brushRow,
            ObjectManager pObjectManager,
            OkCancelStackManager pOkCancelStack,
            ToolHelper toolHelper,
            ToolRegistry pToolRegistry,
            TwoFingerGlobalCameraInputHandler caih,
            SelectActorInputHandler psaih,
            GlobalCameraZoomHandler gczh
    ) {
        super(assetProvider);

        this.brushRegistry = brushRegistry;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.eventManager = eventManager;
        this.eventBus = eventBus;
        this.objectManager = pObjectManager;
        this.okCancelStack = pOkCancelStack;
        this.toolHelper = toolHelper;
        this.toolRegistry = pToolRegistry;
        this.saih = psaih;
        this.self = this;

        strokeConfigRow.setShowFill(false);
        strokeConfigRow.setShowLine(true);

        toolRegistry.registerTool(this);
        fullViewState.registerRow1View(TOOL_NAME, strokeConfigRow);
        fullViewState.registerRow2View(TOOL_NAME, brushRow);

        inputHandlers.add(caih);
        inputHandlers.add(gczh);
        inputHandlers.add(saih);
    }

    //endregion Constructor

    //region Tool UI Related Functions - TODO: refactor Tool class & remove! - CAD 2017.09.15

    @Override
    public final String getName() {
        return TOOL_NAME;
    }

    @Override
    public final String getActivatedColor() {
        return ACTIVATED_COLOR;
    };

    @Override
    public final int getRow() {
        return 1;
    }

    @Override
    public final int getOrder() {
        return 1;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 3, 3, 69, 69);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String icon) {
        return getToolBarIconsTextureAtlas().findRegion("brush_" + icon);
    }

    @Override
    public final String getButtonStyleName() {
        return "brush";
    }

    //endregion Tool UI Related Functions

    //region Public API

    @Override
    public final void onActivation() {

        objectManager.clearSelectedActors();
        
        // Manage Ok / Cancel Stack
        initialLayer = okCancelStack.getLayerID();
        okCancelStack.push(new OkCancelStackFrame("Draw", true, false) {
            @Override
            public void execute() {
                // Deactivate the tool if its not already deactivating (ie, the user pressed the OK button)
                if (!isDeactivating) {
                    toolRegistry.deactivateTool(TOOL_NAME);
                }
            }
        });
        okCancelStack.nextLayer();

        preDrawEventListener = new EngineEventListener(EngineEventType.ENGINE_PRE_DRAW) {
            @Override
            public void listen(EngineEventData data) {
                if (drawStrokeCommand != null) {
                    if (TimeUtils.timeSinceMillis(lastMoveTime) >= 50) {
                        drawStrokeCommand.updateLiveDrawing(lastMovePoint, true);
                    }
                }
            }
        };

        listener = new EngineEventListener(EngineEventType.BRUSH_CHANGED) {
            @Override
            public void listen(EngineEventData data) {
                if (data.getFirstStringParam() != null) {
                    if (!inputHandlers.contains(self)) {
                        inputHandlers.add(self);
                    }
                    if (inputHandlers.contains(saih)) {
                        removeInputHandler(saih);
                    }
                } else {
                    if (inputHandlers.contains(self)) {
                        removeInputHandler(self);
                    }
                    if (!inputHandlers.contains(saih)) {
                        inputHandlers.add(saih);
                    }

                }
            }
        };

        rulerSwitchListener = new EventSubscriber(EventTopic.EDITOR) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                switch (eventType) {
                    case CHANGE_BRUSH_RULER_MODE:
                        toggleLine();
                        break;
                }
            }
        };

        eventManager.addListener(listener);
        eventManager.addListener(preDrawEventListener);
        eventBus.addSubscriber(rulerSwitchListener);
        inputHandlers.add(saih);
    }

    @Override
    public final void onDeactivation() {
        isDeactivating = true;
        eventManager.removeListener(listener);
        eventManager.removeListener(preDrawEventListener);
        eventBus.removeSubscriber(rulerSwitchListener);
        okCancelStack.popThroughLayer(initialLayer, false);
        isDeactivating = false;
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return inputHandlers;
    }

    /**
     * Toggle between free draw and line draw.
     */
    public final void toggleLine() {
        lineFlag = !lineFlag;
        activeBrush = null;
        drawLineCommand = null;
        drawStrokeCommand = null;
    }

    /**
     * is this tool in line mode?
     */
    public final boolean isLineFlag() {
        return lineFlag;
    }

    //endregion Public API


    //region TouchInputHandler Implementation

    @Override
    public final void handleTouchStart(InteractionData startData, int pointer) {
        Gdx.app.log("DrawTool", "handleTouchStart: " + startData.getNumPointers());
        activeBrush = brushRegistry.getActiveBrush();

        if (activeBrush == null) {
            return;
        }

        // Reset activeBrush to make sure everything's OK with the stroke config...
        brushRegistry.setActiveBrush(activeBrush.getName());

        if (lineFlag) {
            drawLineCommand = (DrawStrokeCommand) activeBrush.createCommand();

            actor = objectManager.selectLeaf(startData.getDodlePoint());
            if (actor != null && actor instanceof Shape && ((Shape) actor).getCustomConfig() instanceof BrushConfig) {
                BrushConfig config = (BrushConfig) ((Shape) actor).getCustomConfig();
                if (config.getRulerMode() != RulerMode.NONE) {
                    drawLineCommand.initLineMode(actor.getName(), actor.getParentDodlesViewGroup(), brushRegistry.getBrush(config.getBrush()), RulerMode.ARC, ((Shape) actor).getStrokeConfig());
                    ArrayList<Vector2> points = config.getPoints();
                    startPoint = points.get(0);
                    endPoint = points.get(2);

                    drawLineCommand.update(points);
                    drawLineCommand.execute();
                } else {
                    drawLineCommand = null;
                }
            } else {
                if (drawLineCommand.shouldAddToObjects()) {
                    newGroupMergeCommand = toolHelper.possiblyPushCloseNewObjectGroup(TOOL_NAME, "stroke");
                }

                drawLineCommand.initLineMode(activeBrush);
                startPoint = startData.getDodlePoint();
            }
        } else {
            drawStrokeCommand = activeBrush.createCommand();

            lastMovePoint = startData.getDodlePoint();
            lastMoveTime = TimeUtils.millis();

            drawStrokeCommand.startLiveDrawing(lastMovePoint);
        }
    }

    @Override
    public final void handleTouchMove(InteractionData moveData, int pointer) {
        if (lineFlag) {
            if (drawLineCommand != null) {
                final Vector2 midPoint;

                if (actor == null) {
                    endPoint = moveData.getDodlePoint();
                    midPoint = new Vector2((startPoint.x + endPoint.x) / 2, (startPoint.y + endPoint.y) / 2);
                } else {
                    midPoint = moveData.getDodlePoint();
                }

                ArrayList<Vector2> points = new ArrayList<Vector2>() {
                    {
                        add(startPoint);
                        add(midPoint);
                        add(endPoint);
                    }
                };

                drawLineCommand.undo();

                drawLineCommand.update(points);
                drawLineCommand.execute();

            }
        } else {
            if (drawStrokeCommand != null) {
                lastMovePoint = moveData.getDodlePoint();
                lastMoveTime = TimeUtils.millis();

                drawStrokeCommand.updateLiveDrawing(lastMovePoint, false);
            }
        }
    }

    @Override
    public final void handleTouchEnd(InteractionData endData, int pointer) {
        if (lineFlag) {
            if (drawLineCommand != null) {
                Command commandToAdd = drawLineCommand;

                if (newGroupMergeCommand != null) {
                    CompoundCommand cc = (CompoundCommand) commandFactory.createCommand(CompoundCommand.COMMAND_NAME);
                    ArrayList commands = new ArrayList();
                    commands.add(newGroupMergeCommand);
                    commands.add(drawLineCommand);
                    cc.init(commands);
                    commandToAdd = cc;
                }

                commandManager.add(commandToAdd);
            }
        } else {
            if (drawStrokeCommand != null) {
                if (drawStrokeCommand.shouldAddToObjects()) {
                    newGroupMergeCommand = this.toolHelper.possiblyPushCloseNewObjectGroup(TOOL_NAME, "Brush");
                }

                if (drawStrokeCommand.endLiveDrawing(lastMovePoint)) {
                    Command commandToAdd = drawStrokeCommand;

                    if (newGroupMergeCommand != null) {
                        CompoundCommand cc = (CompoundCommand) commandFactory.createCommand(CompoundCommand.COMMAND_NAME);
                        ArrayList commands = new ArrayList();
                        commands.add(newGroupMergeCommand);
                        commands.add(drawStrokeCommand);
                        cc.init(commands);
                        commandToAdd = cc;
                    }

                    commandManager.add(commandToAdd);
                } else if (newGroupMergeCommand != null) {
                    newGroupMergeCommand.undo();
                    okCancelStack.pop(true);
                    objectManager.setNewObjectGroup(null);
                }

                drawStrokeCommand = null;
            }
        }

        newGroupMergeCommand = null;
    }

    @Override
    public final void handleTouchCancel() {
        if (drawStrokeCommand != null) {
            drawStrokeCommand.undo();
            drawStrokeCommand = null;
        }

        if (drawLineCommand != null) {
            drawLineCommand.undo();
            drawLineCommand = null;
        }
    }

    //endregion TouchInputHandler Implementation

    //region Private Helper Functions

    private void removeInputHandler(InputHandler handler) {
        Iterator<InputHandler> iter = inputHandlers.iterator();

        while (iter.hasNext()) {
            InputHandler ih = iter.next();
            if (ih.equals(handler)) {
                iter.remove();
            }
        }
    }

    //endregion Private Helper Functions
}
