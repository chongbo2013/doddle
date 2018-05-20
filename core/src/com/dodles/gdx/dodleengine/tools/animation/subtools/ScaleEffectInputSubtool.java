package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackFrame;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler;
import com.dodles.gdx.dodleengine.input.ZoomInputHandler;
import com.dodles.gdx.dodleengine.input.ZoomInteractionData;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolState;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScaleEffectInputSubtool extends AbstractAnimationSubtool implements ZoomInputHandler {

    // Subsystem References
    private final DodleStageManager stageManager;
    private final PanRotateZoomActorInputHandler przaih;

    // Internal Variables
    private ArrayList<Float> scaleSequence = new ArrayList<Float>();
    private ArrayList<Long> scaletiming = new ArrayList<Long>();
    private final List<Float> timingPercentages = new ArrayList<Float>();
    private DodlesActor actor = null;
    private boolean creatingNewEffect = false;

    //region Constructor
    @Inject
    public ScaleEffectInputSubtool(
            AnimationManager animationManager,
            AnimationSubtoolRegistry animationSubtoolRegistry,
            AnimationTool animationTool,
            EventBus eventBus,
            final ObjectManager objectManager,
            OkCancelStackManager okCancelStackManager,
            DodleStageManager stageManager,
            PanRotateZoomActorInputHandler przaih
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
        this.stageManager = stageManager;

        this.przaih = przaih;
        przaih.initialize(
                new PanRotateZoomActorInputHandler.ActorProvider() {
                    @Override
                    public DodlesActor getActor() {
                        return objectManager.getSelectedActor();
                    }
                },
                new PanRotateZoomActorInputHandler.PanEventHandler() {
                    @Override
                    public void onPan(Vector2 delta) {
                        // Do nothing on pan
                    }
                },
                new PanRotateZoomActorInputHandler.RotateEventHandler() {
                    @Override
                    public void onRotate(float delta) {
                        // Do nothing on rotate
                    }
                },
                null // Default zoom functionality
        );

        inputHandlers.add(this);
        inputHandlers.add(przaih);
    }
    //endregion Constructor

    //region AbstractAnimationSubtool Implementation

    @Override
    public AnimationSubtoolState getSubtoolState() {
        return AnimationSubtoolState.SCALE_EFFECT_INPUT;
    }

    @Override
    public void onActivation(AnimationSubtoolState previousState) {
        creatingNewEffect = (previousState != AnimationSubtoolState.SCALE_EFFECT_CONFIGURE);
        stageManager.setDisplayMode(DodleStageManager.DisplayMode.NORMAL);
    }

    @Override
    public void onDeactivation(AnimationSubtoolState nextState) {
        System.out.println("ScaleEffectInputSubtool::onDeactivation");
        creatingNewEffect = false;
    }

    //endregion AbstractAnimationSubtool Implementation

    //region Public API

    public boolean isCreatingNewEffect() {
        return creatingNewEffect;
    }

    public final List<Float> getScaleSequence() {
        return new ArrayList<Float>(scaleSequence);
    }

    public final List<Long> getScaleTiming() {
        return new ArrayList<Long>(scaletiming);
    }

    public final List<Float> getTimingPercentages() {
        return new ArrayList<Float>(timingPercentages);
    }

    public final float getAnimationLength() {
        // NOTE: don't rely on the timingPath here, b/c it might not be calculated yet
        if (scaletiming.size() > 1) {
            return (scaletiming.get(scaletiming.size() - 1) - scaletiming.get(0)) / 1000f;
        } else {
            return  0f;
        }
    }

    //endregion Public API

    //region TouchInputHandler Implementation

    @Override
    public void handleZoomStart(ZoomInteractionData startData) {
        actor = objectManager.getSelectedActor();

        if (actor != null) {
            scaleSequence.clear();
            scaletiming.clear();
            scaleSequence.add(startData.getScale());
            scaletiming.add(TimeUtils.millis());
        }
    }

    @Override
    public void handleZoomMove(ZoomInteractionData moveData) {
        if (actor != null) {
            scaleSequence.add(moveData.getScale());
            scaletiming.add(TimeUtils.millis());
        }
    }

    @Override
    public void handleZoomEnd(ZoomInteractionData endData) {
        if (actor != null && scaleSequence.size() > 1) {
            // Update sequence & timing
            scaleSequence.add(endData.getScale());
            scaletiming.add(TimeUtils.millis());

            // Reset actor
            actor.setScaleX(scaleSequence.get(0));
            actor.setScaleY(scaleSequence.get(0));

            decreasePoints(scaleSequence, scaletiming, getAnimationLength());

            // Compute Timing Percentages
            List<Float> newTimingPercentages = computeTimingPercentages(scaletiming);
            timingPercentages.clear();
            timingPercentages.addAll(newTimingPercentages);

            // Create ok/cancel stack if new animation, but not when redrawing
            if (creatingNewEffect) {
                okCancelStackManager.push(new OkCancelStackFrame("Size", true, false) {
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
                    AnimationSubtoolState.SCALE_EFFECT_CONFIGURE.toString());
        }
    }

    /**
     * Creates List<Float> of animation percentages (percent of animation per angle)
     * Based off a list of times between angles
     */
    private List<Float> computeTimingPercentages(List<Long> scaleTiming) {
        if (scaleTiming.size() <= 1) {
            return new ArrayList<Float>();
        }

        float totalTime = scaleTiming.get(scaleTiming.size()-1) - scaleTiming.get(0);
        List<Float> animationPercents = new ArrayList<Float>();

        animationPercents.add(0f);
        for (int i = 1; i < scaleTiming.size(); i++) {
            float delta = scaleTiming.get(i) - scaleTiming.get(i - 1);
            animationPercents.add(delta / totalTime);
        }

        return animationPercents;
    }

    // Decreases points in scale sequence & timing lists by half until there's few enough to play at defined minFPS
    private void decreasePoints(List<Float> scaleSequence, List<Long> scaleTiming, float seconds) {
        float minFPS = 20;
        while (scaleSequence.size() > seconds * minFPS) {
            // Remove every other point
            Iterator<Float> rotationIterator = scaleSequence.iterator();
            Iterator<Long> timingIterator = scaleTiming.iterator();
            while (rotationIterator.hasNext() && timingIterator.hasNext()) {
                rotationIterator.next();
                timingIterator.next();
                if (rotationIterator.hasNext() && timingIterator.hasNext()) {
                    rotationIterator.next();
                    timingIterator.next();
                    rotationIterator.remove();
                    timingIterator.remove();
                }
            }
        }
    }
}
