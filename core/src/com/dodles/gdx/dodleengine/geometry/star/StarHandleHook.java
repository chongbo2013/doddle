package com.dodles.gdx.dodleengine.geometry.star;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.geometry.AbstractPolygonHandleHook;
import com.dodles.gdx.dodleengine.scenegraph.Shape;

/**
 * Handle hook implementation for the star tool.
 */
public class StarHandleHook extends AbstractPolygonHandleHook {
    public StarHandleHook(Shape shape, int corner) {
        super(shape, corner);
    }
    
    @Override
    public final Vector2 getPosition() {
        StarGeometryConfig cfg = (StarGeometryConfig) getShape().getCustomConfig();
        int cornerPos = getCornerPos();
        int depthOffset = 0;
        Vector2 p = cfg.getPoint();
        
        if (cfg.getScales()[cornerPos] == 0) {
            cfg.getScales()[cornerPos] = 1;
        }
        
        if (cfg.getAngles()[cornerPos] == 0) {
            cfg.getAngles()[cornerPos] = getDefaultAngle();
        }

        // only add the depth offset initially
        if (cfg.getScales()[cornerPos] == 1) {
            //CHECKSTYLE.OFF: AvoidInlineConditionals - short and appropriate use of inlining
            depthOffset = (cornerPos % 2 == 1 ? cfg.getDepth() : 0);
            //CHECKSTYLE.ON
        }
        return new Vector2(
            p.x + (((cfg.getSize() * cfg.getScales()[cornerPos]) - depthOffset) * (float) Math.cos(cfg.getAngles()[cornerPos] - cfg.getRotationRadians())),
            p.y + (((cfg.getSize() * cfg.getScales()[cornerPos]) - depthOffset) * (float) Math.sin((cfg.getAngles()[cornerPos] - cfg.getRotationRadians()) * -1))
        );
    }
}
