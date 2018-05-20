package com.dodles.gdx.dodleengine.tools.geometry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.EffectAnimator;
import com.dodles.gdx.dodleengine.animation.EffectDefinition;
import com.dodles.gdx.dodleengine.animation.EffectParameterType;
import com.dodles.gdx.dodleengine.animation.EffectType;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.CompoundCommand;
import com.dodles.gdx.dodleengine.commands.DrawGeometryCommand;
import com.dodles.gdx.dodleengine.commands.MergeCommand;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.overlays.ResizeGeometryOverlay;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.geometry.Geometry;
import com.dodles.gdx.dodleengine.geometry.GeometryConfig;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import de.hypergraphs.hyena.core.shared.data.UUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractGeometrySubtool {


    //endregion Properties and Variables

    // Subsystem References
    protected AnimationManager animationManager;
    protected AssetProvider assetProvider;
    protected CommandFactory commandFactory;
    protected CommandManager commandManager;
    protected DodleStageManager dodlesStageManager;
    protected EditorState editorState;
    protected EventBus eventBus;
    protected GeometryRegistry geometryRegistry;
    protected ObjectManager objectManager;
    protected OkCancelStackManager okCancelStackManager;
    protected ResizeGeometryOverlay resizeGeometryOverlay;

    // Local Variables
    protected final ArrayList<InputHandler> inputHandlers = new ArrayList<InputHandler>();
    protected DrawGeometryCommand command;
    protected MergeCommand createGroupCommand;
    protected Runnable runOkPossiblyPushCloseNewObjectGroup;

    //region Properties and Variables

    //region Protected API

    protected void initializeBaseGeometrySubtool(
            AnimationManager animationManager,
            AssetProvider assetProvider,
            CommandFactory commandFactory,
            CommandManager commandManager,
            DodleStageManager dodlesStageManager,
            EditorState editorState,
            EventBus eventBus,
            GeometryRegistry geometryRegistry,
            ObjectManager objectManager,
            OkCancelStackManager okCancelStackManager,
            ResizeGeometryOverlay resizeGeometryOverlay
    ) {
        this.animationManager = animationManager;
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.dodlesStageManager = dodlesStageManager;
        this.editorState = editorState;
        this.eventBus = eventBus;
        this.geometryRegistry = geometryRegistry;
        this.objectManager = objectManager;
        this.okCancelStackManager = okCancelStackManager;
        this.resizeGeometryOverlay = resizeGeometryOverlay;
    }

    /**
     * HACK: needed to hack the function from the ToolHelper in order to combine the Ok/Cancel Stack for shapes
     * This version of the function locally stores the original Runnable without adding it to the ok cancel stack frame.
     * It is then run when the user presses ok on the OkCancelFrame associated with the specific draw shape
     */
    private MergeCommand possiblyPushCloseNewObjectGroup() {
        if (objectManager.getNewObjectGroup() == null) {
            runOkPossiblyPushCloseNewObjectGroup = new Runnable() {
                @Override
                public void run() {
                    DodlesActor newObjectGroup = objectManager.getNewObjectGroup();

                    if (newObjectGroup != null) {
                        objectManager.setNewObjectGroup(null);
                        dodlesStageManager.updateStateUi();

                        EffectDefinition puffEffect = EffectDefinition
                                .getDefaultDefinitions(assetProvider)
                                .get(EffectType.MOVE)
                                .get("puff");
                        HashMap<EffectParameterType, Object> parameters = new HashMap<EffectParameterType, Object>();
                        parameters.put(EffectParameterType.LENGTH, 0.25f);
                        final EffectAnimator effectAnimator = new EffectAnimator(
                                "puffID",
                                animationManager,
                                objectManager,
                                EffectType.MOVE,
                                "puff",
                                puffEffect,
                                parameters,
                                newObjectGroup);
                        effectAnimator.startAnimation(new Runnable() {
                            @Override
                            public void run() {
                                effectAnimator.resetAnimation();
                                dodlesStageManager.updateStateUi();
                            }
                        });
                    }
                }
            };

            String newGroupID = UUID.uuid();

            MergeCommand mc = (MergeCommand) commandFactory.createCommand(MergeCommand.COMMAND_NAME);
            BaseDodlesViewGroup layer = objectManager.getActiveLayer();
            mc.init(newGroupID, null, layer.getName(), layer.getActiveViewID(), true);
            mc.execute();

            objectManager.setNewObjectGroup((DodlesGroup) objectManager.getActor(newGroupID));

            return mc;
        }

        return null;
    }

    protected void handleOkCancel(final boolean isEdit, final String shapeName) {
        command.getShape().setRenderMode(Shape.RenderMode.DIRECT);
        regenerateShape();

        resizeGeometryOverlay.setShape(command.getShape());
        dodlesStageManager.updateStateUi();

        okCancelStackManager.push(
                new Runnable() {
                    @Override
                    public void run() {
                        command.finalizeConfig();
                        Command commandToAdd = command;

                        if (createGroupCommand != null && !isEdit) {
                            CompoundCommand cc = (CompoundCommand) commandFactory.createCommand(
                                    CompoundCommand.COMMAND_NAME);
                            cc.init(createGroupCommand, command);
                            commandToAdd = cc;
                        }

                        commandManager.add(commandToAdd);
                        finalizeCommand();

                        Gdx.app.postRunnable(runOkPossiblyPushCloseNewObjectGroup);
                        runOkPossiblyPushCloseNewObjectGroup = null;
                    }
                },
                null,
                false,
                shapeName
        );
    }

    protected void finalizeCommand() {
        geometryRegistry.setActiveGeometry(null);
        eventBus.publish(EventTopic.DEFAULT, EventType.SHAPE_DEACTIVATED);
        resizeGeometryOverlay.setShape(null);
        resizeGeometryOverlay.setVisible(false);

        if (command.getShape() != null) {
            command.getShape().setRenderMode(Shape.RenderMode.ATLAS);
            regenerateShape();
        }

        dodlesStageManager.updateStateUi();
        command = null;
        createGroupCommand = null;
    }

    //endregion Protected API

    //region Public API
    public abstract void onActivation();
    public abstract void onDeactivation();
    public abstract Shape getActiveShape();
    public abstract void regenerateShape();

    public void addShapeToCanvas(Geometry geometry, GeometryConfig config) {

        Vector2 centerPoint = new Vector2(375, 375);
        config.setPoint(centerPoint);

        createGroupCommand = possiblyPushCloseNewObjectGroup();
        command = (DrawGeometryCommand) commandFactory.createCommand(DrawGeometryCommand.COMMAND_NAME);
        command.init(UUID.uuid(), objectManager.getNewObjectGroup(), editorState.getStrokeConfig(), geometry, config);
        command.execute();

        handleOkCancel(false, geometry.getIconName());

        geometryRegistry.setActiveGeometry(config.getType());
        geometryRegistry.setGeometryConfig(config);
        editorState.setStrokeConfig(command.getShape().getStrokeConfig());

        resizeGeometryOverlay.setShape(command.getShape());
        resizeGeometryOverlay.setVisible(true);
    }

    public final List<InputHandler> getInputHandlers() {
        return inputHandlers;
    }
    //endregion Public API
}
