package com.dodles.gdx.dodleengine.scenegraph;

import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import java.util.List;

/**
 * Generates graphics commands for a given shape (allows graphics commands to be
 * regenerated when information about a shape has changed).
 */
public interface GraphicsGenerator {
    /**
     * Generates the graphics for the given shape.
     */
    List<Graphics> generateGraphics(Shape shape);
}
