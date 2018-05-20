package com.dodles.gdx.dodleengine.geometry.heart;

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
import com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg.NanoVgHeartGraphics;
import com.dodles.gdx.dodleengine.tools.geometry.GeometryTool;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws heart geometric shapes.
 */
@PerDodleEngine
public class HeartGeometry extends AbstractGeometry {
    public static final String GEOMETRY_NAME = "heart";
    public static final String GEOMETRY_ICON_NAME = "Heart";
    
    private GeometryConfig defaultGeometryConfig;
    
    @Inject
    public HeartGeometry(GeometryRegistry registry, FullEditorViewState fullViewState, HeartConfigurationRowEditorView rcrev) {
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
            defaultGeometryConfig = new HeartGeometryConfig();
            ((HeartGeometryConfig) defaultGeometryConfig).setAxisRatio(HeartGeometryConfig.Axis.X, 1.25f);
        }
        
        return defaultGeometryConfig.cpy();
    }

    @Override
    public final void init(final Shape initShape) {
        initShape.setRenderState(setHandleHooks(initShape));

        initShape.addGenerator(new GraphicsGenerator() {
            @Override
            public List<Graphics> generateGraphics(Shape shape) {
                HeartGeometryConfig cfg = (HeartGeometryConfig) shape.getCustomConfig();

                GeometryRenderState grs = setHandleHooks(shape);
                
                Vector2 xSize = new Vector2(cfg.getAxisRatio(HeartGeometryConfig.Axis.X) * cfg.getSize(), 0).rotate(cfg.getRotation());
                Vector2 ySize = new Vector2(0, cfg.getAxisRatio(HeartGeometryConfig.Axis.Y) * cfg.getSize()).rotate(cfg.getRotation());
                Vector2 startPoint = grs.getHandleHooks().get(1).getPosition().cpy();
                Vector2 leftPoint = grs.getHandleHooks().get(2).getPosition().cpy().add(ySize.cpy().scl(-0.2f)).add(xSize.cpy().scl(-1f));
                Vector2 leftArcPoint = grs.getHandleHooks().get(3).getPosition().cpy().add(xSize.cpy().scl(-0.85f).add(ySize.cpy().scl(-1f)));
                Vector2 rightArcPoint = grs.getHandleHooks().get(3).getPosition().cpy().add(xSize.cpy().scl(0.85f).add(ySize.cpy().scl(-1f)));
                Vector2 rightPoint = grs.getHandleHooks().get(0).getPosition().cpy().add(ySize.cpy().scl(-0.2f)).add(xSize.cpy());
                Vector2 endPoint = cfg.getPoint().cpy().add(ySize.cpy().scl(-0.9f));

                Color stroke = shape.getStrokeConfig().getColor();
                Color fill = shape.getStrokeConfig().getFill();

                ArrayList<Graphics> result = new ArrayList<Graphics>();
                result.add(new NanoVgHeartGraphics(startPoint, leftPoint, leftArcPoint, rightPoint, rightArcPoint, endPoint, 0, stroke, fill, USEHALFSTROKEDRAW));
                return result;
            }
        });
    }

    /**
     * Generate the handle hooks and return a renderState.
     */
    private GeometryRenderState setHandleHooks(Shape shape) {
        GeometryRenderState grs = new GeometryRenderState();
        grs.getHandleHooks().add(new HeartHandleHook(shape, 1, 0, HeartGeometryConfig.Axis.X));
        grs.getHandleHooks().add(new HeartHandleHook(shape, 0, 1, HeartGeometryConfig.Axis.Y));
        grs.getHandleHooks().add(new HeartHandleHook(shape, -1, 0, HeartGeometryConfig.Axis.X));
        grs.getHandleHooks().add(new HeartHandleHook(shape, 0, -1, HeartGeometryConfig.Axis.Y));
        shape.setRenderState(grs);
        return grs;
    }
}
