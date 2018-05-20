package com.dodles.gdx.dodleengine.scenegraph.graphics.shaperenderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRendererType;

/**
 * Draws a rectangle using the ShapeRenderer.
 */
public class ShapeRendererRectangleGraphics implements ShapeRendererGraphics {
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final Color color;
    
    private Rectangle bounds;
    
    public ShapeRendererRectangleGraphics(Rectangle rect, Color color) {
        this(rect.x, rect.y, rect.width, rect.height, color);
    }
    
    public ShapeRendererRectangleGraphics(float x, float y, float width, float height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }
    
    @Override
    public final GraphicsRendererType getRendererType() {
        return GraphicsRendererType.ShapeRenderer;
    }
    
    @Override
    public final void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, width, height);
    }
    
    @Override
    public final Rectangle getBounds() {
        if (bounds == null) {
            bounds = new Rectangle(this.x, this.y, this.width, this.height);            
        }
        
        return bounds;
    }
}
