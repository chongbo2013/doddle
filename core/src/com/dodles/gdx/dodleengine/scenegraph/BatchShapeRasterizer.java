package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.dodles.gdx.dodleengine.DodleEngineConfig;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.graphics.AtlasGraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.AtlasOffset;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasSlice;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasSlice.FrameBufferConsumer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferStack;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRenderer;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

/**
 * Rasterizes shapes in a batch process, which is speedier than doing it inline in the normal rendering loop.
 */
@PerDodleEngine
public class BatchShapeRasterizer {
    private final DodleEngineConfig engineConfig;
    private final FrameBufferAtlasManager atlasManager;
    
    private ShapeRenderer shapeRenderer;
    private EngineEventManager eventManager;
    private FrameBuffer intermediateFb;
    private DodlesSpriteBatch intermediateBatch;
    private boolean overloaded = false;
    
    @Inject
    public BatchShapeRasterizer(
            DodleEngineConfig engineConfig,
            EngineEventManager eventManager,
            FrameBufferAtlasManager atlasManager
    ) {
        this.engineConfig = engineConfig;
        this.eventManager = eventManager;
        this.atlasManager = atlasManager;
    }
    
    /**
     * Rasterizes the given shapes.
     */
    public final void rasterize(List<Shape> shapes) {
        long startTime = System.currentTimeMillis();
        
        for (final Shape shape : shapes) {
            if (!engineConfig.hasOption(DodleEngineConfig.Options.RENDER_ANIMATION) && System.currentTimeMillis() - startTime > 1000f / 15f) {
                if (!overloaded) {
                    overloaded = true;
                    eventManager.fireEvent(EngineEventType.RASTERIZER_OVERLOADED, "yup");
                }
                
                return;
            }
            
            Color tileColor = shape.getStrokeConfig().getColor();

            if (shape.getStrokeConfig().getSize() <= 0) {
                tileColor = shape.getStrokeConfig().getFill();
            }
            
            for (GraphicsRenderer renderer : shape.getRenderers()) {
                if (!(renderer instanceof AtlasGraphicsRenderer)) {
                    throw new GdxRuntimeException("Only AtlasGraphicRenderers accepted!");
                }

                final AtlasGraphicsRenderer atlasRenderer = (AtlasGraphicsRenderer) renderer;
                Collection<AtlasOffset> newOffsets = atlasRenderer.getNewOffsetsToDrawTo();

                if (!newOffsets.isEmpty()) {
                    // TODO: make this work with objects larger than the framebuffer size
                    if (intermediateFb == null) {
                        intermediateFb = new FrameBuffer(Format.RGBA8888, FrameBufferAtlasManager.ATLAS_SIZE, FrameBufferAtlasManager.ATLAS_SIZE, false, true);
                        intermediateBatch = new DodlesSpriteBatch();
                        intermediateBatch.enableBlending();
            
                        // We need to use seperate blending functions here when drawing to the framebuffer.
                        // Just using normal src alpha/ 1 - src alpha causes overwrites instead of blending
                        // when drawing to the framebuffer.  I still don't quite understand why. :/
                        intermediateBatch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
                    }

                    intermediateBatch.getProjectionMatrix().setToOrtho2D(0, 0, FrameBufferAtlasManager.ATLAS_SIZE, FrameBufferAtlasManager.ATLAS_SIZE);
                    
                    FrameBufferStack.instance().begin(intermediateFb);
                    Gdx.gl.glClearColor(tileColor.r, tileColor.g, tileColor.b, 0); // Should this be colored?
                    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
                    
                    intermediateBatch.begin();
                    
                    final Vector2 drawOffset = new Vector2();
                    boolean init = true;
                    
                    for (AtlasOffset offset : newOffsets) {
                        if (init || offset.getXOffset() < drawOffset.x) {
                            drawOffset.x = offset.getXOffset();
                        }
                        
                        if (init || offset.getYOffset() < drawOffset.y) {
                            drawOffset.y = offset.getYOffset();
                        }
                        
                        init = false;
                    }
                    
                    drawOffset.x *= -1;
                    drawOffset.y *= -1;
                    
                    atlasRenderer.drawNew(intermediateBatch, 1, drawOffset);
                    
                    if (!shape.getEraserShapes().isEmpty()) {                                
                        intermediateBatch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ZERO, GL20.GL_ZERO);

                        for (EraserShape eraser : shape.getEraserShapes()) {
                            Shape es = eraser.getShape();
                            es.getStrokeConfig().setOpacity(0);
                            Vector2 eraserDrawOffset = drawOffset.cpy();
                            eraserDrawOffset.x += eraser.getOffset().x;
                            eraserDrawOffset.y += eraser.getOffset().y;
                            Matrix4 originalTransform = null;

                            // We want to avoid setting the transform matrix if at all possible,
                            // if there's no rotation/scaling we can just offset the draw...
                            if (eraser.getRotation() != 0 || eraser.getScale() != 1) {
                                es.setX(eraserDrawOffset.x);
                                es.setY(eraserDrawOffset.y);
                                es.setRotation(eraser.getRotation());
                                es.setScale(eraser.getScale());
                                originalTransform = ActorMixins.setBatchTransformMatrix(intermediateBatch, es);
                                eraserDrawOffset.x = 0;
                                eraserDrawOffset.y = 0;
                            }

                            for (GraphicsRenderer eraserRenderer : eraser.getShape().getRenderers()) {
                                if (!(eraserRenderer instanceof AtlasGraphicsRenderer)) {
                                    throw new GdxRuntimeException("Only AtlasGraphicRenderers accepted!");
                                }

                                final AtlasGraphicsRenderer atlasEraserRenderer = (AtlasGraphicsRenderer) eraserRenderer;
                                atlasEraserRenderer.draw(intermediateBatch, 1, eraserDrawOffset);
                            }

                            if (originalTransform != null) {
                                intermediateBatch.setTransformMatrix(originalTransform);
                            }
                        }

                        intermediateBatch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
                    }
                    
                    intermediateBatch.end();
                    
                    FrameBufferStack.instance().end();

                    for (final AtlasOffset offset : newOffsets) {
                        final FrameBufferAtlasSlice slice = atlasManager.getOrCreateAtlasSlice(shape.getName(), offset, tileColor);
                        slice.drawToSlice(new FrameBufferConsumer() {
                            @Override
                            public void draw(FrameBuffer buffer) {
                                DodlesSpriteBatch fbBatch = atlasManager.getSharedSpriteBatch();
                                Vector2 atlasDrawOffset = new Vector2(offset.getXOffset() + drawOffset.x, offset.getYOffset() + drawOffset.y);
                                
                                fbBatch.begin();
                                fbBatch.draw(intermediateFb.getColorBufferTexture(), slice.getAtlasOffsetPoint().x, slice.getAtlasOffsetPoint().y, (float) FrameBufferAtlasManager.PAGE_SIZE, (float) FrameBufferAtlasManager.PAGE_SIZE, (int) atlasDrawOffset.x - FrameBufferAtlasManager.BORDER, (int) atlasDrawOffset.y - FrameBufferAtlasManager.BORDER, FrameBufferAtlasManager.PAGE_SIZE, FrameBufferAtlasManager.PAGE_SIZE, false, true);
                                fbBatch.end();
                            }
                        });
                        
                        shape.addOffsetRenderedTo(offset);
                    }
                    
                    atlasRenderer.commit();
                }
            }
        }
        
        if (overloaded) {
            eventManager.fireEvent(EngineEventType.RASTERIZER_OVERLOADED);
            overloaded = false;
        }
    }
    
    /**
     * Returns true if the rasterizer is currently overloaded.
     */
    public final boolean isOverloaded() {
        return overloaded;
    }
}
