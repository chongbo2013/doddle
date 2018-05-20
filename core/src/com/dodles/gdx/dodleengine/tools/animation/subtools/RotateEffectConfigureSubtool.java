package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.BaseKeyframe;
import com.dodles.gdx.dodleengine.animation.BlockStatus;
import com.dodles.gdx.dodleengine.animation.EffectDefinition;
import com.dodles.gdx.dodleengine.animation.EffectOperation;
import com.dodles.gdx.dodleengine.animation.EffectParameterType;
import com.dodles.gdx.dodleengine.animation.EffectTiming;
import com.dodles.gdx.dodleengine.animation.EffectType;
import com.dodles.gdx.dodleengine.animation.MotionKeyframe;
import com.dodles.gdx.dodleengine.assets.DemoAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.animation.ModifyEffectCommand;
import com.dodles.gdx.dodleengine.editor.OkCancelStackFrame;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.overlays.PivotPointOverlay;
import com.dodles.gdx.dodleengine.editor.overlays.RotatePreviewOverlay;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
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
import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class RotateEffectConfigureSubtool extends AbstractAnimationSubtool implements TouchInputHandler {

    // Subsystem References
    private final DodleStageManager stageManager;
    private final CommandManager commandManager;
    private final CommandFactory commandFactory;
    private final DodleStageManager dodleStageManager;
    private final EngineEventManager eventManager;
    private final DemoAssets demoAssets;
    private final RotatePreviewOverlay rotatePreviewOverlay;
    private final PivotPointOverlay pivotPointOverlay;

    private boolean canMovePivotPoint = false;
    private EventSubscriber rotateConfigListener;
    private EventSubscriber editPivotPointListener;

    private boolean creatingNewEffect = false;
    private ModifyEffectCommand lastCommand = null;
    private List<Float> originalRotationSequence = null;
    private List<Long> originalRotationTiming = null;
    private boolean inPivotPointMode = false;
    private String initialLayerID;
    private List<Float> rotationSequence = null;
    private List<Long> rotationTiming = null;
    //private RotatePreviewOverlay rotatePreviewOverlay;
    private DodlesActor selectedPivotPoint;


    //region Constructor
    @Inject
    public RotateEffectConfigureSubtool(
            AnimationManager animationManager,
            AnimationSubtoolRegistry animationSubtoolRegistry,
            AnimationTool animationTool,
            ObjectManager objectManager,
            EventBus eventBus,
            RotatePreviewOverlay rotatePreviewOverlay,
            PivotPointOverlay pivotPointOverlay,
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
        this.rotatePreviewOverlay = rotatePreviewOverlay;
        this.pivotPointOverlay = pivotPointOverlay;
        this.dodleStageManager = dodleStageManager;

        rotateConfigListener = new EventSubscriber(EventTopic.EDITOR) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                switch (eventType) {
                    case ROTATE_CONFIG_UPDATE: {
                        String receivedResult = data.getFirstStringParam();
                        System.out.println("Rotate Config Update " + data.getFirstStringParam());
                        String[] params = receivedResult.split(Pattern.quote("|"));
                        for(int i = 0; i< params.length; i++) {
                            System.out.println("Param is " + params[i]);
                        }
                        int iterations = Integer.parseInt(params[0]);
                        boolean finishUpright = Boolean.parseBoolean(params[1]);
                        boolean autoRotate = Boolean.parseBoolean(params[2]);
                        boolean reverseMotion =  Boolean.parseBoolean(params[3]);
                        createRotationAnimationCommand(iterations, finishUpright, autoRotate, reverseMotion);
                        break;
                    }
                    case ANIM_TOGGLE_PIVOT_POINT_MODE:
                        togglePivotPointMode();
                        break;
                }
            }
        };
        inputHandlers.add(this);
    }
    //endregion Constructor


    private void createRotationAnimationCommand(
            int iterations,
            boolean finishUpright,
            boolean autoRotate,
            boolean reverseMotion
    ) {

        if(lastCommand != null) {
            lastCommand.undo();
        }

        List<Float> rotationSequence = new ArrayList<Float>(originalRotationSequence);
        List<Long> rotationTiming = new ArrayList<Long>(originalRotationTiming);

        if (rotationSequence.size() <= 1) {
            return;
        }

        float seconds = (rotationTiming.get(rotationTiming.size()-1) - rotationTiming.get(0)) / 1000f * .85f;

        decreasePoints(rotationSequence, rotationTiming, seconds);
        rotationSequence = createSmoothedRotationList(rotationSequence);
        List<Float> animationPercents = getAnimationPercents(rotationTiming);

        DodlesActor actor = objectManager.getSelectedActor();
        String actorID = null;
        if (actor != null) {
            actorID = actor.getName();
        }

        ModifyEffectCommand command = (ModifyEffectCommand) commandFactory.createCommand(ModifyEffectCommand.COMMAND_NAME);
        String afterEffectID = null;
        boolean afterLastEffect = false; // This boolean can be modified once the user has the option to choose where in the animation the effect will start
        float delay = 0;

        if (afterLastEffect) {
            // after last effect for the actor in the block
            float maxLength = -1;

            for (EffectTiming timing : animationTool.getActiveBlock().allEffects(actorID)) {
                float curLength = timing.calculateEndTime();

                if (curLength > maxLength) {
                    maxLength = curLength;
                    afterEffectID = timing.getEffect().getEffectID();
                }
            }
        } else {
            // at the current position in the timeline
            BlockStatus status = animationManager.getBlockStatus(animationTool.getActiveBlock().getBlockId());

            if (status != null) {
                delay = status.getCurTime();
            }
        }

        HashMap<EffectParameterType, Object> parameters = new HashMap<EffectParameterType, Object>();

        parameters.put(EffectParameterType.LENGTH, seconds);
        parameters.put(EffectParameterType.DEGREES, 360f);
        EffectDefinition effectDefinition;

        String effectName = "rotateEffect";

        if(autoRotate) {
            effectDefinition = createAutoRotateAnimation(effectName,rotationSequence, animationPercents, parameters);
        } else {
            effectDefinition = renderEffectAnimation(effectName, rotationSequence, animationPercents, parameters, false);
        }

        String effectID = UUID.uuid();

        command.init(
                animationTool.getScene().getSceneID(),
                effectID,
                animationTool.getActiveBlock().getBlockId(),
                actorID,
                EffectType.MOVE,
                effectName,
                effectDefinition,
                parameters,
                afterEffectID,
                delay
        );

        command.execute();

        HashMap<EffectParameterType, Object> finishUprightParameters = new HashMap<EffectParameterType, Object>();
        if(finishUpright && !autoRotate) {
            finishUprightParameters.put(EffectParameterType.LENGTH, 0.5f);
            finishUprightParameters.put(EffectParameterType.DEGREES, 360f);
            EffectDefinition finishUprightEffectDefinition = renderFinishUprightEffectAnimation(
                    effectName, rotationSequence, animationPercents, finishUprightParameters, false);
            command.reverseBack(finishUprightEffectDefinition, finishUprightParameters);
        }
        if(reverseMotion && !autoRotate) {

            if(finishUpright) {
                EffectDefinition reverseEffect = renderFinishUprightEffectAnimation(
                        effectName, rotationSequence, animationPercents, finishUprightParameters, true);
                command.reverseBack(reverseEffect, finishUprightParameters);
            }
            EffectDefinition reverseEffect = renderEffectAnimation(
                    effectName, rotationSequence, animationPercents, parameters, true);
            command.reverseBack(reverseEffect, parameters);
        }
        if(iterations > 1) {
            command.repeatIterations(iterations - 1);
        }



        commandManager.add(command);

        lastCommand = command;

        String params = "TOOL.ANIMATION.SelectEffect.ROTATE";
        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, params);
        eventBus.publish(EventTopic.DEFAULT, EventType.SCENE_ANIMATIONS_CHANGED);
    }

    private EffectDefinition renderEffectAnimation(String effectName, List<Float> rotationSequence, List<Float> animationPercents, HashMap<EffectParameterType, Object> parameters, boolean reverse) {
        List<BaseKeyframe> keyframes = new ArrayList<BaseKeyframe>();
        float percentDone = 0;

        for (int i = 0; i < rotationSequence.size(); i++) {
            float rotatePercent;
            if(reverse) {
                rotatePercent= -rotationSequence.get(i) / 360f * 100f;
            } else {
                rotatePercent= rotationSequence.get(i) / 360f * 100f;
            }
            percentDone += animationPercents.get(i) * 100;
            keyframes.add(new MotionKeyframe(
                    percentDone,
                    rotatePercent,
                    null,
                    null,
                    null,
                    null));
        }
        EffectDefinition effectDefinition = new EffectDefinition(effectName, keyframes, EffectOperation.NONE, parameters);
        return effectDefinition;
    }

    private EffectDefinition renderFinishUprightEffectAnimation(String effectName, List<Float> rotationSequence, List<Float> animationPercents, HashMap<EffectParameterType, Object> parameters, boolean reverse) {
        List<BaseKeyframe> keyframes = new ArrayList<BaseKeyframe>();
        float percentDone = 0;
        int sizeOfRotationList = rotationSequence.size();
        int presetFps = 30;
        float currentRotationPercent = rotationSequence.get(sizeOfRotationList - 1);
        float rotationAngleToCover = 360f - currentRotationPercent;

        for (int i = 0; i < presetFps; i++) {
            float rotatePercent;
            if(reverse) {
                rotatePercent= ((-(rotationAngleToCover / presetFps) * (i + 1)) / 360f) * 100f;
            } else {
                rotatePercent= (((rotationAngleToCover / presetFps) * (i + 1)) / 360f) * 100f;
            }
            percentDone += (100/presetFps);
            keyframes.add(new MotionKeyframe(
                    percentDone,
                    rotatePercent,
                    null,
                    null,
                    null,
                    null));
        }
        EffectDefinition effectDefinition = new EffectDefinition(effectName, keyframes, EffectOperation.NONE, parameters);
        return effectDefinition;
    }



    private EffectDefinition createAutoRotateAnimation(String effectName, List<Float> rotationSequence, List<Float> animationPercents, HashMap<EffectParameterType, Object> parameters) {
        List<BaseKeyframe> keyframes = new ArrayList<BaseKeyframe>();
        int rotatePercentPerFrame = (360 / rotationSequence.size());
        float percentDone = 0;

        for (int i = 0; i < rotationSequence.size(); i++) {
            float rotatePercent = ((rotatePercentPerFrame * (i + 1)) / 360f) * 100f;
            percentDone += animationPercents.get(i) * 100;
            keyframes.add(new MotionKeyframe(
                    percentDone,
                    rotatePercent,
                    null,
                    null,
                    null,
                    null));
        }
        EffectDefinition effectDefinition = new EffectDefinition(effectName, keyframes, EffectOperation.NONE, parameters);
        return effectDefinition;
    }

    /**
     * Smooths rotation by averaging nearby angles
     * @return The smoothed rotation list in terms of percentage of rotation
     */
    private List<Float> createSmoothedRotationList(List<Float> rotationList) {
        for (int i = 1; i < rotationList.size(); i++) {
            float angle = rotationList.get(i);
            while (angle > rotationList.get(i-1) + 180f) {
                angle -= 360f;
            }
            while (angle < rotationList.get(i-1) - 180f) {
                angle += 360f;
            }
            rotationList.set(i, angle);
        }

        List<Float> smoothedList = new ArrayList<Float>();
        for (int i = 0; i < rotationList.size(); i++) {
            float prev4 = rotationList.get(Math.max(0, i-4));
            float prev3 = rotationList.get(Math.max(0, i-3));
            float prev2 = rotationList.get(Math.max(0, i-2));
            float prev = rotationList.get(Math.max(0, i-1));
            float current = rotationList.get(i);
            float next = rotationList.get(Math.min(i+1, rotationList.size()-1));
            float next2 = rotationList.get(Math.min(i+2, rotationList.size()-1));
            float next3 = rotationList.get(Math.min(i+3, rotationList.size()-1));
            float next4 = rotationList.get(Math.min(i+4, rotationList.size()-1));

            float average = (prev4 + prev3 + prev2 + prev + current + next + next2 + next3 + next4) / 9;
            smoothedList.add(average);
        }

        return smoothedList;
    }

    // Decreases points in rotation list & rotation timing list by half until there's few enough to play at defined minFPS
    private List<Float> decreasePoints(List<Float> rotationList, List<Long> rotationTiming, float seconds) {
        float minFPS = 20;
        while (rotationList.size() > seconds * minFPS) {
            // Remove every other point
            Iterator<Float> rotationIterator = rotationList.iterator();
            Iterator<Long> timingIterator = rotationTiming.iterator();
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
        return rotationList;
    }

    /**
     * Creates List<Float> of animation percentages (percent of animation per angle)
     * Based off a list of times between angles
     */
    private List<Float> getAnimationPercents(List<Long> rotationTiming) {
        if (rotationTiming.size() <= 1) {
            return new ArrayList<Float>();
        }

        float totalTime = rotationTiming.get(rotationTiming.size()-1) - rotationTiming.get(0);
        List<Float> animationPercents = new ArrayList<Float>();

        animationPercents.add(0f);
        for (int i = 1; i < rotationTiming.size(); i++) {
            float delta = rotationTiming.get(i) - rotationTiming.get(i - 1);
            animationPercents.add(delta / totalTime);
        }

        return animationPercents;
    }

    @Override
    public AnimationSubtoolState getSubtoolState() {
        return AnimationSubtoolState.ROTATE_EFFECT_CONFIGURE;
    }

    @Override
    public void onPreActivation(AnimationSubtoolState currentState, AbstractAnimationSubtool currentSubtool) {
        // Determine if we are currently creating a new rotate effect
        if (currentState == AnimationSubtoolState.ROTATE_EFFECT_INPUT) {
            RotateEffectInputSubtool rotateEffectInputSubtool = (RotateEffectInputSubtool) currentSubtool;
            if (rotateEffectInputSubtool != null) {
                creatingNewEffect = rotateEffectInputSubtool.isCreatingNewEffect();
                if (creatingNewEffect) {
                    lastCommand = null;
                }
                originalRotationSequence = rotateEffectInputSubtool.getRotationSequence();
                originalRotationTiming = rotateEffectInputSubtool.getRotationTiming();
            }
        }
    }


    private void enablePivotPointManipulation() {
        //load image of pivot point from demo assetn
        pivotPointOverlay.initializePivotPoint();
        DodlesActor actor = objectManager.getSelectedActor();
        pivotPointOverlay.update( CommonActorOperations.getRootGroup(actor).localToStageCoordinates(actor.getOrigin()));
        pivotPointOverlay.displayOverlay(true);
    }

    private void togglePivotPointMode() {
        inPivotPointMode = !inPivotPointMode;

        if(inPivotPointMode) {
            OkCancelStackFrame newStack = new OkCancelStackFrame("PivotPoint", true, true) {
                @Override
                public void execute() {
                    DodlesActor actor = objectManager.getSelectedActor();
                    Vector2 newOrigin = pivotPointOverlay.getLastLocation();
                    Vector2 localOrigin = ((Actor)actor).stageToLocalCoordinates(new Vector2(newOrigin.x, newOrigin.y));
                    ((Actor) actor).setOrigin(localOrigin.x , localOrigin.y);
                    System.out.println("New origin of the actor is " + actor.getOrigin().toString());
                    inPivotPointMode = false;
                }
            };
            okCancelStackManager.nextLayer();
            initialLayerID = okCancelStackManager.getLayerID();
            okCancelStackManager.push(newStack, true);
        } else {
            if (initialLayerID != null) {
               okCancelStackManager.popThroughLayer(initialLayerID,true);
               disablePivotPointManipulation();
               enablePivotPointManipulation();
            }
            initialLayerID = null;
        }
    }

    private void disablePivotPointManipulation() {
        //load image of pivot point from demo asset
        pivotPointOverlay.displayOverlay(false);
    }


    @Override
    public void handleTouchStart(InteractionData startData, int pointer) {
        if(inPivotPointMode) {
            pivotPointOverlay.update(startData.getGlobalPoint());
        }
    }

    @Override
    public void handleTouchMove(InteractionData moveData, int pointer) {
        //move the pivot point image
        if(inPivotPointMode) {
            pivotPointOverlay.update(moveData.getGlobalPoint());
        }

    }

    @Override
    public void handleTouchEnd(final InteractionData endData, int pointer) {
        if(inPivotPointMode) {
            pivotPointOverlay.update(endData.getGlobalPoint());
            DodlesActor actor = objectManager.getSelectedActor();
        }
        //((Actor) actor).setOrigin(selectedPivotPoint.getX() + (pivotPointWidth) , selectedPivotPoint.getY() + (pivotPointHeight));
        System.out.println(" Handle Touch Ended, end point is " + endData.getDodlePoint().x + " : " + endData.getDodlePoint().y);

    }

    @Override
    public void handleTouchCancel() {

    }




    @Override
    public void onActivation(AnimationSubtoolState previousState) {
        stageManager.setDisplayMode(DodleStageManager.DisplayMode.NORMAL);
        if(previousState == AnimationSubtoolState.ROTATE_EFFECT_INPUT) {
            createRotationAnimationCommand(1, false, false, false);
        }
        eventBus.addSubscriber(rotateConfigListener);

        // Create ok/cancel stack if editing existing animation
        if (previousState == AnimationSubtoolState.TIMELINE) {
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
        enablePivotPointManipulation();

        // Rotate preview taken out until re-implemented correctly
        //rotatePreviewOverlay.setRotationSequence(originalRotationSequence);
        //rotatePreviewOverlay.displayOverlay(true);
    }

    @Override
    public void onDeactivation(AnimationSubtoolState nextState) {
        // Rotate preview taken out until re-implemented correctly
        //rotatePreviewOverlay.displayOverlay(false);

        disablePivotPointManipulation();
        eventBus.removeSubscriber(rotateConfigListener);

    }
}
