package com.dodles.gdx.dodleengine.scenegraph.graphics;

import com.badlogic.gdx.math.Rectangle;
import java.util.List;

/**
 * Common methods for graphics renderers.
 */
public abstract class AbstractGraphicsRenderer<T extends Graphics> {
    
    /**
     * Returns the draw bounds for all graphics contained within the renderer.
     */
    public final Rectangle getDrawBounds() {
        Rectangle drawBounds = null;
        
        for (Graphics g : getGraphics()) {
            if (drawBounds == null) {
                drawBounds = g.getBounds();
            } else {
                drawBounds = drawBounds.merge(g.getBounds());
            }
        }

        if (drawBounds == null) {
            return null;
        }
        
        // Send back a clone rectangle
        return drawBounds;
    }
    
    /**
     * Returns the graphics being rendered.
     */
    protected abstract List<T> getGraphics();
}
