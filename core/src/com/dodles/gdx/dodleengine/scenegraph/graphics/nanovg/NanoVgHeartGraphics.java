package com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRendererType;
import com.dodles.gdx.dodleengine.util.GraphicUtils;
import com.gurella.engine.graphics.vector.Canvas;

/**
 * Draws a rectangle using NanoVG.
 */
public class NanoVgHeartGraphics extends AbstractNanoVgGraphics implements NanoVgGraphics {
    private final Vector2 startPoint;
    private final Vector2 leftPoint;
    private final Vector2 leftArcPoint; 
    private final Vector2 rightPoint;
    private final Vector2 rightArcPoint;
    private final Vector2 endPoint;
    
    private Rectangle bounds;
    
    public NanoVgHeartGraphics(Vector2 startPoint, Vector2 leftPoint, Vector2 leftArcPoint, Vector2 rightPoint, Vector2 rightArcPoint, Vector2 endPoint, float strokeSize, Color strokeColor, Color fillColor, boolean useHalfStrokeDraw) {
        super(strokeSize, strokeColor, fillColor, useHalfStrokeDraw);
        
        this.startPoint = startPoint.cpy();
        this.leftPoint = leftPoint.cpy();
        this.leftArcPoint = leftArcPoint.cpy();
        this.rightPoint = rightPoint.cpy();
        this.rightArcPoint = rightArcPoint.cpy();
        this.endPoint = endPoint.cpy();
    }
    
    @Override
    public final GraphicsRendererType getRendererType() {
        return GraphicsRendererType.NanoVg;
    }
    
    @Override
    public final void draw(Canvas canvas) {
        configureCanvas(canvas);
        canvas.drawHeart(startPoint, leftPoint, leftArcPoint, rightPoint, rightArcPoint, endPoint);
    }
    
    @Override
    public final Rectangle getBounds() {
        if (bounds == null) {
            Vector2[] corners = {
                this.startPoint,
                this.leftPoint,
                this.leftArcPoint,
                this.rightPoint,
                this.rightArcPoint,
                this.endPoint
            };
            float minX = GraphicUtils.getMin(corners, corners.length, -1, 'x');
            float minY = GraphicUtils.getMin(corners, corners.length, -1, 'y');
            float maxX = GraphicUtils.getMax(corners, corners.length, -1, 'x');
            float maxY = GraphicUtils.getMax(corners, corners.length, -1, 'y');

            bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
        }
        
        return bounds;
    }
}
