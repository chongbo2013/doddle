package com.dodles.gdx.dodleengine.geometry.custom;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.geometry.HandleHook;
import com.dodles.gdx.dodleengine.scenegraph.Shape;

public class CustomHandleHook implements HandleHook {
    private final Shape shape;
    private final int xDir;
    private final int yDir;
    private final CustomGeometryConfig.Axis axis;

    public CustomHandleHook(Shape shape, int xDir, int yDir, CustomGeometryConfig.Axis axis) {
        this.shape = shape;
        this.xDir = xDir;
        this.yDir = yDir;
        this.axis = axis;
    }

    @Override
    public final Vector2 getPosition() {
        CustomGeometryConfig cfg = (CustomGeometryConfig) shape.getCustomConfig();
        Vector2 p = cfg.getPoint();

        Vector2 result = new Vector2(
            (cfg.getSize() * cfg.getAxisRatio(axis)) * xDir,
            (cfg.getSize() * cfg.getAxisRatio(axis)) * yDir
        );

        result.rotate(cfg.getRotation());
        result.x += p.x;
        result.y += p.y;

        return result;
    }

    @Override
    public final void setPosition(Vector2 newPoint) {
        CustomGeometryConfig cfg = (CustomGeometryConfig) shape.getCustomConfig();
        Vector2 centerPoint = cfg.getPoint();
        float newValue;
        
        Vector2 diff = newPoint.sub(centerPoint.cpy());
        diff.rotate(-cfg.getRotation());
        
        if (xDir == 0) {
            newValue = (diff.y * yDir) / cfg.getSize();
        } else {
            newValue = (diff.x * xDir) / cfg.getSize();
        }

        float snapDistance = 0.4f;
        if (newValue > 1 - snapDistance && newValue < 1 + snapDistance) {
            // "snap" when we're close to the standard point
            newValue = 1;
        } else if (newValue < 0) {
            newValue = 0;
        }

        cfg.setAxisRatio(axis, newValue);
    }
}
