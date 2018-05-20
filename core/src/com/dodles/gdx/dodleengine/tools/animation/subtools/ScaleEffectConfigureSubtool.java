package com.dodles.gdx.dodleengine.tools.animation.subtools;

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
import java.util.HashMap;
import java.util.List;

public class ScaleEffectConfigureSubtool extends AbstractAnimationSubtool {

    // Subsystem References
    private final DodleStageManager stageManager;
    private final CommandManager commandManager;
    private final CommandFactory commandFactory;
    private final DodleStageManager dodleStageManager;
    private final EngineEventManager eventManager;

    // Internal variables
    private boolean creatingNewEffect = false;
    private ModifyEffectCommand lastCommand = null;
    private List<Float> scaleSequence = null;
    private List<Long> scaleTiming = null;
    private List<Float> timingPercentages = null;
    private float scaleTimeLength = 0;

    //region Constructor
    @Inject
    public ScaleEffectConfigureSubtool(
            AnimationManager animationManager,
            AnimationSubtoolRegistry animationSubtoolRegistry,
            AnimationTool animationTool,
            EventBus eventBus,
            ObjectManager objectManager,
            OkCancelStackManager okCancelStackManager,
            DodleStageManager dodleStageManager,
            EngineEventManager eventManager,
            CommandManager commandManager,
            CommandFactory commandFactory,
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
        this.commandManager = commandManager;
        this.eventManager = eventManager;
        this.stageManager = stageManager;
        this.commandFactory = commandFactory;
        this.dodleStageManager = dodleStageManager;
    }
    //endregion Constructor

    @Override
    public AnimationSubtoolState getSubtoolState() {
        return AnimationSubtoolState.SCALE_EFFECT_CONFIGURE;
    }

    @Override
    public void onPreActivation(AnimationSubtoolState currentState, AbstractAnimationSubtool currentSubtool) {
        System.out.println("ScaleEffectConfigureSubtool::onPreActivation");
        // Determine if we are currently creating a new scale effect
        if (currentState == AnimationSubtoolState.SCALE_EFFECT_INPUT) {
            ScaleEffectInputSubtool scaleEffectInputSubtool = (ScaleEffectInputSubtool) currentSubtool;
            if (scaleEffectInputSubtool != null) {
                creatingNewEffect = scaleEffectInputSubtool.isCreatingNewEffect();
                if (creatingNewEffect) {
                    lastCommand = null;
                }
                scaleSequence = scaleEffectInputSubtool.getScaleSequence();
                scaleTiming = scaleEffectInputSubtool.getScaleTiming();
                timingPercentages = scaleEffectInputSubtool.getTimingPercentages();
                scaleTimeLength = scaleEffectInputSubtool.getAnimationLength();
            }
        }
    }

    @Override
    public void onActivation(AnimationSubtoolState previousState) {
        System.out.println("ScaleEffectConfigureSubtool::onActivation");
        stageManager.setDisplayMode(DodleStageManager.DisplayMode.NORMAL);
        if (previousState == AnimationSubtoolState.SCALE_EFFECT_INPUT) {
            createScaleAnimationCommand();
        }

        // Create ok/cancel stack if editing existing animation
        if (previousState == AnimationSubtoolState.TIMELINE) {
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
    }

    @Override
    public void onDeactivation(AnimationSubtoolState nextState) {
        System.out.println("ScaleEffectConfigureSubtool::onDeactivation. Next state: " + nextState);
    }

    private void createScaleAnimationCommand() {

        if(lastCommand != null) {
            lastCommand.undo();
        }

        if (scaleSequence.size() <= 1) {
            return;
        }

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

        parameters.put(EffectParameterType.LENGTH, scaleTimeLength);
        parameters.put(EffectParameterType.SCALE, scaleSequence.get(scaleTiming.size()-1) - 1);

        String effectName = "scaleEffect";

        EffectDefinition effectDefinition = renderEffectAnimation(effectName, scaleSequence, timingPercentages, parameters);

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

        commandManager.add(command);

        lastCommand = command;

        String params = "TOOL.ANIMATION.SelectEffect.SCALE";
        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, params);
        eventBus.publish(EventTopic.DEFAULT, EventType.SCENE_ANIMATIONS_CHANGED);
    }

    private EffectDefinition renderEffectAnimation(String effectName, List<Float> scaleSequence, List<Float> animationPercents, HashMap<EffectParameterType, Object> parameters) {
        List<BaseKeyframe> keyframes = new ArrayList<BaseKeyframe>();
        float percentDone = 0;
        float startScale = scaleSequence.get(0);
        float endScale = scaleSequence.get(scaleSequence.size() - 1);
        float totalScaleDiff = endScale - startScale;

        for (int i = 0; i < scaleSequence.size(); i++) {
            float currentScaleDiff = scaleSequence.get(i) - startScale;
            float scalePercent = (currentScaleDiff / totalScaleDiff) * 100;
            percentDone += animationPercents.get(i) * 100;
            keyframes.add(new MotionKeyframe(
                    percentDone,
                    null,
                    scalePercent,
                    null,
                    null,
                    null));
        }
        EffectDefinition effectDefinition = new EffectDefinition(effectName, keyframes, EffectOperation.NONE, parameters);
        return effectDefinition;
    }
}
