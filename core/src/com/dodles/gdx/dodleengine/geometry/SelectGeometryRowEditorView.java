package com.dodles.gdx.dodleengine.geometry;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.geometry.GeometryTool;

import javax.inject.Inject;

/**
 * Row for selecting which geometry to draw.
 */
@PerDodleEngine
public class SelectGeometryRowEditorView extends AbstractEditorView {
    private final GeometryRegistry registry;
    private final ToolRegistry toolRegistry;

    private Table rootTable;

    @Inject
    public SelectGeometryRowEditorView(GeometryRegistry registry, ToolRegistry toolRegistry) {
        this.registry = registry;
        this.toolRegistry = toolRegistry;
    }

    @Override
    public final void activate(Skin skin, String newState) {
        if (rootTable == null) {
            rootTable = new Table();
            rootTable.setFillParent(true);

            for (Geometry geometry : registry.getGeometries()) {
                configureGeometry(geometry, skin);
            }

            this.addActor(rootTable);
        }
    }

    private void configureGeometry(final Geometry geometry, Skin skin) {
        TextButton button = new TextButton(geometry.getName(), skin, "medium");
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                registry.setActiveGeometry(geometry.getName());
                ((GeometryTool) toolRegistry.getActiveTool()).addShapeToCanvas(geometry, geometry.getDefaultGeometryConfig());
            }
        });
        rootTable.add(button).expandX();
            
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }

}
