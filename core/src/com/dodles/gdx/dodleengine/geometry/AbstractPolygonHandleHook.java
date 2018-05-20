package com.dodles.gdx.dodleengine.geometry;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.Shape;

/**
 * Core functionality for polygon handle hooks.
 */
public abstract class AbstractPolygonHandleHook implements HandleHook {
    private final Shape shape;
    private final int cornerPos;
    private float defaultAngle;
    
    public AbstractPolygonHandleHook(Shape shape, int corner) {
        this.shape = shape;
        this.cornerPos = corner;
        
        AbstractPolygonGeometryConfig cfg = (AbstractPolygonGeometryConfig) shape.getCustomConfig();
        defaultAngle = (((360f / (float) cfg.getNumPoints()) * (float) this.cornerPos) - 270) * (float) (Math.PI / 180);
    }
    
    //CHECKSTYLE.OFF: DesignForExtension - we really want a base implementation that can be overridden.
    @Override
    //CHECKSTYLE.ON
    public Vector2 getPosition() {
        AbstractPolygonGeometryConfig cfg = (AbstractPolygonGeometryConfig) shape.getCustomConfig();
        Vector2 p = cfg.getPoint();
        
        if (cfg.getScales()[cornerPos] == 0) {
            cfg.getScales()[cornerPos] = 1;
        }
        
        if (cfg.getAngles()[cornerPos] == 0) {
            cfg.getAngles()[cornerPos] = defaultAngle;
        }

        return new Vector2(
            p.x + (float) ((cfg.getSize() * cfg.getScales()[cornerPos]) * Math.cos(cfg.getAngles()[cornerPos] - cfg.getRotationRadians())),
            p.y + (float) ((cfg.getSize() * cfg.getScales()[cornerPos]) * Math.sin((cfg.getAngles()[cornerPos] - cfg.getRotationRadians()) * -1))
        );
    }

    //CHECKSTYLE.OFF: DesignForExtension - we really want a base implementation that can be overridden.
    @Override
    //CHECKSTYLE.ON
    public void setPosition(Vector2 newPoint) {
        AbstractPolygonGeometryConfig cfg = (AbstractPolygonGeometryConfig) shape.getCustomConfig();
        
        DistanceAngle st = snappingTool(cfg, newPoint);
        
        cfg.getScales()[cornerPos] = (float) st.getDistance() / cfg.getSize();
        cfg.getAngles()[cornerPos] = (float) st.getAngle() + cfg.getRotationRadians();
    }
    
    /**
     * snappingTool ported from angularJS.
     * @param cfg
     * @param point
     * @return
     */
    protected final DistanceAngle snappingTool(AbstractPolygonGeometryConfig cfg, Vector2 point) {
        float distance = cfg.getPoint().dst(point);
        
        // see comments in AbstractBrush.java for why we have to calculate this on our own
        //float currentAngle = cfg.getPoint().angle(point) - (float)(Math.PI / 2);
        float angle = (float) Math.atan2(point.x - cfg.getPoint().x, point.y - cfg.getPoint().y);
        float currentAngle = angle - (float) (Math.PI / 2);
        float finalDefaultAngle = defaultAngle - cfg.getRotationRadians();
        
        
        // Snap to angle
        if (Math.abs(currentAngle - finalDefaultAngle) < 0.1 || Math.abs(finalDefaultAngle - currentAngle) < 0.1) {
            currentAngle = (float) finalDefaultAngle;
        }
        
        // Snap to size
        if (Math.abs(distance - cfg.getSize()) < 10) {
            distance = cfg.getSize();
        }
        
        /*float minX = GraphicUtils.getMin(cfg.getCorners(), cfg.getNumPoints(), cornerPos, 'x');
        float maxX = GraphicUtils.getMax(cfg.getCorners(), cfg.getNumPoints(), cornerPos, 'x');
        float minY = GraphicUtils.getMin(cfg.getCorners(), cfg.getNumPoints(), cornerPos, 'y');
        float maxY = GraphicUtils.getMax(cfg.getCorners(), cfg.getNumPoints(), cornerPos, 'y');
        
        if (Math.abs(point.x - cfg.getPoint().x) < 8 || Math.abs(cfg.getPoint().x - point.x) < 8) {
            if (point.y < cfg.getPoint().y) {
                currentAngle = (float) Math.PI / 2;
            } else {
                currentAngle = (float) (3 * Math.PI) / 2;
            }
        }

        if (Math.abs(point.y - cfg.getPoint().y) < 8 || Math.abs(cfg.getPoint().y - point.y) < 8) {
            if (point.x < cfg.getPoint().x) {
                currentAngle = (float) Math.PI;
            } else {
                currentAngle = 2 * (float) Math.PI;
            }
        }

        if (Math.abs(minX - point.x) < 8 || Math.abs(point.x - minX) < 8) {
            distance = cfg.getPoint().dst(new Vector2(minX, point.y)); 
        } else if (Math.abs(maxX - point.x) < 8 || Math.abs(point.x - maxX) < 8) {
            distance = cfg.getPoint().dst(new Vector2(maxX, point.y)); 
        }

        if (Math.abs(minY - point.y) < 8 || Math.abs(point.y - minY) < 8) {
            distance = cfg.getPoint().dst(new Vector2(point.x, minY));
        } else if (Math.abs(maxY - point.y) < 8 || Math.abs(point.y - maxY) < 8) {
            distance = cfg.getPoint().dst(new Vector2(point.x, maxY));
        }*/
        
        
        return new DistanceAngle(distance, currentAngle);
    }

    /**
     * simple pojo for ease of porting.
     * @author marknickel
     *
     */
    public class DistanceAngle {
        private final double distance;
        private final double angle;
        
        public DistanceAngle(double dist, double a) {
            this.distance = dist;
            this.angle = a;
        }

        /**
         * get distance.
         * @return
         */
        public final double getDistance() {
            return distance;
        }

        /**
         * get angle.
         * @return
         */
        public final double getAngle() {
            return angle;
        }
    }

    /**
     * get the shape associated with this handle.
     * @return
     */
    public final Shape getShape() {
        return shape;
    }

    /**
     * get the corner position for this handle.
     * @return
     */
    public final int getCornerPos() {
        return cornerPos;
    }

    /**
     * get the default angle.
     * @return
     */
    public final float getDefaultAngle() {
        return defaultAngle;
    }

    /** 
     * set the default angle.
     * @param defaultAngle
     */
    public final void setDefaultAngle(float defaultAngle) {
        this.defaultAngle = defaultAngle;
    }
}
