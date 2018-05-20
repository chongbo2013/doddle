package com.dodles.gdx.dodleengine.tools.geometry;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.CameraManager;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.CompoundCommand;
import com.dodles.gdx.dodleengine.commands.DrawGeometryCommand;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.overlays.ResizeGeometryOverlay;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.geometry.Geometry;
import com.dodles.gdx.dodleengine.geometry.GeometryConfig;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.geometry.GeometryRenderState;
import com.dodles.gdx.dodleengine.geometry.HandleHook;
import com.dodles.gdx.dodleengine.input.InteractionData;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler.ActorProvider;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler.PanEventHandler;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler.RotateEventHandler;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler.ZoomEventHandler;
import com.dodles.gdx.dodleengine.input.TouchInputHandler;
import com.dodles.gdx.dodleengine.input.TwoFingerGlobalCameraInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Shape;

import javax.inject.Inject;

@PerDodleEngine
public class StandardGeometrySubtool extends AbstractGeometrySubtool implements TouchInputHandler {

    //region Properties and Variables

    // Subsystem References
    private final CameraManager cameraManager;
    private final EventBus eventBus;
    private final EngineEventManager eventManager;
    private final PanRotateZoomActorInputHandler przaih;

    // Local Variables
    private HandleHook dragHandle;
    private EngineEventListener strokeConfigListener;
    private EventSubscriber regenerateShapeSubscriber;

    //endregion Properties and Variables

    //region Constructor

    @Inject
    public StandardGeometrySubtool(
            AnimationManager animationManager,
            AssetProvider assetProvider,
            CameraManager cameraManager,
            CommandFactory commandFactory,
            CommandManager commandManager,
            DodleStageManager dodlesStageManager,
            EditorState editorState,
            EventBus eventBus,
            EngineEventManager eventManager,
            GeometryRegistry geometryRegistry,
            ObjectManager objectManager,
            OkCancelStackManager okCancelStackManager,
            PanRotateZoomActorInputHandler przaih,
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
        this.animationManager = animationManager;
        this.assetProvider = assetProvider;
        this.cameraManager = cameraManager;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.dodlesStageManager = dodlesStageManager;
        this.editorState = editorState;
        this.eventBus = eventBus;
        this.eventManager = eventManager;
        this.geometryRegistry = geometryRegistry;
        this.objectManager = objectManager;
        this.okCancelStackManager = okCancelStackManager;
        this.przaih = przaih;
        this.resizeGeometryOverlay = resizeGeometryOverlay;

        // Initialize Input Handling
        przaih.initialize(new ActorProvider() {
            @Override
            public DodlesActor getActor() {
                return getActiveShape();
            }
        }, new PanEventHandler() {
            @Override
            public void onPan(Vector2 delta) {
                if (command != null) {
                    GeometryConfig config = ((GeometryConfig) command.getShape().getCustomConfig());
                    config.getPoint().x += delta.x;
                    config.getPoint().y += delta.y;
                    regenerateShape();
                }
            }
        }, new RotateEventHandler() {
            @Override
            public void onRotate(float delta) {
                if (command != null) {
                    GeometryConfig config = ((GeometryConfig) command.getShape().getCustomConfig());
                    config.setRotation(config.getRotation() + delta);
                    regenerateShape();
                }
            }
        }, new ZoomEventHandler() {
            @Override
            public void onZoom(float delta) {
                if (command != null) {
                    GeometryConfig config = ((GeometryConfig) command.getShape().getCustomConfig());
                    config.setSize(config.getSize() * delta);
                    regenerateShape();
                }
            }
        });
        inputHandlers.add(przaih);
        inputHandlers.add(tfgcaih);
        inputHandlers.add(this);

        // Create Event Handlers
        strokeConfigListener = new EngineEventListener(EngineEventType.STROKE_CONFIG_CHANGED) {
            @Override
            public void listen(EngineEventData data) {
                regenerateShape();
            }
        };
        regenerateShapeSubscriber = new EventSubscriber(EventTopic.DEFAULT) {
            @Override
            public void listen(EventTopic topic, EventType eventType, EventData data) {
                if (EventType.REGENERATE_SHAPE.equals(eventType)) {
                    regenerateShape();
                }
            }
        };
    }

