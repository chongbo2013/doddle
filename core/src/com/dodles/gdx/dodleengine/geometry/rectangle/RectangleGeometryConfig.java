package com.dodles.gdx.dodleengine.geometry.rectangle;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.geometry.AbstractPolygonGeometryConfig;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Configuration for rectangular geometry.
 */
public class RectangleGeometryConfig extends AbstractPolygonGeometryConfig {
    private int defaultReactangleWidth = 200;

    public RectangleGeometryConfig() {
        super(150, 4, 1);
        setType(RectangleGeometry.GEOMETRY_NAME);
    }

    public ArrayList<Vector2> getDefaultCorners() {
        final Vector2 centerPoint = getPoint();
        ArrayList<Vector2> cornerlist = new ArrayList<Vector2>();
        cornerlist.add(new Vector2(centerPoint.x + defaultReactangleWidth/2, centerPoint.y + defaultReactangleWidth/2));
        cornerlist.add(new Vector2(centerPoint.x - defaultReactangleWidth/2, centerPoint.y + defaultReactangleWidth/2));
        cornerlist.add(new Vector2(centerPoint.x - defaultReactangleWidth/2, centerPoint.y -defaultReactangleWidth/2));
        cornerlist.add(new Vector2(centerPoint.x + defaultReactangleWidth/2, centerPoint.y - defaultReactangleWidth/2));
        return cornerlist;
    }


    public RectangleGeometryConfig(JsonValue json) {
        super(json);
    }
}
