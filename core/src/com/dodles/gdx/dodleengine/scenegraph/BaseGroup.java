package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Some common alterations to how libGDX groups work that should apply to
 * all group subclasses.  However, extending this class directly means that
 * the actor WILL NOT live in the dodles scene graph!
 */
public class BaseGroup extends Group {
    private final Affine2 worldTransform = new Affine2();
    private final Matrix4 computedTransform = new Matrix4();
    private Affine2 worldTransformOverride;

    /**
     * Returns the world transform, or the world transform override if set.
     */
    protected final Affine2 getWorldTransform() {
        if (worldTransformOverride != null) {
            return worldTransformOverride;
        }

        return worldTransform;
    }

    /**
     * Sets the world transform override to use instead of computing the transform.
     */
    public final void setWorldTransformOverride(Affine2 override) {
        worldTransformOverride = override;
    }

    @Override
    protected final Matrix4 computeTransform() {
        if (worldTransformOverride != null) {
            computedTransform.set(worldTransformOverride);
            return computedTransform;
        }

        return computeTransform(worldTransform, computedTransform, this);
    }

    /**
     * Computes the transform of the actor, worldTransform and computedTransform are
     * passed in to prevent unnecessary allocations.
     */
    public static Matrix4 computeTransform(Affine2 worldTransform, Matrix4 computedTransform, Actor actor) {
        setAffineTransform(worldTransform, actor.getX(), actor.getY(), actor.getOriginX(), actor.getOriginY(), actor.getRotation(), actor.getScaleX(), actor.getScaleY());

        // Find the first parent that transforms.
        Group parentGroup = actor.getParent();
        while (parentGroup != null) {
            if (parentGroup.isTransform()) {
                break;
            }

            parentGroup = parentGroup.getParent();
        }

        if (parentGroup != null && parentGroup instanceof BaseGroup) {
            worldTransform.preMul(((BaseGroup) parentGroup).getWorldTransform());
        }

        computedTransform.set(worldTransform);
        return computedTransform;
    }

    /**
     * Updates the affine transform with the passed parameters.
     */
    public static void setAffineTransform(Affine2 trn, float x, float y, float originX, float originY, float rotation, float scaleX, float scaleY) {
        trn.setToTrnRotScl(x + originX, y + originY, rotation, scaleX, scaleY);

        if (originX != 0 || originY != 0) {
            trn.translate(-originX, -originY);
        }
    }
}
