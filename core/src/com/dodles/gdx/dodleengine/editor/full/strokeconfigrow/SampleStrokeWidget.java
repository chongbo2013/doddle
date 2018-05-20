package com.dodles.gdx.dodleengine.editor.full.strokeconfigrow;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.dodles.gdx.dodleengine.brushes.Brush;
import com.dodles.gdx.dodleengine.brushes.BrushRegistry;
import com.dodles.gdx.dodleengine.brushes.RulerMode;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.draw.DrawTool;
import de.hypergraphs.hyena.core.shared.data.UUID;

/**
 * Displays sample brush stroke.
 */
public class SampleStrokeWidget extends Table {


    private final BrushRegistry brushRegistry;
    private final FrameBufferAtlasManager atlasManager;
    private final ToolRegistry toolRegistry;
    private float width = 200;
    private float height = 50;
    private Shape shape;
    private boolean runOnce = false;

    public SampleStrokeWidget(FrameBufferAtlasManager atlasManager, BrushRegistry brushRegistry, EngineEventManager eventManager, ToolRegistry toolRegistry) {
        String id = UUID.uuid();
        shape = new Shape(id, "N/A", atlasManager);
        this.brushRegistry = brushRegistry;
        this.atlasManager = atlasManager;
        this.toolRegistry = toolRegistry;


        eventManager.addListener(new EngineEventListener(EngineEventType.STROKE_CONFIG_CHANGED) {
            @Override
            public void listen(EngineEventData data) {
                setupShape();
            }
        });
        setupShape();
    }

    /**
     * Create sample brush stroke.
     */
    private void setupShape() {

        Brush brush = brushRegistry.getActiveBrush();
        if (brush != null) {
            float scale = .4f;
            shape.clearGenerators();
            shape.setStrokeConfig(brush.getBrushStrokeConfig());
            brush.setRulerMode(RulerMode.NONE);
            brush.beginStroke(shape);
            float w = getWidth() * (1 / scale);
            float h = getHeight() * (1 / scale);
            brush.mouseMove(new Vector2(0, (h * 3) / 4));
            brush.mouseMove(new Vector2(w / 3, h / 4));
            brush.mouseMove(new Vector2((w * 2) / 3, (h * 3) / 4));
            brush.mouseMove(new Vector2(w, h / 4));
            shape.setScale(scale);
        }
    }

    @Override
    public final float getMinWidth() {
        return width;
    }


    @Override
    public final float getMinHeight() {
        return height;
    }

    @Override
    public final float getMaxWidth() {
        return width;
    }


    @Override
    public final float getMaxHeight() {
        return height;
    }

    @Override
    public final float getPrefWidth() {
        return width;
    }


    @Override
    public final float getPrefHeight() {
        return height;
    }


    @Override
    public final void draw(Batch batch, float parentAlpha) {
        validate();
        if (runOnce && toolRegistry.getActiveTool().getName() == DrawTool.TOOL_NAME) {
            shape.setPosition(getX(), getY());
            shape.draw(batch, parentAlpha);
        } else {
            setupShape();
            runOnce = true;
        }

    }

}
