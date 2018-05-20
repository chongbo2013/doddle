package com.dodles.gdx.dodleengine.geometry;

import java.util.ArrayList;
import java.util.List;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.GraphicsGenerator;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg.NanoVgPolygonGraphics;

/**
 * Core functionality for polygon geometry.
 */
public abstract class AbstractPolygonGeometry extends AbstractGeometry {
    @Override
    public final void init(Shape initShape) {
        GeometryRenderState grs = new GeometryRenderState();
        initShape.setRenderState(grs);

        initShape.addGenerator(new GraphicsGenerator() {
            @Override
            public List<Graphics> generateGraphics(Shape shape) {
                AbstractPolygonGeometryConfig cfg = (AbstractPolygonGeometryConfig) shape.getCustomConfig();
                int numPoints = cfg.getNumPoints();

                GeometryRenderState grs = new GeometryRenderState();
                generateHandleHooks(shape, grs.getHandleHooks());
                shape.setRenderState(grs);

                List<HandleHook> handleHooks = grs.getHandleHooks();
                
                if (numPoints != handleHooks.size()) {
                    generateHandleHooks(shape, handleHooks);
                }

                Vector2[] corners = cfg.getCorners();
                for (int i = 0; i < numPoints; i++) {
                    corners[i] = handleHooks.get(i).getPosition();
                }
                float size = shape.getStrokeConfig().getSize();
                Color stroke = shape.getStrokeConfig().getColor();
                Color fill = shape.getStrokeConfig().getFill();

                ArrayList<Graphics> result = new ArrayList<Graphics>();
                result.add(new NanoVgPolygonGraphics(corners, cfg.getCornerRadius(), 2f, stroke, fill, USEHALFSTROKEDRAW));
                return result;
            }
        });
    }

    /**
     * Generates the handlehooks for the geometry.
     */
    public abstract void generateHandleHooks(Shape shape, List<HandleHook> handleHooks);
}
