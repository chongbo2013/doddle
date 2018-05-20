package com.dodles.gdx.dodleengine.geometry.triangle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.geometry.AbstractGeometry;
import com.dodles.gdx.dodleengine.geometry.AbstractPolygonGeometryConfig;
import com.dodles.gdx.dodleengine.geometry.GeometryConfig;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.geometry.GeometryRenderState;
import com.dodles.gdx.dodleengine.geometry.polygon.PolygonHandleHook;
import com.dodles.gdx.dodleengine.scenegraph.GraphicsGenerator;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg.NanoVgPolygonGraphics;
import com.dodles.gdx.dodleengine.tools.geometry.GeometryTool;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws triangular geometric shapes.
 */
@PerDodleEngine
public class TriangleGeometry extends AbstractGeometry {
    public static final String GEOMETRY_NAME = "triangle";
    public static final String GEOMETRY_ICON_NAME = "Triangle";
    
    private GeometryConfig defaultGeometryConfig;
    
    @Inject
    public TriangleGeometry(GeometryRegistry registry, FullEditorViewState fullViewState, TriangleConfigurationRowEditorView rcrev) {
        registry.registerGeometry(this);
        
        String state = GeometryTool.TOOL_NAME + "." + GEOMETRY_NAME;
        fullViewState.registerRow2View(state, null);
        fullViewState.registerRow3View(state, rcrev);
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
            defaultGeometryConfig = new TriangleGeometryConfig();
        }
        
        return defaultGeometryConfig.cpy();
    }

    @Override
    public final void init(Shape initShape) {
        initShape.setRenderState(setHandleHooks(initShape));

        initShape.addGenerator(new GraphicsGenerator() {
            @Override
            public List<Graphics> generateGraphics(Shape shape) {
                AbstractPolygonGeometryConfig cfg = (AbstractPolygonGeometryConfig) shape.getCustomConfig();
                int numPoints = cfg.getNumPoints();

                GeometryRenderState grs = setHandleHooks(shape);

                Vector2[] corners = cfg.getCorners();
                for (int i = 0; i < numPoints; i++) {
                    corners[i] = grs.getHandleHooks().get(i).getPosition();
                }

                Color stroke = shape.getStrokeConfig().getColor();
                Color fill = shape.getStrokeConfig().getFill();

                ArrayList<Graphics> result = new ArrayList<Graphics>();
                result.add(new NanoVgPolygonGraphics(corners, cfg.getCornerRadius(), 0, stroke, fill, USEHALFSTROKEDRAW));
                return result;
            }
        });
    }

    /**
     * Generate the handle hooks and return a renderState.
     */
    private GeometryRenderState setHandleHooks(Shape shape) {
        GeometryRenderState grs = new GeometryRenderState();

        final int initNumPoints = ((AbstractPolygonGeometryConfig) shape.getCustomConfig()).getNumPoints();
        for (int i = 0; i < initNumPoints; i++) {
            grs.getHandleHooks().add(new PolygonHandleHook(shape, i));
        }
        shape.setRenderState(grs);
        return grs;
    }
}
