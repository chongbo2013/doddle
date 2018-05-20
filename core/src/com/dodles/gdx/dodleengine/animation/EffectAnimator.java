package com.dodles.gdx.dodleengine.animation;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import com.dodles.gdx.dodleengine.scenegraph.Transform;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseAttributeType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStepType;
import com.dodles.gdx.dodleengine.util.JsonUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the animation of a single effect for a single actor.
 */
public class EffectAnimator {
    private final AnimationManager animationManager;
    private final ObjectManager objectManager;
    private final EffectType effectType;
    private final String effectName;
    private final EffectDefinition effect;
    private final Map<EffectParameterType, Object> parameters;
    private final Actor actor;
    private final String effectID;
    
    private boolean animating = false;
    private Scene originalScene = null;
    private String originalPhaseID = null;
    
    public EffectAnimator(String effectID, AnimationManager animationManager, ObjectManager objectManager, EffectType effectType, String effectName, EffectDefinition effect, Map<EffectParameterType, Object> parameters, DodlesActor actor) {
        this.effectID = effectID;
        this.animationManager = animationManager;
        this.objectManager = objectManager;
        this.effectType = effectType;
        this.effectName = effectName;
        this.effect = effect;
        this.parameters = parameters;
        this.actor = (Actor) actor;
        
        if (actor == null) {
            throw new GdxRuntimeException("blah");
        }
        
        for (EffectParameterType ept : effect.getDefaultParameters().keySet()) {
            if (!parameters.containsKey(ept)) {
                parameters.put(ept, effect.getDefaultParameters().get(ept));
            }
        }
    }
    
    public EffectAnimator(JsonValue json, AnimationManager animationManager, ObjectManager objectManager, SceneAnimation sceneAnimation) {
        this(
            json.getString("effectID"),
            animationManager,
            objectManager,
            EffectType.valueOf(json.getString("effectType")),
            json.getString("effectName", null),
            new EffectDefinition(json.get(EffectDefinition.JSON_NAME)),
            parseParameters(json.get("parameters")),
            objectManager.allActorsInScene(sceneAnimation.getSceneID()).get(json.getString("actorID"))
        );
    }
    
    /**
     * Returns the ID of the effect.
     */
    public final String getEffectID() {
        return effectID;
    }
    
    /**
     * Returns the actor attached to this effect.
     */
    public final Actor getActor() {
        return actor;
    }
    
    /**
     * Returns the type of the effect.
     */
    public final EffectType getEffectType() {
        return effectType;
    }
    
    /**
     * Returns the name of the effect.
     */
    public final String getEffectName() {
        return effectName;
    }
    
    /**
     * Returns the effect definition.
     */
    public final EffectDefinition getEffectDefinition() {
        return effect;
    }
    
    /**
     * Returns the effect parameters.
     */
    public final Map<EffectParameterType, Object> getEffectParameters() {
        return parameters;
    }
    
    /**
     * Returns the length of the effect.
     */
    public final float getEffectLength() {
        if (!parameters.containsKey(EffectParameterType.LENGTH)) {
            return 0;
        }
        
        return (Float) parameters.get(EffectParameterType.LENGTH);
    }
    
    /**
     * Resets the animation.
     */
    public final void resetAnimation() {
        if (animating) {
            actor.clearActions();
            ((DodlesActor) actor).resetToBaseTransform();
            resetOperation();
            animating = false;
        }
    }
    
    /**
     * Stops the animation (but doesn't reset it).
     */
    public final void stopAnimation() {
        if (animating) {
            actor.clearActions();
            animating = false;
        }
    }
    
    /**
     * Starts the animation.
     */
    public final void startAnimation(Runnable callback) {
        startAnimation(callback, 0);
    }
    
