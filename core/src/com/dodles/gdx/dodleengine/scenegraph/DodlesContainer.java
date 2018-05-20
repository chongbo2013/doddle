package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.ui.Container;

/**
 * Extends the normal LibGDX container to be able to work with our transform computations.
 */
public class DodlesContainer extends Container {
    private final Affine2 worldTransform = new Affine2();
    private final Matrix4 computedTransform = new Matrix4();
    
    @Override
    protected final Matrix4 computeTransform() {
        return BaseGroup.computeTransform(worldTransform, computedTransform, this);
    }
}
