package com.dodles.gdx.dodleengine.geometry.circle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.geometry.AbstractGeometry;
import com.dodles.gdx.dodleengine.geometry.GeometryConfig;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.geometry.GeometryRenderState;
import com.dodles.gdx.dodleengine.scenegraph.GraphicsGenerator;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg.NanoVgCircleGraphics;
import com.dodles.gdx.dodleengine.tools.geometry.GeometryTool;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws circle geometric shapes made from bezier curves.
 */
@PerDodleEngine
public class CircleGeometry extends AbstractGeometry {
    public static final String GEOMETRY_NAME = "circle";
    public static final String GEOMETRY_ICON_NAME = "Circle";
    
    private GeometryConfig defaultGeometryConfig;
    
    @Inject
    public CircleGeometry(GeometryRegistry registry, FullEditorViewState fullViewState, CircleConfigurationRowEditorView rcrev) {
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
            defaultGeometryConfig = new CircleGeometryConfig();
        }
        
        return defaultGeometryConfig.cpy();
    }

    @Override
    public final void init(Shape initShape) {
        initShape.setRenderState(setHandleHooks(initShape));

        initShape.addGenerator(new GraphicsGenerator() {
            @Override
            public List<Graphics> generateGraphics(Shape shape) {
                CircleGeometryConfig cfg = (CircleGeometryConfig) shape.getCustomConfig();

                GeometryRenderState grs = setHandleHooks(shape);

                List<Vector2> points = new ArrayList<Vector2>();
                points.add(grs.getHandleHooks().get(0).getPosition().cpy());
                points.add(grs.getHandleHooks().get(1).getPosition().cpy());
                points.add(grs.getHandleHooks().get(2).getPosition().cpy());
                points.add(grs.getHandleHooks().get(3).getPosition().cpy());

                Color stroke = shape.getStrokeConfig().getColor();
                Color fill = shape.getStrokeConfig().getFill();

                ArrayList<Graphics> result = new ArrayList<Graphics>();
                result.add(new NanoVgCircleGraphics(cfg.getPoint(), points, 0, stroke, fill, USEHALFSTROKEDRAW));
                return result;
            }
        });
    }

    /**
     * Generate the handle hooks and return a renderState.
     */
    private GeometryRenderState setHandleHooks(Shape shape) {
        GeometryRenderState grs = new GeometryRenderState();
        grs.getHandleHooks().add(new CircleHandleHook(shape, 1, 0, CircleGeometryConfig.Axis.XPOS));
        grs.getHandleHooks().add(new CircleHandleHook(shape, 0, 1, CircleGeometryConfig.Axis.YPOS));
        grs.getHandleHooks().add(new CircleHandleHook(shape, -1, 0, CircleGeometryConfig.Axis.XNEG));
        grs.getHandleHooks().add(new CircleHandleHook(shape, 0, -1, CircleGeometryConfig.Axis.YNEG));
        shape.setRenderState(grs);
        return grs;
    }
}