    /**
     * Starts the animation with an optional delay.
     */
    public final void startAnimation(Runnable callback, float postDelay) {
        animating = true;
        ParallelAction actions = Actions.parallel();
        
        if (effect.getOperation() != EffectOperation.NONE) {
            actions.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    executeOperation();
                }
            }));
        }
        
        Action action = calculateAction();
        
        if (action != null) {
            actions.addAction(action);
        }
        
        if (actions.getActions().size > 0) {
            if (postDelay > 0) {
                actions.addAction(Actions.delay(postDelay));
            }
            
            actor.addAction(Actions.sequence(actions, Actions.run(callback)));
        }
    }
    
    /**
     * Calculates the change in actor position caused by the current effect.
     */
    public final Transform calculateTransformDelta() {
        Transform result = new Transform();
        result.setScaleX(0);
        result.setScaleY(0);
        
        for (BaseKeyframe keyframe : effect.getKeyframes()) {
            if (keyframe instanceof MotionKeyframe) {
                Transform frameTransform = ((MotionKeyframe) keyframe).calculateTransform(parameters);

                if (frameTransform.getX() != null) {
                    result.setX(frameTransform.getX());
                }

                if (frameTransform.getY() != null) {
                    result.setY(frameTransform.getY());
                }

                if (frameTransform.getScaleX() != null) {
                    result.setScaleX(frameTransform.getScaleX());
                }

                if (frameTransform.getScaleY() != null) {
                    result.setScaleY(frameTransform.getScaleY());
                }

                if (frameTransform.getRotation() != null) {
                    result.setRotation(frameTransform.getRotation());
                }
            }
        }
        
        return result;
    }
    
    /**
     * Returns the timing of tweens within the effect.
     */
    public final List<Float> getTweenTimes() {
        ArrayList<Float> result = new ArrayList<Float>();
        float prevPct = 0;
        
        for (BaseKeyframe keyframe : effect.getKeyframes()) {
            result.add(calculateTweenLength(keyframe, prevPct));
        }
        
        return result;
    }
    
    private static Map<EffectParameterType, Object> parseParameters(JsonValue json) {
        Map<EffectParameterType, Object> parameters = new HashMap<EffectParameterType, Object>();
        
        for (int i = 0; i < json.size; i++) {
            JsonValue curParam = json.get(i);
            parameters.put(EffectParameterType.valueOf(curParam.name), JsonUtility.getObject(curParam));
        }
        
        return parameters;
    }
    
    private void executeOperation() {
        switch (effect.getOperation()) {
            case CHANGE_SCENE:
                originalScene = objectManager.getScene();
                Scene newScene = objectManager.getScene(((Float) parameters.get(EffectParameterType.SCENE_NUM)).intValue());
                SceneAnimation animation = animationManager.getSceneAnimation(newScene.getName());
                
                if (!animationManager.isPlayingSingleBlock()) {
                    animationManager.activateBlock(newScene.getName(), animation.getRootBlock().getBlockId());
                }
                break;
                
            case CHANGE_PHASE:
                if (actor instanceof DodlesGroup) {
                    DodlesGroup pg = ((DodlesGroup) actor);
                    originalPhaseID = pg.getVisiblePhaseID();
                    pg.setVisiblePhase(((Float) parameters.get(EffectParameterType.PHASE_NUM)).intValue() - 1);
                }
                break;

            case CHANGE_PHASE_ATTRIBUTE:
                if (actor instanceof DodlesGroup) {
                    DodlesGroup pg = ((DodlesGroup) actor);
                    originalPhaseID = pg.getVisiblePhaseID();
                    PhaseAttributeType attr = PhaseAttributeType.valueOf((String) parameters.get(EffectParameterType.PHASE_ATTRIBUTE));
                    PhaseStepType step = PhaseStepType.valueOf((String) parameters.get(EffectParameterType.PHASE_STEP));
                    pg.setPhaseAttribute(attr, step);
                }
                break;
            
            case PLAY_AUDIO:
                if (!animationManager.isSeeking()) {
                    Sound sound = animationManager.getSound((String) parameters.get(EffectParameterType.SOUND_ASSET));
                    sound.play();
                }
                break;
                
            default:
                throw new GdxRuntimeException("Unrecognized operation: " + effect.getOperation().name());
        }
    }
    
    private void resetOperation() {
        if (originalScene != null) {
            objectManager.setActiveScene(originalScene.getName());
            originalScene = null;
        } else if (originalPhaseID != null) {
            ((DodlesGroup) actor).setVisiblePhase(originalPhaseID);
            originalPhaseID = null;
        }
    }
    
    private float calculateTweenLength(BaseKeyframe keyframe, float prevPct) {
        return ((Float) parameters.get(EffectParameterType.LENGTH)) * (keyframe.getPercent().floatValue() - prevPct) * 0.01f;
    }
    
    private Action calculateAction() {
        if (effect.getKeyframes() == null) {
            return null;
        }
        
        float prevPct = 0;
        
        SequenceAction sequence = Actions.sequence();
        BaseKeyframe prevKeyframe = null;

        for (BaseKeyframe keyframe : effect.getKeyframes()) {
            float tweenLength = calculateTweenLength(keyframe, prevPct);
            prevPct = keyframe.getPercent();
            
            ParallelAction keyFrameActions = Actions.parallel();
            sequence.addAction(keyFrameActions);
            
            if (keyframe instanceof MotionKeyframe) {
                MotionKeyframe mKeyframe = (MotionKeyframe) keyframe;
                MotionKeyframe prevMKeyframe = (MotionKeyframe) prevKeyframe;
                
                Transform frameTransform = mKeyframe.calculateTransform(parameters);
                Transform prevFrameTransform = new Transform();
                
                if (prevMKeyframe != null) {
                    prevFrameTransform = prevMKeyframe.calculateTransform(parameters);
                }
            
                if (frameTransform.getX() != null && frameTransform.getY() != null) {
                    MoveByAction mba = Actions.moveBy(frameTransform.getX() - prevFrameTransform.getX(), frameTransform.getY() - prevFrameTransform.getY());
                    keyFrameActions.addAction(mba);
                }

                if (frameTransform.getScaleX() != null && frameTransform.getScaleY() != null) {
                    ScaleByAction sba = Actions.scaleBy(frameTransform.getScaleX() - prevFrameTransform.getScaleX(), frameTransform.getScaleY() - prevFrameTransform.getScaleY());
                    keyFrameActions.addAction(sba);
                }

                if (frameTransform.getRotation() != null) {
                    RotateByAction rba = Actions.rotateBy(frameTransform.getRotation() - prevFrameTransform.getRotation());
                    keyFrameActions.addAction(rba);
                }

                for (Action action : keyFrameActions.getActions()) {
                    TemporalAction ta = (TemporalAction) action;
                    ta.setInterpolation(mKeyframe.getInterpolation());
                    ta.setDuration(tweenLength);
                }
            } else if (keyframe instanceof VisemeKeyframe && actor instanceof DodlesGroup) {
                final VisemeKeyframe vKeyframe = ((VisemeKeyframe) keyframe);
                final DodlesGroup pg = ((DodlesGroup) actor);
                
                keyFrameActions.addAction(Actions.parallel(
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            originalPhaseID = pg.getVisiblePhaseID();
                            PhaseAttributeType attr = PhaseAttributeType.TALK;
                            PhaseStepType step = PhaseStepType.valueOf((String) vKeyframe.getViseme().toString());
                            pg.setPhaseAttribute(attr, step);
                        }
                    }),
                    Actions.delay(tweenLength)
                ));
            }
            
            prevKeyframe = keyframe;
        }
        
        return sequence;
    }
    
    /**
     * Serializes the effect animator to JSON.
     */
    public final void writeConfig(Json json) {
        json.writeValue("effectID", effectID);
        json.writeValue("actorID", actor.getName());
        json.writeValue("effectType", effectType.name());
        json.writeValue("effectName", effectName);
        effect.writeConfig(json);
        
        json.writeObjectStart("parameters");
        for (EffectParameterType ept : parameters.keySet()) {
            json.writeValue(ept.name(), parameters.get(ept));
        }
        json.writeObjectEnd();
    }

    /**
     * Serializes the effect animator to JSON without the keyframes.
     */
    public final void writeAnimationToJson(Json json) {
        json.writeValue("effectID", effectID);
        json.writeValue("actorID", actor.getName());
        json.writeValue("effectType", effectType.name());
        json.writeValue("effectName", effectName);
        effect.writeConfigWithoutKeyFrames(json);

        json.writeObjectStart("parameters");
        for (EffectParameterType ept : parameters.keySet()) {
            json.writeValue(ept.name(), parameters.get(ept));
        }
        json.writeObjectEnd();
    }
}
