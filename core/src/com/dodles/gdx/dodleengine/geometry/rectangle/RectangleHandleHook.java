package com.dodles.gdx.dodleengine.geometry.rectangle;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.geometry.AbstractPolygonHandleHook;
import com.dodles.gdx.dodleengine.geometry.HandleHook;
import com.dodles.gdx.dodleengine.scenegraph.Shape;

/**
 * A handle hook for rectangle geometry.
 */
public class RectangleHandleHook extends AbstractPolygonHandleHook {
    public RectangleHandleHook(Shape shape, int corner) {
        super(shape, corner);
    }
}
