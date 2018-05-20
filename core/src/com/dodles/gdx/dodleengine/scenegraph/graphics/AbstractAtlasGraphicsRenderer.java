package com.dodles.gdx.dodleengine.scenegraph.graphics;

import com.badlogic.gdx.math.Rectangle;
import java.util.Collection;
import java.util.HashSet;

/**
 * Common methods for atlas graphics renderers.
 */
public abstract class AbstractAtlasGraphicsRenderer<T extends Graphics> extends AbstractGraphicsRenderer<T> {
    private int cachedFrom = -1;
    private int cachedTo = -1;
    private HashSet<AtlasOffset> cachedResult = new HashSet<AtlasOffset>();
    
    /**
     * Returns new offsets that need to be drawn since the last time drawing occurred.
     */
    public final Collection<AtlasOffset> getNewOffsetsToDrawTo() {        
        int from = getGraphicsIndex();
        int to = getGraphics().size();
        
        if (from == cachedFrom && to == cachedTo) {
            return cachedResult;
        }
        
        cachedResult.clear();
        cachedFrom = from;
        cachedTo = to;
        
        for (int i = from; i < to; i++) {
            Rectangle bounds = getGraphics().get(i).getBounds();            
            
            // TODO: This is inefficent and can allocate more tiles than necessary for the shape!
            for (float x = bounds.x; x < bounds.x + bounds.width; x += AtlasOffset.RENDERED_SIZE) {
                float y;
                
                for (y = bounds.y; y < bounds.y + bounds.height; y += AtlasOffset.RENDERED_SIZE) {
                    cachedResult.add(new AtlasOffset(x, y));
                }
                
                cachedResult.add(new AtlasOffset(x, bounds.y + bounds.height));
                
            }
            
            for (float y = bounds.y; y < bounds.y + bounds.height; y += AtlasOffset.RENDERED_SIZE) {
                cachedResult.add(new AtlasOffset(bounds.x + bounds.width, y));
            }
            
            cachedResult.add(new AtlasOffset(bounds.x + bounds.width, bounds.y + bounds.height));
        }
        
        return cachedResult;
    }
    
    /**
     * Returns the current index of the last drawn graphics element.
     */
    protected abstract int getGraphicsIndex();
}
