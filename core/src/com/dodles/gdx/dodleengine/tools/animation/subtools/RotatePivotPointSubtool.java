package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.assets.DemoAssets;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.ImageImportCommand;
import com.dodles.gdx.dodleengine.commands.animation.ModifyEffectCommand;
import com.dodles.gdx.dodleengine.editor.OkCancelStackFrame;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.input.InteractionData;
import com.dodles.gdx.dodleengine.input.TouchInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolState;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

public class RotatePivotPointSubtool extends AbstractAnimationSubtool implements TouchInputHandler {

    // Subsystem References
    private final DodleStageManager stageManager;
    private final CommandManager commandManager;
    private final CommandFactory commandFactory;
    private final DodleStageManager dodleStageManager;
    private final EngineEventManager eventManager;
    private final DemoAssets demoAssets;

    private boolean canMovePivotPoint = false;
    private EventSubscriber rotateConfigListener;
    private EventSubscriber editPivotPointListener;
    private ModifyEffectCommand lastCommand = null;
    private List<Float> rotationSequence = null;
    private List<Long> rotationTiming = null;
    private int pivotPointWidth = 40;
    private int pivotPointHeight = 40;
    private final HashMap<DodlesActor,DodlesActor> pivotPointMap = new HashMap<DodlesActor, DodlesActor>();
    //private RotatePreviewOverlay rotatePreviewOverlay;
    private DodlesActor selectedPivotPoint;

    //region Constructor
    @Inject
    public RotatePivotPointSubtool(
            AnimationManager animationManager,
            AnimationSubtoolRegistry animationSubtoolRegistry,
            AnimationTool animationTool,
            ObjectManager objectManager,
            EventBus eventBus,
            // RotatePreviewOverlay rotatePreviewOverlay,
            DodleStageManager dodleStageManager,
            EngineEventManager eventManager,
            CommandManager commandManager,
            DemoAssets demoAssets,
            CommandFactory commandFactory,
            OkCancelStackManager okCancelStackManager,
            DodleStageManager stageManager
    ) {
        // Super
        super(
                animationManager,
                animationSubtoolRegistry,
                animationTool,
                eventBus,
                objectManager,
                okCancelStackManager
        );

        // Subsystem References
        this.demoAssets = demoAssets;
        this.commandManager = commandManager;
        this.eventManager = eventManager;
        this.stageManager = stageManager;
        this.commandFactory = commandFactory;
        // this.rotatePreviewOverlay = rotatePreviewOverlay;
        this.dodleStageManager = dodleStageManager;

        // HACK: temp solution to DEV-509 until the AnimationTool and UI have better state handling of current block
        this.eventBus.addSubscriber(new EventSubscriber(EventTopic.DEFAULT) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                switch (eventType) {
                    case HOST_ENGINE_TRASH_STATE:
                        lastCommand = null;
                        break;
                }
            }
        });

        inputHandlers.add(this);
    }
    //endregion Constructor
    @Override
    public void handleTouchStart(InteractionData startData, int pointer) {
    }

    @Override
    public void handleTouchMove(InteractionData moveData, int pointer) {
    }

    @Override
    public void handleTouchEnd(final InteractionData endData, int pointer) {

    }

    @Override
    public void handleTouchCancel() {

    }

    @Override
    public AnimationSubtoolState getSubtoolState() {
        return AnimationSubtoolState.ROTATE_EFFECT_PIVOT_POINT;
    }

    @Override
    public void onActivation(AnimationSubtoolState previousState) {
    }

    @Override
    public void onDeactivation(AnimationSubtoolState nextState) {
    }
}
