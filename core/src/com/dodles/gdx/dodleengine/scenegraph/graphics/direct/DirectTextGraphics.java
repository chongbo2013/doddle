package com.dodles.gdx.dodleengine.scenegraph.graphics.direct;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.dodles.gdx.dodleengine.assets.FontRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRendererType;

/**
 * Renders text graphics.
 */
public class DirectTextGraphics implements DirectGraphics {
    private FontRenderer renderer;

    public DirectTextGraphics(BitmapFont font, String text, float size, Color color) {
        renderer = new FontRenderer(font);
        renderer.setFlipY(true);
        renderer.setColor(color);
        renderer.setFontSize(size);
        renderer.setText(text, 0, 0);
    }
    
    @Override
    public final void draw(Batch batch, float parentAlpha) {
        renderer.draw(batch);
    }

    @Override
    public final GraphicsRendererType getRendererType() {
        return GraphicsRendererType.Direct;
    }

    @Override
    public final Rectangle getBounds() {
        return renderer.getDrawBounds();
    }
}
