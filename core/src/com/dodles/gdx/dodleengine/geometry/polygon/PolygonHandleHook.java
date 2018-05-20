package com.dodles.gdx.dodleengine.geometry.polygon;

import com.dodles.gdx.dodleengine.geometry.AbstractPolygonHandleHook;
import com.dodles.gdx.dodleengine.scenegraph.Shape;

/**
 * Handle hook implementation for the polygon tool.
 */
public class PolygonHandleHook extends AbstractPolygonHandleHook {
    public PolygonHandleHook(Shape shape, int corner) {
        super(shape, corner);
    }
}
