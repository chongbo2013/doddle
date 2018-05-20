package com.dodles.gdx.dodleengine.scenegraph.graphics.direct;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;

/**
 * A graphics command that draws directly to the batch.
 */
public interface DirectGraphics extends Graphics {
    /**
     * Draws the command. 
     */
    void draw(Batch batch, float parentAlpha);
}
