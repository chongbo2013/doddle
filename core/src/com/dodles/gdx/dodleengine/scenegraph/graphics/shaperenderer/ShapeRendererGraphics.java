package com.dodles.gdx.dodleengine.scenegraph.graphics.shaperenderer;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;

/**
 * A graphics command that's drawn using the shape renderer.
 */
public interface ShapeRendererGraphics extends Graphics {
    /**
     * Draws the command using the ShapeRenderer. 
     */
    void draw(ShapeRenderer shapeRenderer);
}
