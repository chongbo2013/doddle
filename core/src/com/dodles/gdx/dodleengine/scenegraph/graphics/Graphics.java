package com.dodles.gdx.dodleengine.scenegraph.graphics;

import com.badlogic.gdx.math.Rectangle;

/**
 * Represents an arbitrary 2D draw command to be executed.
 */
public interface Graphics {
    /**
     * Returns the type of renderer needed to render this graphics command.
     */
    GraphicsRendererType getRendererType();
    
    /**
     * Returns the boundaries of the command.
     */
    Rectangle getBounds();
}
