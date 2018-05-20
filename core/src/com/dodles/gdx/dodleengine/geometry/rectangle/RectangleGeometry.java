package com.dodles.gdx.dodleengine.geometry.rectangle;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.geometry.AbstractPolygonGeometry;
import com.dodles.gdx.dodleengine.geometry.GeometryConfig;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.geometry.HandleHook;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.tools.geometry.GeometryTool;

import javax.inject.Inject;
import java.util.List;

/**
 * Draws rectangular geometric shapes.
 */
@PerDodleEngine
public class RectangleGeometry extends AbstractPolygonGeometry {
    public static final String GEOMETRY_NAME = "rectangle";
    public static final String GEOMETRY_ICON_NAME = "Square";

    private GeometryConfig defaultGeometryConfig;
    private boolean handleHooksInitialized = false;
    @Inject
    public RectangleGeometry(GeometryRegistry registry, FullEditorViewState fullViewState, RectangleConfigurationRowEditorView rcrev) {
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
            defaultGeometryConfig = new RectangleGeometryConfig();
        }

        return defaultGeometryConfig.cpy();
    }

    @Override
    public final void generateHandleHooks(Shape shape, List<HandleHook> handleHooks) {
        handleHooks.clear();

        final int initNumPoints = ((RectangleGeometryConfig) shape.getCustomConfig()).getNumPoints();
        for (int i = 0; i < initNumPoints; i++) {
            RectangleHandleHook handlehook = new RectangleHandleHook(shape, i);
            if(!handleHooksInitialized) {
                handlehook.setPosition(((RectangleGeometryConfig) shape.getCustomConfig()).getDefaultCorners().get(i));
            }
            handleHooks.add(handlehook);
        }
        handleHooksInitialized = true;
    }

    public void intializeRectangleCorners() {
        handleHooksInitialized = false;
    }
}