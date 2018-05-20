package com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg;

import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.gurella.engine.graphics.vector.Canvas;

/**
 * A graphics command that's drawn using NanoVG.
 */
public interface NanoVgGraphics extends Graphics {
    /**
     * Draws the command using NanoVG.
     */
    void draw(Canvas canvas);
}
