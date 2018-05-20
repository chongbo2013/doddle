package com.dodles.gdx.dodleengine.geometry.custom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.geometry.AbstractGeometry;
import com.dodles.gdx.dodleengine.geometry.GeometryConfig;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.geometry.GeometryRenderState;
import com.dodles.gdx.dodleengine.scenegraph.GraphicsGenerator;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg.NanoVgPathGraphics;
import com.gurella.engine.graphics.vector.Path;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@PerDodleEngine
public class CustomGeometry extends AbstractGeometry {
    public static final String GEOMETRY_NAME = "CustomShape";
    public static final String GEOMETRY_ICON_NAME = "Freeform";
    private GeometryConfig defaultGeometryConfig;
    private ArrayList<Vector2> points;
    
    @Inject
    public CustomGeometry(GeometryRegistry registry) {
        registry.registerGeometry(this);
    }

    @Override
    public final String getName() {
        return GEOMETRY_NAME;
    }

    @Override
    public final String getIconName() {
        return GEOMETRY_ICON_NAME;
    }

    @Override
    public final int getOrder() {
        return 1;
    }

    @Override
    public final GeometryConfig getDefaultGeometryConfig() {
        if (defaultGeometryConfig == null) {
            defaultGeometryConfig = new CustomGeometryConfig(points);
        }

        return defaultGeometryConfig.cpy();
    }

    @Override
    public final void init(Shape initShape) {
        initShape.setRenderState(setHandleHooks(initShape));

        initShape.addGenerator(new GraphicsGenerator() {
            @Override
            public List<Graphics> generateGraphics(Shape shape) {
                CustomGeometryConfig cfg = (CustomGeometryConfig) shape.getCustomConfig();
                ArrayList<Vector2> inputpoints = cfg.getPoints();

                float size = cfg.getSize();
                Color stroke = shape.getStrokeConfig().getColor();
                Color fill = shape.getStrokeConfig().getColor();

                Path newpath = new Path();
                Path poligonpath = newpath.polygon(inputpoints.toArray(new Vector2[inputpoints.size()]));
                ArrayList<Path.PathData> pathData = poligonpath.toPathData();

                ArrayList<Graphics> result = new ArrayList<Graphics>();
                result.add(new NanoVgPathGraphics(cfg.getPoint(),inputpoints, 0f, size, stroke, fill, false));
                return result;
            }
        });
    }

    /**
     * Generate the handle hooks and return a renderState.
     */
    private GeometryRenderState setHandleHooks(Shape shape) {
        GeometryRenderState grs = new GeometryRenderState();
        shape.setRenderState(grs);
        return grs;
    }

    public final void setPointsArray(ArrayList<Vector2> newpoints) {
        points = newpoints;
    }
}
