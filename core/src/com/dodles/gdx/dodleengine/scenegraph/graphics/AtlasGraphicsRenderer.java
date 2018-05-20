package com.dodles.gdx.dodleengine.scenegraph.graphics;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.DodlesSpriteBatch;
import java.util.Collection;

/**
 * A graphics renderer that can render to an atlas.
 */
public interface AtlasGraphicsRenderer extends GraphicsRenderer {
    /**
     * Returns new offsets to draw to since the previous time commit was called.
     */
    Collection<AtlasOffset> getNewOffsetsToDrawTo();
    
    /**
     * Draws new graphics commands since the last time commit was called.
     */
    void drawNew(DodlesSpriteBatch batch, float parentAlpha, Vector2 offset);
    
    /**
     * Updates the draw tracking pointer.
     */
    void commit();
}
