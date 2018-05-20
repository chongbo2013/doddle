package com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.DrawingStyle;

/**
 * Common functionality for NanoVG graphics implementations.
 */
public class AbstractNanoVgGraphics {
    private float strokeSize;
    private Color strokeColor;
    private Color fillColor;
    private boolean useHalfStrokeDraw = false;
    
    public AbstractNanoVgGraphics(float strokeSize, Color strokeColor, Color fillColor, boolean useHalfStrokeDraw) {
        this.strokeSize = strokeSize;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
        this.useHalfStrokeDraw = useHalfStrokeDraw;
    }
    
    /**
     * Returns the stroke size.
     */
    public final float getStrokeSize() {
        return strokeSize;
    }
    
    /**
     * Returns the stroke color.
     */
    public final Color getStrokeColor() {
        return strokeColor;
    }
    
    /**
     * Returns the fill color.
     */
    public final Color getFillColor() {
        return fillColor;
    }
    
    /**
     * Configures the canvas for the draw operation.
     */
    protected final void configureCanvas(Canvas canvas) {
        canvas.setDrawingStyle(DrawingStyle.fill);
        
        if (strokeSize > 0) {
            canvas.setDrawingStyle(DrawingStyle.fillAndStroke);
            canvas.setStrokeWidth(strokeSize);
            canvas.setStrokeColor(strokeColor);
        }
        
        canvas.setFillColor(fillColor);
    }
}
