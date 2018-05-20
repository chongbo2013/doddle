package com.dodles.gdx.dodleengine.scenegraph.graphics.shaperenderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRendererType;

/**
 * Renders a quadratic curve using the ShapeRenderer.
 */
public class ShapeRendererQuadraticCurveGraphics implements ShapeRendererGraphics {
    private final Vector2[] dv  = new Vector2[4];
    private final Vector2[] bezierPoints = new Vector2[3];
    private final Vector2[] points;
    private final Vector2[] tangents;
    private final float strokeSize;
    private final Color color;
    
    private Rectangle bounds;
    
    public ShapeRendererQuadraticCurveGraphics(Vector2 p0, Vector2 cp, Vector2 p1, float strokeSize, Color color) {
        bezierPoints[0] = p0;
        bezierPoints[1] = cp;
        bezierPoints[2] = p1;
        this.strokeSize = strokeSize;
        this.color = color;
        
        Bezier<Vector2> curve = new Bezier<Vector2>(bezierPoints);
        float length = curve.approxLength(5);
        
        // TODO: better way to figure out how many samples we want?
        int numSamples = Math.max(3, (int) (length / 2f));
        points = new Vector2[numSamples];
        tangents = new Vector2[numSamples];
        
        for (int i = 0; i < numSamples; i++) {
            points[i] = new Vector2();
            tangents[i] = new Vector2();
            float position = ((float) i) / ((float) numSamples - 1);
            
            curve.valueAt(points[i], position);
            curve.derivativeAt(tangents[i], position);
            tangents[i].set(-tangents[i].y, tangents[i].x);
        }
        
        for (int i = 0; i < dv.length; i++) {
            dv[i] = new Vector2();
        }
    }
    
    @Override
    public final GraphicsRendererType getRendererType() {
        return GraphicsRendererType.ShapeRenderer;
    }
    
    @Override
    public final void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        
        float width = (float) strokeSize / 2f;
        tangents[0].setLength(width);
        
        for (int i = 1; i < points.length; i++) {
            tangents[i].setLength(width);
            dv[0].set(points[i - 1].x + tangents[i - 1].x, points[i - 1].y + tangents[i - 1].y);
            dv[1].set(points[i - 1].x - tangents[i - 1].x, points[i - 1].y - tangents[i - 1].y);
            dv[2].set(points[i].x + tangents[i].x, points[i].y + tangents[i].y);
            dv[3].set(points[i].x - tangents[i].x, points[i].y - tangents[i].y);
            
            shapeRenderer.triangle(dv[0].x, dv[0].y, dv[1].x, dv[1].y, dv[2].x, dv[2].y);
            shapeRenderer.triangle(dv[1].x, dv[1].y, dv[2].x, dv[2].y, dv[3].x, dv[3].y);
        }
    }

    @Override
    public final Rectangle getBounds() {
        if (bounds == null) {
            float pad = (float) strokeSize / 2f;
        
            float minX = Math.min(bezierPoints[0].x, Math.min(bezierPoints[1].x, bezierPoints[2].x));
            float minY = Math.min(bezierPoints[0].y, Math.min(bezierPoints[1].y, bezierPoints[2].y));
            float maxX = Math.max(bezierPoints[0].x, Math.max(bezierPoints[1].x, bezierPoints[2].x));
            float maxY = Math.max(bezierPoints[0].y, Math.max(bezierPoints[1].y, bezierPoints[2].y));

            bounds = new Rectangle(minX - pad, minY - pad, maxX - minX + pad * 2f, maxY - minY + pad * 2f);
        }
        
        return bounds;
    }
}
