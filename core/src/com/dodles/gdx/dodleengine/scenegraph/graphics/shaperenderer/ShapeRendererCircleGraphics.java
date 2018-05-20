package com.dodles.gdx.dodleengine.scenegraph.graphics.shaperenderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRendererType;

/**
 * Draws a circle using the ShapeRenderer.
 */
public class ShapeRendererCircleGraphics implements ShapeRendererGraphics {
    private final float x;
    private final float y;
    private final float radius;
    private final Color color;
    
    private Rectangle bounds;
    
    public ShapeRendererCircleGraphics(float x, float y, float radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public final void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.circle(x, y, radius);
    }

    @Override
    public final GraphicsRendererType getRendererType() {
        return GraphicsRendererType.ShapeRenderer;
    }

    @Override
    public final Rectangle getBounds() {
        if (bounds == null) {
            bounds = new Rectangle(this.x - this.radius, this.y - this.radius, this.radius * 2, this.radius * 2);
        }
        
        return bounds;
    }
}
