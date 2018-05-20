package com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRendererType;
import com.gurella.engine.graphics.vector.Canvas;

/**
 * Draws a circle using NanoVG.
 */
public class NanoVgPolygonGraphics extends AbstractNanoVgGraphics implements NanoVgGraphics {
    private final Vector2[] corners;
    private final float cornerRadius;
    
    private Rectangle bounds;
    
    public NanoVgPolygonGraphics(Vector2[] points, float radius, float strokeSize, Color strokeColor, Color fillColor, boolean useHalfStrokeDraw) {
        super(strokeSize, strokeColor, fillColor, useHalfStrokeDraw);
        
        this.cornerRadius = radius;
        this.corners = new Vector2[points.length];
        
        for (int i = 0; i < points.length; i++) {
            this.corners[i] = points[i].cpy();
        }
    }
    
    @Override
    public final GraphicsRendererType getRendererType() {
        return GraphicsRendererType.NanoVg;
    }
    
    @Override
    public final void draw(Canvas canvas) {
        configureCanvas(canvas);
        canvas.drawRoundedPolygon(corners, cornerRadius);
    }
    
    @Override
    public final Rectangle getBounds() {
        if (bounds == null) {
            float pad = (float) getStrokeSize() / 2f;
            float finalMinX = 0f, finalMinY = 0f, finalMaxX = 0f, finalMaxY = 0f;
            float minX = 0f, minY = 0f, maxX = 0f, maxY = 0f;

            finalMinX = corners[0].x;
            finalMinY = corners[0].y;
            finalMaxX = corners[0].x;
            finalMaxY = corners[0].y;

            for (int i = 0; i < corners.length; i++) {
                minX = corners[i].x;
                minY = corners[i].y;
                maxX = corners[i].x;
                maxY = corners[i].y;

                finalMinX = Math.min(finalMinX, minX);
                finalMinY = Math.min(finalMinY, minY);
                finalMaxX = Math.max(finalMaxX, maxX);
                finalMaxY = Math.max(finalMaxY, maxY);
            }
            bounds = new Rectangle(finalMinX - pad, finalMinY - pad, finalMaxX - finalMinX + pad * 2, finalMaxY - finalMinY + pad * 2);
        }
        
        return bounds;
    }
}
