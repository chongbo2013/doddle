package com.dodles.gdx.dodleengine.geometry.heart;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.geometry.HandleHook;
import com.dodles.gdx.dodleengine.scenegraph.Shape;

/**
 * A handle hook for heart geometry.
 */
public class HeartHandleHook implements HandleHook {
    private final Shape shape;
    private final int xDir;
    private final int yDir;
    private final HeartGeometryConfig.Axis axis;
    
    public HeartHandleHook(Shape shape, int xDir, int yDir, HeartGeometryConfig.Axis axis) {
        this.shape = shape;
        this.xDir = xDir;
        this.yDir = yDir;
        this.axis = axis;
    }
    
    @Override
    public final Vector2 getPosition() {
        HeartGeometryConfig cfg = (HeartGeometryConfig) shape.getCustomConfig();
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
        HeartGeometryConfig cfg = (HeartGeometryConfig) shape.getCustomConfig();
        Vector2 oldPos = getPosition();
        float increment;
        
        Vector2 diff = newPoint.sub(oldPos.cpy());
        diff.rotate(-cfg.getRotation());
        
        if (xDir == 0) {
            increment = ((diff.y) / 2 * yDir) / cfg.getSize();
        } else {
            increment = ((diff.x) / 2 * xDir) / cfg.getSize();
        }
        
        float newValue = cfg.getAxisRatio(axis) + increment;
        float snapDistance = 0.2f;
        
        if (newValue > 1 - snapDistance && newValue < 1 + snapDistance) {
            // "snap" when we're close to the standard point
            increment = 1 - cfg.getAxisRatio(axis);
            newValue = 1;
        } else if (newValue < 0) {
            increment = 0 - cfg.getAxisRatio(axis);
            newValue = 0;
        }
        
        Vector2 pointIncrement;
        if (xDir == 0) {
            pointIncrement = new Vector2(0, increment * cfg.getSize() * yDir);
        } else {
            pointIncrement = new Vector2(increment * cfg.getSize() * xDir, 0);
        }
        
        pointIncrement.rotate(cfg.getRotation());
        cfg.getPoint().add(pointIncrement);
        
        cfg.setAxisRatio(axis, newValue);
    }
}
