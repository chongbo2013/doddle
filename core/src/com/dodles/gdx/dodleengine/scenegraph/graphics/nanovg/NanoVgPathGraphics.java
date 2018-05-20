package com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRendererType;
import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * A graphics object that utilized a given path object.
 */

public class NanoVgPathGraphics extends AbstractNanoVgGraphics implements NanoVgGraphics {
    private final Path path;
    private final Vector2 originallOrigin = new Vector2(375   , 375);
    private final float originalSize = 150;
    private Rectangle bounds;

    public NanoVgPathGraphics(List<Path.PathData> pathData, float strokeSize, Color strokeColor, Color fillColor, boolean useHalfStrokeDraw) {
        super(strokeSize, strokeColor, fillColor, useHalfStrokeDraw);

        this.path = Path.obtain(pathData);
    }

    public NanoVgPathGraphics(Vector2 center, ArrayList<Vector2> inputPoints, float strokeSize, float size, Color strokeColor, Color fillColor, boolean useHalfStrokeDraw) {
        super(strokeSize, strokeColor, fillColor, useHalfStrokeDraw);
        Vector2 originOffset = new Vector2((center.x - originallOrigin.x), (center.y - originallOrigin.y));

        ArrayList<Vector2> finallist = new ArrayList<Vector2>();

        for(Vector2 point: inputPoints) {
            Vector2 recenteredPoint = new Vector2(originOffset.x+ point.x  , originOffset.y + point.y );
            Vector2 distance = new Vector2(recenteredPoint.x - center.x, recenteredPoint.y - center.y);
            Vector2 finalpoint = new Vector2(recenteredPoint.x - distance.x + (distance.x * (size/originalSize)), recenteredPoint.y - distance.y + (distance.y * (size/originalSize)) );
            finallist.add(finalpoint);
        }
        Path newpath = new Path();
        Path poligonpath = newpath.polygon(finallist.toArray(new Vector2[finallist.size()]));
        ArrayList<Path.PathData> pathData = poligonpath.toPathData();
        this.path = Path.obtain(pathData);
    }

    @Override
    public final void draw(Canvas canvas) {
        configureCanvas(canvas);

        canvas.drawPath(path);
    }

    @Override
    public final GraphicsRendererType getRendererType() {
        return GraphicsRendererType.NanoVg;
    }

    @Override
    public final Rectangle getBounds() {
        if (bounds == null) {
            bounds = path.getDodleBounds();
        }
        
        return bounds;
    }
}
