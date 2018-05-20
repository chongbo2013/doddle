package com.dodles.gdx.dodleengine.geometry;

import com.badlogic.gdx.graphics.Color;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;

/**
 * Base geometry functionality.
 */
public abstract class AbstractGeometry implements Geometry {
    private StrokeConfig defaultStrokeConfig;
    public static final boolean USEHALFSTROKEDRAW = true;
    
    @Override
    public final StrokeConfig getDefaultStrokeConfig() {
        if (defaultStrokeConfig == null) {
            defaultStrokeConfig = new StrokeConfig();
            defaultStrokeConfig.setColor(new Color(0f, 0f, 0f, 1));
            defaultStrokeConfig.setFill(new Color(0f, 0f, 0f, 1));
            defaultStrokeConfig.setSize(0);
        }
        
        return defaultStrokeConfig.cpy();
    }
    
    /**
     * Implements Comparable<Geometry>.
     */
    public final int compareTo(Geometry otherGeometry) {        
        Integer thisOrder = new Integer(getOrder());
        Integer thatOrder = new Integer(otherGeometry.getOrder());
        
        return thisOrder.compareTo(thatOrder);
    }
}