    //endregion Constructor

    //region Public API

    @Override
    public final void onActivation() {
        eventManager.addListener(strokeConfigListener);
        eventBus.addSubscriber(regenerateShapeSubscriber);
    }

    @Override
    public final void onDeactivation() {
        eventManager.removeListener(strokeConfigListener);
        eventBus.removeSubscriber(regenerateShapeSubscriber);
        resizeGeometryOverlay.setShape(null);
        dodlesStageManager.updateStateUi();
    }

    public final Shape getActiveShape() {
        Shape shape = null;
        if (command != null) {
            shape = command.getShape();
        }
        return shape;
    }

    public final void regenerateShape() {
        Shape shape = getActiveShape();
        if (shape != null) {
            shape.regenerate();
            dodlesStageManager.updateStateUi();
        }
    }

    //endregion Public API

    //region TouchInputHandler Implementation

    @Override
    public final void handleTouchStart(InteractionData startData, int pointer) {
        boolean isEdit = geometryRegistry.getActiveGeometry() == null;

        if (isEdit) {
            Shape selectedChildShape = null;
            DodlesActor selectedChild = objectManager.selectLeaf(startData.getDodlePoint());
            if (selectedChild != null) {
                if (selectedChild != null && selectedChild instanceof Shape) {
                    selectedChildShape = (Shape) selectedChild;
                }
            }

            if (selectedChildShape != null && selectedChildShape.getCustomConfig() instanceof GeometryConfig) {
                GeometryConfig config = (GeometryConfig) selectedChildShape.getCustomConfig();
                geometryRegistry.setActiveGeometry(config.getType(), config, selectedChildShape.getStrokeConfig());

                command = (DrawGeometryCommand) commandFactory.createCommand(DrawGeometryCommand.COMMAND_NAME);
                command.init(
                        selectedChild.getName(),
                        null,
                        selectedChildShape.getStrokeConfig(),
                        geometryRegistry.getActiveGeometry(),
                        config
                );
                command.execute();

                resizeGeometryOverlay.setShape(command.getShape());
                resizeGeometryOverlay.setVisible(true);

                Command commandToAdd = command;

                if (createGroupCommand != null && !isEdit) {
                    CompoundCommand cc = (CompoundCommand) commandFactory.createCommand(CompoundCommand.COMMAND_NAME);
                    cc.init(createGroupCommand, command);
                    commandToAdd = cc;
                    createGroupCommand = null;
                }
            }

        } else if (command != null) {
            Shape shape = command.getShape();
            GeometryConfig geoConfig = (GeometryConfig) shape.getCustomConfig();
            float pointDst = Math.abs(startData.getDodlePoint().dst(CommonActorOperations.localToDodleCoordinates(shape, geoConfig.getPoint())));

            dragHandle = null;
            resizeGeometryOverlay.setVisible(true);

            for (HandleHook handle : ((GeometryRenderState) shape.getRenderState()).getHandleHooks()) {
                float grabDst = 40 / cameraManager.getGlobalViewportScale();
                Vector2 dodleCoords = CommonActorOperations.localToDodleCoordinates(shape, handle.getPosition());
                float handleDst = Math.abs(dodleCoords.dst(startData.getDodlePoint()));

                if (handleDst < grabDst && handleDst < pointDst) {
                    dragHandle = handle;
                    przaih.setEnabled(false);
                }
            }
        }

        Geometry geometry = geometryRegistry.getActiveGeometry();
        String iconName = "Shapes";
        if (geometry != null) {
            iconName = geometry.getIconName();
        }

        if (command != null && isEdit) {
            handleOkCancel(true, iconName);
        }
    }

    @Override
    public final void handleTouchMove(InteractionData moveData, int pointer) {
        if (dragHandle != null && command != null) {
            Vector2 shapePoint = CommonActorOperations.dodleToLocalCoordinates(command.getShape(), moveData.getDodlePoint());
            dragHandle.setPosition(shapePoint);
            regenerateShape();
        }
    }

    @Override
    public final void handleTouchEnd(InteractionData endData, int pointer) {
        regenerateShape();
        przaih.setEnabled(true);
    }

    @Override
    public void handleTouchCancel() {
    }

    //endregion TouchInputHandler Implementation

}
