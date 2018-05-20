package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
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
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.animation.ModifyEffectCommand;
import com.dodles.gdx.dodleengine.editor.OkCancelStackFrame;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolState;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class MoveEffectConfigureSubtool extends AbstractAnimationSubtool {

    //region Properties & Variables

    // Subsystem References
    private final CommandFactory commandFactory;
    private final DodleStageManager dodleStageManager;
    private final EngineEventManager eventManager;
    private final CommandManager commandManager;

    // Internal Variables
    private boolean creatingNewEffect = false;

    private float drawnPathTimeLength = 0;
    private List<Vector2> drawnPath;
    private List<Float> drawnPathTimePercentages;

    private EventSubscriber moveConfigListener;
    private ModifyEffectCommand lastCommand = null;

    //endregion Properties & Variables


    //region Constructor
    @Inject
    public MoveEffectConfigureSubtool(
            AnimationManager animationManager,
            AnimationSubtoolRegistry animationSubtoolRegistry,
            AnimationTool animationTool,
            CommandManager commandManager,
            CommandFactory commandFactory,
            DodleStageManager dodleStageManager,
            EngineEventManager eventManager,
            final ObjectManager objectManager,
            EventBus eventBus,
            OkCancelStackManager okCancelStackManager

    ) {
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
        this.dodleStageManager = dodleStageManager;
        this.eventManager = eventManager;

        moveConfigListener = new EventSubscriber(EventTopic.EDITOR) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                switch (eventType) {
                    case MOVE_CONFIG_UPDATE: {
                        String receivedResult = data.getFirstStringParam();
                        System.out.println("Move Config Update " + data.getFirstStringParam());
                        String[] params = receivedResult.split(Pattern.quote("|"));
                        for(int i = 0; i< params.length; i++) {
                            System.out.println("Param is " + params[i]);
                        }
                        int iterations = Integer.parseInt(params[0]);
                        boolean parallelToPath = Boolean.parseBoolean(params[1]);
                        boolean finishUpright = Boolean.parseBoolean(params[2]);
                        boolean straightPath = Boolean.parseBoolean(params[3]);
                        boolean returnToStart =  Boolean.parseBoolean(params[4]);
                        boolean reverseMotion =  Boolean.parseBoolean(params[5]);
                        createPathAnimationCommand(iterations, parallelToPath, finishUpright, straightPath, returnToStart, reverseMotion);
                        break;
                    }
                }
            }
        };
    }
    //endregion Constructor

    //region AbstractAnimationSubtool Implementation

    @Override
    public AnimationSubtoolState getSubtoolState() {
        return AnimationSubtoolState.MOVE_EFFECT_CONFIGURE;
    }

    @Override
    public void onPreActivation(AnimationSubtoolState currentState, AbstractAnimationSubtool currentSubtool) {
        // Determine if we are currently creating a new draw effect
        if (currentState == AnimationSubtoolState.MOVE_EFFECT_DRAW) {
            MoveEffectDrawSubtool moveEffectDrawSubtool = (MoveEffectDrawSubtool) currentSubtool;
            if (moveEffectDrawSubtool != null) {
                creatingNewEffect = moveEffectDrawSubtool.isCreatingNewEffect();
                if (creatingNewEffect) {
                    lastCommand = null;
                }
                drawnPathTimeLength = moveEffectDrawSubtool.getAnimationLength();
                drawnPath = moveEffectDrawSubtool.getPositionPath();
                drawnPathTimePercentages = moveEffectDrawSubtool.getTimingPercentages();
            }
        }
    }

    @Override
    public void onActivation(AnimationSubtoolState previousState) {
        System.out.println("Entering MOVE_EFFECT_CONFIGURE state, previous state is " + previousState);
        if(previousState == AnimationSubtoolState.MOVE_EFFECT_DRAW) {
            System.out.println(" I am creating the first draw path");
            createPathAnimationCommand(1, false, false, false, false, false);
        }
        eventBus.addSubscriber(moveConfigListener);
        // Create ok/cancel stack if editing existing animation
        if (previousState == AnimationSubtoolState.TIMELINE) {
            okCancelStackManager.push(new OkCancelStackFrame("Move-On-Timeline", true, false) {
                @Override
                public void execute() {
                    eventBus.publish(
                            EventTopic.EDITOR,
                            EventType.SET_ANIMATION_TOOL_STATE,
                            AnimationSubtoolState.TIMELINE.toString());
                }
            });
        }
    }

    @Override
    public void onDeactivation(AnimationSubtoolState nextState) {
        eventBus.removeSubscriber(moveConfigListener);
        creatingNewEffect = false;
        drawnPathTimeLength = 0;
        drawnPath = null;
        drawnPathTimePercentages = null;
    }

    //endregion AbstractAnimationSubtool Implementation

    //region Private Helper Functions

    private void createPathAnimationCommand(
            int iterations,
            boolean parallelToPath,
            boolean finishUpright,
            boolean straightPath,
            boolean returnToStart,
            boolean reverseMotion
    ) {
        // This is a temporary measure to delete the previous effect for ease of testing
        // Until there is a means to delete effects or preview a single effect
        //if (tempEffectId != null) {
        //    tempBlock.removeEffect(tempEffectId);
        //}

        if(lastCommand != null) {
            lastCommand.undo();
        }

        List<Vector2> path = drawnPath;
        List<Float> animationPercentages = drawnPathTimePercentages;

        if (drawnPath.size() <= 1) {
            return;
        }

        DodlesActor actor = objectManager.getSelectedActor();
        String actorID = null;
        if (actor != null) {
            actorID = actor.getName();
        }

        // Note: for now select the object before clicking animation button (first time)
        // Otherwise the actor won't be detected in the scene
        // Once that's figured out this if() can be taken out
        if (objectManager.allActorsInScene(animationTool.getScene().getSceneID()).get(actorID) == null) {
            Gdx.app.log("DrawMoveAnimationTool", "Actor ID is not in scene");
            if (objectManager.getActor(actorID) == null) {
                Gdx.app.log("DrawMoveAnimationTool", "Actor ID not in objectManager");
            }
            return;
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

        Vector2 start = drawnPath.get(0);
        Vector2 end = drawnPath.get(drawnPath.size() - 1);
        float translateX = end.x - start.x;
        float translateY = end.y - start.y;

        if(straightPath) {
            drawnPath = new ArrayList<Vector2>();
            drawnPath.add(start);
            drawnPath.add(end);
        }

        parameters.put(EffectParameterType.LENGTH, drawnPathTimeLength);
        parameters.put(EffectParameterType.TRANSLATE_X, translateX);
        parameters.put(EffectParameterType.TRANSLATE_Y, translateY);
        if(parallelToPath) {
            parameters.put(EffectParameterType.DEGREES, 360.0f);
        }

        String effectName = "pathEffect";
        EffectDefinition effectDefinition = renderEffectAnimation(
                effectName,
                path,
                animationPercentages,
                start,
                end,
                parameters,
                false);

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

        command.execute(); //Execute will add the animation ONE time to the timeline

        if(finishUpright) {

            List<Vector2> reversedPath = path;

            HashMap<EffectParameterType, Object> reverseParameters = new HashMap<EffectParameterType, Object>();

            reverseParameters.put(EffectParameterType.LENGTH, 0.3f);
            reverseParameters.put(EffectParameterType.DEGREES, 360.0f);


            List<Vector2> newReversedPath = new ArrayList<Vector2>();
            newReversedPath.add(path.get(path.size()-1));
            newReversedPath.add(path.get(path.size()-1));

            EffectDefinition reverseEffect = renderOnlyRotationEffectAnimation(
                    effectName, reversedPath, animationPercentages, end, start, reverseParameters);
            command.reverseBack(reverseEffect, reverseParameters);
        }

        if(reverseMotion) {
            List<Vector2> reversedPath = path;

            HashMap<EffectParameterType, Object> reverseParameters = new HashMap<EffectParameterType, Object>();

            float reverseTranslateX = end.x - start.x;
            float reverseTranslateY = end.y - start.y;

            reverseParameters.put(EffectParameterType.LENGTH, drawnPathTimeLength);
            reverseParameters.put(EffectParameterType.TRANSLATE_X, reverseTranslateX);
            reverseParameters.put(EffectParameterType.TRANSLATE_Y, reverseTranslateY);
            if(parallelToPath) {
                reverseParameters.put(EffectParameterType.DEGREES, 360.0f);
            }

            Collections.reverse(reversedPath);


            EffectDefinition reverseEffect = renderEffectAnimation(
                    effectName, reversedPath, animationPercentages, start, end, reverseParameters, true);
            command.reverseBack(reverseEffect, reverseParameters);
        }

        if(returnToStart) {
            HashMap<EffectParameterType, Object> reverseParameters = new HashMap<EffectParameterType, Object>();

            float reverseTranslateX = start.x - end.x;
            float reverseTranslateY = start.y - end.y;

            reverseParameters.put(EffectParameterType.LENGTH, 0.01f);
            reverseParameters.put(EffectParameterType.TRANSLATE_X, reverseTranslateX);
            reverseParameters.put(EffectParameterType.TRANSLATE_Y, reverseTranslateY);
            List<Vector2> newReversedPath = new ArrayList<Vector2>();
            newReversedPath.add(path.get(path.size()-1));
            newReversedPath.add(path.get(0));

            EffectDefinition reverseEffect = renderEffectAnimation(
                    effectName, newReversedPath, animationPercentages, end, start, reverseParameters, false);
            command.reverseBack(reverseEffect, reverseParameters);
        }

        if(iterations > 1) {
            command.repeatIterations(iterations - 1);
        }

        commandManager.add(command);

        lastCommand = command;

        String params = "TOOL.ANIMATION.SelectEffect.MOVE";
        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, params);
        eventBus.publish(EventTopic.DEFAULT, EventType.SCENE_ANIMATIONS_CHANGED);
    }


    private EffectDefinition renderEffectAnimation(
            String effectName,
            List<Vector2> path,
            List<Float> animationPercents,
            Vector2 start,
            Vector2 end,
            HashMap<EffectParameterType, Object> reverseParameters,
            boolean isReverseMotion
    ) {
        List<BaseKeyframe> keyframes = new ArrayList<BaseKeyframe>();
        float percentDone = 0;

        List<Float> rotations = null;
        if(reverseParameters.get(EffectParameterType.DEGREES) != null) {
            rotations = createSmoothedRotationList(path, isReverseMotion);
        }

        for (int i = 0; i < path.size(); i++) {
            Vector2 current = path.get(i);
            Float rotatePercent = rotations == null ? null : rotations.get(i);
            percentDone += animationPercents.get(i) * 100;
            float translateXPercent = (current.x - start.x) / (end.x - start.x) * 100f;
            float translateYPercent = (current.y - start.y) / (end.y - start.y) * 100f;
            keyframes.add(new MotionKeyframe(
                    percentDone,
                    rotatePercent,
                    null,
                    translateXPercent,
                    translateYPercent,
                    null));
        }
        EffectDefinition effectDefinition = new EffectDefinition(effectName, keyframes, EffectOperation.NONE, reverseParameters);
        return effectDefinition;
    }

    /**
     * Smooths rotation by averaging nearby rotation amounts
     * @return The smoothed rotation list in terms of percentage of rotation
     */
    private List<Float> createSmoothedRotationList(List<Vector2> path, boolean isReverseMotion) {

        // First create the rotation list without smoothing
        List<Float> rotationList = new ArrayList<Float>();
        Float startingAngle = null;
        for (int i = 0; i < path.size(); i++) {
            Vector2 current = path.get(i);
            Vector2 next = path.get(Math.min(i+1, path.size()-1));
            float degrees;
            if (isReverseMotion) {
                degrees = (float) Math.toDegrees(Math.atan2(next.y - current.y, next.x - current.x));
            }
            else {
                degrees = (float) Math.toDegrees(Math.atan2(current.y - next.y, current.x - next.x));
            }
            if (startingAngle == null) {
                startingAngle = degrees;
            }
            degrees -= startingAngle;
            degrees = (degrees / 360f) * 100f;
            if (i > 0) {
                while (degrees > rotationList.get(i-1) + 50f) {
                    degrees -= 100f;
                }
                while (degrees < rotationList.get(i-1) - 50f) {
                    degrees += 100f;
                }
            }
            rotationList.add(degrees);
        }

        // Then smooth by averaging angle with surrounding angles
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

    private EffectDefinition renderOnlyRotationEffectAnimation(
            String effectName,
            List<Vector2> path,
            List<Float> animationPercents,
            Vector2 start,
            Vector2 end,
            HashMap<EffectParameterType, Object> reverseParameters
    ) {
        List<BaseKeyframe> keyframes = new ArrayList<BaseKeyframe>();
        float percentDone = 0;
        for (int i = 0; i < animationPercents.size(); i++) {
            percentDone += animationPercents.get(i) * 100;
            float rotatePercent = 90/100f * percentDone;
            keyframes.add(new MotionKeyframe(
                    percentDone,
                    rotatePercent,
                    null,
                    null,
                    null,
                    null));
        }

        EffectDefinition effectDefinition = new EffectDefinition(effectName, keyframes, EffectOperation.NONE, reverseParameters);
        return effectDefinition;
    }

    //endregion Private Helper Functions
}