package com.dodles.gdx.dodleengine.tools.geometry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.CompoundCommand;
import com.dodles.gdx.dodleengine.commands.DrawCustomGeometryCommand;
import com.dodles.gdx.dodleengine.commands.MergeCommand;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.overlays.ResizeGeometryOverlay;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.input.GlobalCameraZoomHandler;
import com.dodles.gdx.dodleengine.input.InteractionData;
import com.dodles.gdx.dodleengine.input.TouchInputHandler;
import com.dodles.gdx.dodleengine.input.TwoFingerGlobalCameraInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.tools.ToolHelper;

import javax.inject.Inject;
import java.util.ArrayList;

@PerDodleEngine
public class CustomGeometrySubtool extends AbstractGeometrySubtool implements TouchInputHandler {

    //region Properties and Variables

    // Subsystem References
    private final EngineEventManager eventManager;
    private final ToolHelper toolHelper;

    // Event Handlers
    private EngineEventListener preDrawEventListener;

    // Drawing Variables
    private DrawCustomGeometryCommand drawCommand;
    private MergeCommand newGroupMergeCommand;
    private String initialLayer;
    private Vector2 lastMovePoint;
    private long lastMoveTime;

    //endregion Properties and Variables

    //region Constructor

    @Inject
    public CustomGeometrySubtool(
            AnimationManager animationManager,
            AssetProvider assetProvider,
            CommandFactory commandFactory,
            CommandManager commandManager,
            DodleStageManager dodlesStageManager,
            EditorState editorState,
            EngineEventManager eventManager,
            EventBus eventBus,
            GeometryRegistry geometryRegistry,
            ObjectManager objectManager,
            OkCancelStackManager okCancelStackManager,
            ToolHelper toolHelper,
            GlobalCameraZoomHandler gczh,
            ResizeGeometryOverlay resizeGeometryOverlay,
            TwoFingerGlobalCameraInputHandler tfgcaih
    ) {
        initializeBaseGeometrySubtool(
                animationManager,
                assetProvider,
                commandFactory,
                commandManager,
                dodlesStageManager,
                editorState,
                eventBus,
                geometryRegistry,
                objectManager,
                okCancelStackManager,
                resizeGeometryOverlay);

        // Store subsystem references
        this.eventManager = eventManager;
        this.toolHelper = toolHelper;

        // Input Handlers
        inputHandlers.add(this);
        inputHandlers.add(gczh);
        inputHandlers.add(tfgcaih);

        // Event Handlers
        preDrawEventListener = new EngineEventListener(EngineEventType.ENGINE_PRE_DRAW) {
            @Override
            public void listen(EngineEventData data) {
                if (drawCommand != null) {
                    if (TimeUtils.timeSinceMillis(lastMoveTime) >= 50) {
                        drawCommand.updateLiveDrawing(lastMovePoint, true);
                    }
                }
            }
        };
    }

    //region Constructor


    //region Public API

    @Override
    public final void onActivation() {
        System.out.println("CustomGeometrySubtool::onActivation");
        initialLayer = okCancelStackManager.getLayerID();
        eventManager.addListener(preDrawEventListener);
    }

    @Override
    public final void onDeactivation() {
        System.out.println("CustomGeometrySubtool::onDeactivation");
        eventManager.removeListener(preDrawEventListener);
        okCancelStackManager.popThroughLayer(initialLayer, false);
    }

    @Override
    public final Shape getActiveShape() {
        // TODO:
        return null;
    }

    @Override
    public final void regenerateShape() {
        // TODO:
        System.out.println("CustomGeometrySubtool::regenerateShape() - not implemented!");
    }

    //endregion Public API


    //region TouchInputHandler Implementation

    @Override
    public void handleTouchStart(InteractionData startData, int pointer) {
        Gdx.app.log("CustomGeometrySubtool", "handleTouchStart: " + startData.getNumPointers());

        drawCommand = (DrawCustomGeometryCommand) commandFactory.createCommand(DrawCustomGeometryCommand.COMMAND_NAME);
        lastMovePoint = startData.getDodlePoint();
        lastMoveTime = TimeUtils.millis();
        drawCommand.startLiveDrawing(lastMovePoint);
    }

    @Override
    public void handleTouchMove(InteractionData moveData, int pointer) {
        if (drawCommand != null) {
            lastMovePoint = moveData.getDodlePoint();
            lastMoveTime = TimeUtils.millis();
            drawCommand.updateLiveDrawing(lastMovePoint, false);
        }
    }

    @Override
    public void handleTouchEnd(InteractionData endData, int pointer) {
        if (drawCommand != null) {
            if (drawCommand.shouldAddToObjects()) {
                newGroupMergeCommand = this.toolHelper.possiblyPushCloseNewObjectGroup("", "Brush");
            }

            if (drawCommand.endLiveDrawing(lastMovePoint)) {
                Command commandToAdd = drawCommand;

                if (newGroupMergeCommand != null) {
                    CompoundCommand cc = (CompoundCommand) commandFactory.createCommand(CompoundCommand.COMMAND_NAME);
                    ArrayList commands = new ArrayList();
                    commands.add(newGroupMergeCommand);
                    commands.add(drawCommand);
                    cc.init(commands);
                    commandToAdd = cc;
                }

                commandManager.add(commandToAdd);
            } else if (newGroupMergeCommand != null) {
                newGroupMergeCommand.undo();
                okCancelStackManager.pop(true);
                objectManager.setNewObjectGroup(null);
            }

            drawCommand = null;
        }

        newGroupMergeCommand = null;
    }

    @Override
    public void handleTouchCancel() {
        if (drawCommand != null) {
            drawCommand.undo();
            drawCommand = null;
        }
    }

    //endregion TouchInputHandler Implementation

}
