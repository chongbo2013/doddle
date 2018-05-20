package com.dodles.gdx.dodleengine.scenegraph.graphics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.dodles.gdx.dodleengine.scenegraph.DodlesSpriteBatch;

/**
 * A renderer that takes graphics commands and translates them to screen drawing commands.
 * @author mike.rosack
 */
public interface GraphicsRenderer extends Disposable {
    /**
     * Draws all graphics contained within the renderer.
     */
    void draw(DodlesSpriteBatch batch, float parentAlpha, Vector2 offset);
    
    /**
     * Return the bounds of all graphics commands contained in the renderer.
     */
    Rectangle getDrawBounds();
    
    /**
     * Adds a new graphics command to the renderer.
     */
    void appendGraphics(Graphics g);
}
