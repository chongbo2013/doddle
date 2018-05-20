package com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRendererType;
import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.CubicBezierCurve;

/**
 * Draws a circle using NanoVG.
 */
public class NanoVgCircleGraphics extends AbstractNanoVgGraphics implements NanoVgGraphics {
    private final List<CubicBezierCurve> curves;
    
    private Rectangle bounds;
    
    public NanoVgCircleGraphics(Vector2 center, List<Vector2> points, float strokeSize, Color strokeColor, Color fillColor, boolean useHalfStrokeDraw) {
        super(strokeSize, strokeColor, fillColor, useHalfStrokeDraw);

        this.curves = new ArrayList<CubicBezierCurve>();
        this.curves.add(createBezierCurveQuadrant(points.get(0), points.get(1), center, false));
        this.curves.add(createBezierCurveQuadrant(points.get(1), points.get(2), center, true));
        this.curves.add(createBezierCurveQuadrant(points.get(2), points.get(3), center, true));
        this.curves.add(createBezierCurveQuadrant(points.get(3), points.get(0), center, true));
    }

    public NanoVgCircleGraphics(Vector2 center, float radiusX, float radiusY, float strokeSize, Color strokeColor, Color fillColor, boolean useHalfStrokeDraw) {
        super(strokeSize, strokeColor, fillColor, useHalfStrokeDraw);

        ArrayList<Vector2> points = new ArrayList<Vector2>();
        points.add(new Vector2(center.x + radiusX, center.y));
        points.add(new Vector2(center.x, center.y + radiusY));
        points.add(new Vector2(center.x - radiusX, center.y));
        points.add(new Vector2(center.x, center.y - radiusY));

        this.curves = new ArrayList<CubicBezierCurve>();
        this.curves.add(createBezierCurveQuadrant(points.get(0), points.get(1), center, false));
        this.curves.add(createBezierCurveQuadrant(points.get(1), points.get(2), center, true));
        this.curves.add(createBezierCurveQuadrant(points.get(2), points.get(3), center, true));
        this.curves.add(createBezierCurveQuadrant(points.get(3), points.get(0), center, true));
    }

        @Override
    public final GraphicsRendererType getRendererType() {
        return GraphicsRendererType.NanoVg;
    }
    
    @Override
    public final void draw(Canvas canvas) {
        configureCanvas(canvas);
        canvas.drawCubicBezierCurves(curves);
    }
    
    @Override
    public final Rectangle getBounds() {
        if (bounds == null) {
            float pad = (float) getStrokeSize() / 2f;
            float finalMinX = 0f, finalMinY = 0f, finalMaxX = 0f, finalMaxY = 0f;
            float minX = 0f, minY = 0f, maxX = 0f, maxY = 0f;

            finalMinX = Math.min(curves.get(0).getEp0().x, Math.min(curves.get(0).getCp0().x, Math.min(curves.get(0).getCp1().x, curves.get(0).getEp1().x)));
            finalMinY = Math.min(curves.get(0).getEp0().y, Math.min(curves.get(0).getCp0().y, Math.min(curves.get(0).getCp1().y, curves.get(0).getEp1().y)));
            finalMaxX = Math.max(curves.get(0).getEp0().x, Math.max(curves.get(0).getCp0().x, Math.max(curves.get(0).getCp1().x, curves.get(0).getEp1().x)));
            finalMaxY = Math.max(curves.get(0).getEp0().y, Math.max(curves.get(0).getCp0().y, Math.max(curves.get(0).getCp1().y, curves.get(0).getEp1().y)));

            for (CubicBezierCurve c : curves) {
                minX = Math.min(c.getEp0().x, Math.min(c.getCp0().x, Math.min(c.getCp1().x, c.getEp1().x)));
                minY = Math.min(c.getEp0().y, Math.min(c.getCp0().y, Math.min(c.getCp1().y, c.getEp1().y)));
                maxX = Math.max(c.getEp0().x, Math.max(c.getCp0().x, Math.max(c.getCp1().x, c.getEp1().x)));
                maxY = Math.max(c.getEp0().y, Math.max(c.getCp0().y, Math.max(c.getCp1().y, c.getEp1().y)));

                finalMinX = Math.min(finalMinX, minX);
                finalMinY = Math.min(finalMinY, minY);
                finalMaxX = Math.max(finalMaxX, maxX);
                finalMaxY = Math.max(finalMaxY, maxY);
            }

            bounds = new Rectangle(finalMinX - pad, finalMinY - pad, finalMaxX - finalMinX + pad * 2, finalMaxY - finalMinY + pad * 2);
        }
        
        return bounds;
    }

    private CubicBezierCurve createBezierCurveQuadrant(Vector2 point0, Vector2 point1, Vector2 centerp, boolean isContinuation) {
        Vector2 v0 = point0.cpy().sub(centerp);
        Vector2 v1 = point1.cpy().sub(centerp);
        
        float cpLen0 = getControlPointLength(v0.len());
        float cpLen1 = getControlPointLength(v1.len());

        return new CubicBezierCurve(
                point0.cpy().add(v1.setLength(cpLen1)),
                point1.cpy().add(v0.setLength(cpLen0)),
                point0,
                point1,
                isContinuation);
    }

    private float getControlPointLength(float size) {
        // http://stackoverflow.com/questions/1734745/how-to-create-circle-with-b%C3%A9zier-curves
        return (float) (size * 4 * (Math.sqrt(2) - 1) / 3);
    }
}
