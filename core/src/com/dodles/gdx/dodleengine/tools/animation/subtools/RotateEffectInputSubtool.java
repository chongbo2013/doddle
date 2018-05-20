package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackFrame;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.input.InteractionData;
import com.dodles.gdx.dodleengine.input.TouchInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolState;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.github.czyzby.kiwi.util.tuple.immutable.Pair;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class RotateEffectInputSubtool extends AbstractAnimationSubtool implements TouchInputHandler {

    // Subsystem References
    private final CommandManager commandManager;
    private final CommandFactory commandFactory;
    private final EngineEventManager eventManager;
    private final DodleStageManager stageManager;

    // Internal Variables
    private Float startActorAngle = null;
    private Float startTouchAngle = null;
    private DodlesActor actor = null;
    private final Pair<List<Float>, List<Long>> animationSequence = new Pair<List<Float>, List<Long>>(new ArrayList<Float>(), new ArrayList<Long>());
    private boolean creatingNewEffect = false;

    //region Constructor
    @Inject
    public RotateEffectInputSubtool(
            AnimationManager animationManager,
            AnimationSubtoolRegistry animationSubtoolRegistry,
            AnimationTool animationTool,
            final ObjectManager objectManager,
            CommandFactory commandFactory,
            CommandManager commandManager,
            EngineEventManager eventManager,
            EventBus eventBus,
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
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
        this.eventManager = eventManager;
        this.stageManager = stageManager;

        inputHandlers.add(this);
    }
    //endregion Constructor

    //region AbstractAnimationSubtool Implementation

    @Override
    public AnimationSubtoolState getSubtoolState() {
        return AnimationSubtoolState.ROTATE_EFFECT_INPUT;
    }

    @Override
    public void onActivation(AnimationSubtoolState previousState) {
        creatingNewEffect = (previousState != AnimationSubtoolState.ROTATE_EFFECT_CONFIGURE);
        stageManager.setDisplayMode(DodleStageManager.DisplayMode.NORMAL);
    }

    @Override
    public void onDeactivation(AnimationSubtoolState nextState) {
        System.out.println("Rotate effect deactivated");
        creatingNewEffect = false;
    }

    public boolean isCreatingNewEffect() {
        return creatingNewEffect;
    }

    public final List<Float> getRotationSequence() {
        return new ArrayList<Float>(animationSequence.getFirst());
    }

    public final List<Long> getRotationTiming() {
        return new ArrayList<Long>(animationSequence.getSecond());
    }



    //endregion AbstractAnimationSubtool Implementation

    //region TouchInputHandler Implementation

    @Override
    public void handleTouchStart(InteractionData startData, int pointer) {
        actor = objectManager.getSelectedActor();

        if (actor != null) {
            startActorAngle = actor.getRotation();
            Vector2 touchStart = new Vector2(startData.getDodlePoint().x, startData.getDodlePoint().y);
            Rectangle rect = CommonActorOperations.getDodleBounds(actor);
            Vector2 midPoint = new Vector2(rect.x + rect.width/2, rect.y + rect.height/2);
            startTouchAngle = (float) Math.toDegrees(Math.atan2(touchStart.y - midPoint.y, touchStart.x - midPoint.x));
            animationSequence.getFirst().clear();
            animationSequence.getSecond().clear();
            animationSequence.getFirst().add(0f);
            animationSequence.getSecond().add(TimeUtils.millis());
        }
    }

    @Override
    public void handleTouchMove(InteractionData moveData, int pointer) {
        System.out.println("Touch moved");
        if (actor != null) {
            Vector2 touch = new Vector2(moveData.getDodlePoint().x, moveData.getDodlePoint().y);
            Rectangle rect = CommonActorOperations.getDodleBounds(actor);
            Vector2 midPoint = new Vector2(rect.x + rect.width/2, rect.y + rect.height/2);
            float angle = (float) Math.toDegrees(Math.atan2(touch.y - midPoint.y, touch.x - midPoint.x));
            angle -= startTouchAngle;
            actor.setRotation(angle);
            animationSequence.getFirst().add(angle);
            animationSequence.getSecond().add(TimeUtils.millis());
        }
    }

    @Override
    public void handleTouchEnd(InteractionData endData, int pointer) {
        if (actor != null) {
            actor.setRotation(startActorAngle);
            actor = null;
        }

        // Create ok/cancel stack if new animation, but not when redrawing
        if (creatingNewEffect) {
            okCancelStackManager.push(new OkCancelStackFrame("Spin", true, false) {
                @Override
                public void execute() {
                    eventBus.publish(
                            EventTopic.EDITOR,
                            EventType.SET_ANIMATION_TOOL_STATE,
                            AnimationSubtoolState.TIMELINE.toString());
                }
            });
        }

        // Transition to Configure Event
        eventBus.publish(
                EventTopic.EDITOR,
                EventType.SET_ANIMATION_TOOL_STATE,
                AnimationSubtoolState.ROTATE_EFFECT_CONFIGURE.toString());
    }

    @Override
    public void handleTouchCancel() {

    }

    //endregion TouchInputHandler Implementation
}