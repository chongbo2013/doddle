package com.dodles.gdx.dodleengine.animation;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.scenegraph.Transform;
import java.util.Map;

/**
 * Defines the behavior for a simple keyframe in an animation.
 */
public class MotionKeyframe extends BaseKeyframe {
    public static final String KEYFRAME_TYPE = "Motion";
    
    private Float scalePercent = null;
    private Float rotatePercent = null;
    private Float translateYPercent = null;
    private Float translateXPercent = null;
    private String tween = "";
    
    public MotionKeyframe(JsonValue json) {
        super(json);
        
        if (json.has("rotatePct")) {
            rotatePercent = new Float(json.getFloat("rotatePct"));
        }
        
        if (json.has("scalePct")) {
            scalePercent = new Float(json.getFloat("scalePct"));
        }
        
        if (json.has("translateYPct")) {
            translateYPercent = new Float(json.getFloat("translateYPct"));
        }
        
        if (json.has("translateXPct")) {
            translateXPercent = new Float(json.getFloat("translateXPct"));
        }
        
        if (json.has("tween")) {
            tween = json.getString("tween");
        }
    }

    public MotionKeyframe(float pct, Float rotatePercent, Float scalePercent, Float translateXPercent, Float translateYPercent, String tween) {
        super(pct);

        this.rotatePercent = rotatePercent;
        this.scalePercent = scalePercent;
        this.translateXPercent = translateXPercent;
        this.translateYPercent = translateYPercent;

        if (tween == null) {
            tween = "";
        }
        this.tween = tween;
    }

    @Override
    public final String getType() {
        return KEYFRAME_TYPE;
    }

    /**
     * Returns the percent to scale the actor.
     */
    public final Float getScalePercent() {
        return scalePercent;
    }
    
    /**
     * Returns the percent to rotate the actor.
     */
    public final Float getRotatePercent() {
        return rotatePercent;
    }

    /**
     * Returns the percent to translate the actor in the Y axis.
     */
    public final Float getTranslateYPercent() {
        return translateYPercent;
    }

    /**
     * Returns the percent to translate the actor in the X axis.
     */
    public final Float getTranslateXPercent() {
        return translateXPercent;
    }
    
    /**
     * Calculates the transform that will be caused by this keyframe.
     */
    public final Transform calculateTransform(Map<EffectParameterType, Object> parameters) {
        Transform result = new Transform(true);
        
        if (translateXPercent != null || translateYPercent != null) {                
            float xPos = 0;
            float yPos = 0;

            if (translateXPercent != null) {
                float translateX = (Float) parameters.get(EffectParameterType.TRANSLATE_X);
                xPos = translateX * translateXPercent * 0.01f;
            }

            if (translateYPercent != null) {
                float translateY = (Float) parameters.get(EffectParameterType.TRANSLATE_Y);
                yPos = translateY * translateYPercent * 0.01f;
            }
            
            result.setX(xPos);
            result.setY(yPos);
        }
        
        if (scalePercent != null) {
            float scale = (Float) parameters.get(EffectParameterType.SCALE);            
            result.setScaleX(1 + scale * scalePercent * 0.01f);
            result.setScaleY(1 + scale * scalePercent * 0.01f);
        }

        if (rotatePercent != null) {
            result.setRotation((Float) parameters.get(EffectParameterType.DEGREES) * rotatePercent * 0.01f);
        }
        
        return result;
    }

    /**
     * Returns the interpolation to use for the duration of the keyframe.
     */
    public final Interpolation getInterpolation() {
        if (tween.equals("circIn")) {
            return Interpolation.circleIn;
        } else if (tween.equals("circOut")) {
            return Interpolation.circleOut;
        } else if (tween.equals("sineInOut")) {
            return Interpolation.sine;
        }
        
        return Interpolation.linear;
    }

    @Override
    public final void onWriteConfig(Json json) {
        if (scalePercent != null) {
            json.writeValue("scalePct", scalePercent);
        }
        if (rotatePercent != null) {
            json.writeValue("rotatePct", rotatePercent);
        }
        if (translateYPercent != null) {
            json.writeValue("translateYPct", translateYPercent);
        }
        if (translateXPercent != null) {
            json.writeValue("translateXPct", translateXPercent);
        }
        if (tween != null) {
            json.writeValue("tween", tween);
        }
    }
}
