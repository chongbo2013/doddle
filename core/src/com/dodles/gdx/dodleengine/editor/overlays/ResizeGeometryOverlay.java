package com.dodles.gdx.dodleengine.editor.overlays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.geometry.GeometryRenderState;
import com.dodles.gdx.dodleengine.geometry.HandleHook;
import com.dodles.gdx.dodleengine.scenegraph.BaseGroup;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.GraphicsGenerator;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.shaperenderer.ShapeRendererCircleGraphics;
import de.hypergraphs.hyena.core.shared.data.UUID;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * An overlay that displays the geometry handle hooks.
 */
@PerDodleEngine
public class ResizeGeometryOverlay extends BaseGroup implements Overlay {
    private final DodleStageManager stageManager;
    
    private List<HandleHook> handleHooks;
    private boolean addedToScaledGroup = false;
    private Shape geometryShape;
    
    @Inject
    public ResizeGeometryOverlay(DodleStageManager stageManager) {
        this.stageManager = stageManager;
        stageManager.registerOverlay(this);
        this.setVisible(false);
    }
    
    /**
     * Sets the shape this overlay is rendering.
     */
    public final void setShape(Shape shape) {
        this.clear();
        geometryShape = shape;
        
        if (shape != null) {
            if (!addedToScaledGroup) {
                stageManager.getDrawGroup().addActor(this);
                addedToScaledGroup = true;
            }

            handleHooks = ((GeometryRenderState) shape.getRenderState()).getHandleHooks();

            for (HandleHook hook : handleHooks) {
                this.addActor(generateHandleShape(hook));
            }
        }
        
        this.setVisible(shape != null);
    }

    @Override
    public final void update(DodleStageManager.DisplayMode displayMode) {
        if (geometryShape != null) {
            if (((GeometryRenderState) geometryShape.getRenderState()).getHandleHooks().size() != this.getChildren().size) {
                // Recreate handle hook shapes if the number of handle hooks changes...
                setShape(geometryShape);
            }
            
            for (Actor actor : this.getChildren()) {
                ((Shape) actor).regenerate();
            }
        }
    }
    
    @Override
    public final void reset() {
        this.setShape(null);
    }
    
    private Shape generateHandleShape(final HandleHook handle) {
        Shape handleShape = new Shape("handlehook-" + UUID.uuid(), "N/A");
        
        handleShape.addGenerator(new GraphicsGenerator() {
            @Override
            public List<Graphics> generateGraphics(Shape shape) {
                ArrayList<Graphics> result = new ArrayList<Graphics>();
                Vector2 position = handle.getPosition();
                Vector2 dodlePosition = CommonActorOperations.localToDodleCoordinates(geometryShape, position);
                Color black = Color.BLACK.cpy();
                black.a = 0.5f;
                result.add(new ShapeRendererCircleGraphics(dodlePosition.x, dodlePosition.y, 10, black));
                return result;
            }
        });
        return handleShape;
    }
}
