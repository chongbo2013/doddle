package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.scenegraph.graphics.AtlasOffset;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasSlice;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRendererType;
import com.dodles.gdx.dodleengine.scenegraph.graphics.direct.DirectGraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg.NanoVgAtlasGraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg.NanoVgDirectGraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.shaperenderer.ShapeRendererGraphicsRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A Shape is an actor that can render Graphics commands to draw arbitrary shapes
 * on the stage.
 */
public class Shape extends Actor implements DodlesActor, Disposable, Updatable {
    public static final String ACTOR_TYPE = "Shape";
    
    private final ArrayList<AtlasOffset> offsetsRenderedTo = new ArrayList<AtlasOffset>();
    private final ArrayList<EraserShape> eraserShapes = new ArrayList<EraserShape>();
    private final FrameBufferAtlasManager atlasManager;
    private final ArrayList<GraphicsGenerator> generators = new ArrayList<GraphicsGenerator>();
    private final HashMap<GraphicsRendererType, GraphicsRenderer> renderers = new HashMap<GraphicsRendererType, GraphicsRenderer>();
    
    private GraphicsRenderer[] cachedRendererList;
    private StrokeConfig strokeConfig;
    private CustomToolConfig customConfig;
    private Transform baseTransform = new Transform();
    private RenderState renderState = null;
    private int generatedIndex = 0;
    private Rectangle drawBounds = null;
    private RenderMode mode = RenderMode.DIRECT;
    private String trackingID;
    private String originalID;

    public Shape(String id, String trackingID) {
        this(id, trackingID, new StrokeConfig());
    }

    public Shape(String id, String trackingID, StrokeConfig strokeConfig) {
        this(id, trackingID, null, strokeConfig);
    }

    public Shape(String id, String trackingID, FrameBufferAtlasManager atlasManager) {
        this(id, trackingID, atlasManager, new StrokeConfig());
    }

    public Shape(String id, String trackingID, FrameBufferAtlasManager atlasManager, StrokeConfig strokeConfig) {
        setName(id);
        setTrackingID(trackingID);

        if (atlasManager != null) {
            mode = RenderMode.ATLAS;
        }

        this.strokeConfig = strokeConfig;
        this.atlasManager = atlasManager;
    }

    public Shape(JsonValue json, IdDatabase idDB, DodlesActorFactory actorFactory, FrameBufferAtlasManager atlasManager) {
        ActorMixins.importFromJson(this, idDB, json);
        customConfig = CustomToolConfigFactory.create(json.get("customToolConfig"));
        strokeConfig = new StrokeConfig(json.get("strokeConfig"));
        setRenderMode(RenderMode.valueOf(json.getString("renderMode")));
        
        if (json.has("eraserShapes")) {
            for (JsonValue eraserShape : json.get("eraserShapes").iterator()) {
                eraserShapes.add(new EraserShape(eraserShape, idDB, actorFactory, atlasManager));
            }
        }
        
        this.atlasManager = atlasManager;
    }
    
    // CHECKSTYLE.OFF: DesignForExtension - subclasses need to override
    @Override
    public String getType() {
        return ACTOR_TYPE;
    }
    // CHECKSTYLE.ON: DesignForExtension
    
    @Override
    public final String getTrackingID() {
        return trackingID;
    }
    
    @Override
    public final void setTrackingID(String pTrackingID) {
        trackingID = pTrackingID;
    }
    
    @Override
    public final String getOriginalID() {
        return originalID;
    }
    
    @Override
    public final void setOriginalID(String pOriginalID) {
        originalID = pOriginalID;
    }

    /**
     * Adds a graphics generator to the shape.
     */
    public final void addGenerator(GraphicsGenerator g) {
        generators.add(g);
    }

    /**
     * Removes all generators from the shape.
     */
    public final void clearGenerators() {
        generators.clear();
        regenerate();
    }

    /**
     * Regenerates the graphics for the shape.
     */
    public final void regenerate() {
        generatedIndex = 0;
        drawBounds = null;
        dispose();

        if (renderState != null) {
            renderState.onRegenerate();
        }

        updateOrigin();
    }

    /**
     * Returns the render state for the shape.
     */
    public final RenderState getRenderState() {
        return renderState;
    }

    /**
     * Sets the render state for the shape.
     */
    public final void setRenderState(RenderState newRenderState) {
        renderState = newRenderState;
    }

    /**
     * Returns the stroke config.
     */
    public final StrokeConfig getStrokeConfig() {
        return strokeConfig;
    }

    /**
     * Sets the stroke config.
     */
    public final void setStrokeConfig(StrokeConfig strokeConfig) {
        this.strokeConfig = strokeConfig;
    }

    /**
     * Returns the shape configuration.
     */
    public final CustomToolConfig getCustomConfig() {
        return customConfig;
    }

    /**
     * Sets the shape configuration.
     */
    public final void setCustomConfig(CustomToolConfig newCustomConfig) {
        customConfig = newCustomConfig;
    }

    @Override
    public final Rectangle getDrawBounds() {
        if (drawBounds == null) {
            // Calculating bounds for shapes can be expensive, make sure we cache results!
            for (GraphicsRenderer renderer : getRenderers()) {
                if (drawBounds == null) {
                    drawBounds = renderer.getDrawBounds();
                } else {
                    drawBounds = drawBounds.merge(renderer.getDrawBounds());
                }
            }
        }

        return drawBounds;
    }

    @Override
    public final void updateOrigin() {
        if (customConfig != null && customConfig instanceof UpdateOriginConfig) {
            ((UpdateOriginConfig) customConfig).updateOrigin(this);
        } else {
            ActorMixins.updateOrigin(this);
        }
    }

    @Override
    public final Vector2 getOrigin() {
        return new Vector2(getOriginX(), getOriginY());
    }

    @Override
    public final void updateBaseTransform(Transform transform) {
        baseTransform = transform;
        ActorMixins.updateTransformProperties(this, transform);
    }

    @Override
    public final void resetToBaseTransform() {
        updateBaseTransform(baseTransform);
    }

    @Override
    public final Transform getBaseTransform() {
        return baseTransform;
    }

    /**
     * Get the generators for cloning purposes.
     */
    public final ArrayList<GraphicsGenerator> getGenerators() {
        return generators;
    }

    @Override
    public final void dispose() {
        for (GraphicsRenderer renderer : renderers.values()) {
            renderer.dispose();
        }

        renderers.clear();
        cachedRendererList = null;

        if (atlasManager != null) {
            for (AtlasOffset offset : offsetsRenderedTo) {
                atlasManager.freeSlice(getName(), offset);
            }
        }

        offsetsRenderedTo.clear();
    }

    /**
     * Draws all of the graphics commands to the scene relative to the current
     * position of the Shape.
     */
    @Override
    public final void draw(Batch batch, float parentAlpha) {
        Matrix4 originalTransform = null;
        Vector2 drawOffset = null;

        // We want to avoid setting the transform matrix if at all possible,
        // if there's no rotation/scaling we can just offset the draw...
        if (getRotation() == 0 && getScaleX() == 1 && getScaleY() == 1) {
            drawOffset = new Vector2(getX(), getY());
        } else {
            originalTransform = ActorMixins.setBatchTransformMatrix(batch, this);
            drawOffset = new Vector2();
        }

        switch (mode) {
            case DIRECT:
                drawDirect((DodlesSpriteBatch) batch, parentAlpha, drawOffset);
                break;

            case ATLAS:
                drawToAtlas((DodlesSpriteBatch) batch, parentAlpha, drawOffset);
                break;

            default:
                throw new UnsupportedOperationException("Unrecognized render mode: " + mode.name());
        }

        if (originalTransform != null) {
            batch.setTransformMatrix(originalTransform);
        }
    }

    /**
     * Returns the render mode for the shape.
     */
    public final RenderMode getRenderMode() {
        return mode;
    }

    /**
     * Sets the render mode for the shape.
     */
    public final void setRenderMode(RenderMode newMode) {
        mode = newMode;
        regenerate();
    }

    private GraphicsRenderer createRenderer(GraphicsRendererType renderType) {
        switch (renderType) {
            case NanoVg:
                if (mode == RenderMode.DIRECT) {
                    return new NanoVgDirectGraphicsRenderer();
                } else {
                    return new NanoVgAtlasGraphicsRenderer();
                }

            case ShapeRenderer:
                return new ShapeRendererGraphicsRenderer();

            case Direct:
                return new DirectGraphicsRenderer();

            default:
                throw new GdxRuntimeException("Unsupported renderer type: " + renderType);
        }
    }

    /**
     * Returns all renderers needed to draw the shape.
     */
    public final GraphicsRenderer[] getRenderers() {
        for (; generatedIndex < generators.size(); generatedIndex++) {
            GraphicsGenerator generator = generators.get(generatedIndex);

            for (Graphics g : generator.generateGraphics(this)) {
                GraphicsRendererType rendererType = g.getRendererType();

                if (!renderers.containsKey(rendererType)) {
                    renderers.put(rendererType, createRenderer(rendererType));
                    cachedRendererList = null;
                }

                renderers.get(rendererType).appendGraphics(g);
            }
        }

        if (cachedRendererList == null) {
            // This is actually pretty expensive in HTML, so we cache it...
            cachedRendererList = renderers.values().toArray(new GraphicsRenderer[renderers.size()]);
        }

        return cachedRendererList;
    }

    @Override
    public final BaseDodlesViewGroup getParentDodlesViewGroup() {
        return CommonActorOperations.getParentDodlesViewGroup(this);
    }

    @Override
    public final String getParentViewID() {
        return CommonActorOperations.getParentView(this).getName();
    }
    
    /**
     * Returns the atlas manager.
     */
    protected final FrameBufferAtlasManager getAtlasManager() {
        return atlasManager;
    }

    private void drawDirect(DodlesSpriteBatch batch, float parentAlpha, Vector2 drawOffset) {
        for (GraphicsRenderer renderer : getRenderers()) {
            renderer.draw(batch, parentAlpha, drawOffset);
        }
    }

    private void drawToAtlas(DodlesSpriteBatch batch, final float parentAlpha, Vector2 drawOffset) {
        if (atlasManager == null) {
            throw new UnsupportedOperationException("The shape must be passed an atlas manager to be able to use ATLAS RenderMode!");
        }

        if (getName() == null) {
            // Don't draw anything that hasn't been given a name yet...
            return;
        }

        batch.setColor(1, 1, 1, parentAlpha);

        for (AtlasOffset offset : offsetsRenderedTo) {
            FrameBufferAtlasSlice slice = atlasManager.getAtlasSlice(getName(), offset);

            float x = offset.getXOffset();
            float y = offset.getYOffset();

            if (drawOffset != null) {
                x += drawOffset.x;
                y += drawOffset.y;
            }

            batch.draw(
                    slice.getRegion(),
                    x,
                    y,
                    AtlasOffset.RENDERED_SIZE,
                    AtlasOffset.RENDERED_SIZE
            );
        }

        batch.setColor(Color.WHITE);
    }

    // CHECKSTYLE.OFF: DesignForExtension - need full override by child
    @Override
    public Shape dodleClone(IdDatabase idDB, ObjectManager objectManager) {
        Shape shape = new Shape(idDB.getNewID(getName()), trackingID, atlasManager, strokeConfig.cpy());
        ActorMixins.commonClone(this, objectManager, shape);
        
        if (this.getCustomConfig() != null) {
            shape.setCustomConfig(this.getCustomConfig().cpy());
        }

        for (GraphicsGenerator generator : this.getGenerators()) {
            shape.addGenerator(generator);
        }

        shape.setRenderMode(this.getRenderMode());

        return shape;
    }
    // CHECKSTYLE.ON: DesignForExtension

    @Override
    public final void writeConfig(Json json) {
        writeConfig(json, null);
    }
    
    /**
     * Writes the JSON configuration, with an optional JSON object name.
     */
    public final void writeConfig(Json json, String objectName) {
        if (objectName == null) {
            json.writeObjectStart();
        } else {
            json.writeObjectStart(objectName);
        }
        
        ActorMixins.exportToJson(this, json);
        
        if (customConfig != null) {
            json.writeObjectStart("customToolConfig");
            customConfig.writeConfig(json);
            json.writeObjectEnd();
        }
        
        json.writeObjectStart("strokeConfig");
        strokeConfig.writeConfig(json);
        json.writeObjectEnd();
        
        json.writeArrayStart("eraserShapes");
        for (EraserShape eraserShape : eraserShapes) {
            eraserShape.writeConfig(json);
        }
        json.writeArrayEnd();
        
        json.writeValue("renderMode", getRenderMode().name());
        extendConfig(json);
        json.writeObjectEnd();
    }
    
    /**
     * Allows extension of the JSON configuration by subclasses.
     */
    protected void extendConfig(Json json) { }
    
    /**
     * Returns the offsets rendered to.
     */
    public final List<AtlasOffset> getOffsetsRenderedTo() {
        return Collections.unmodifiableList(offsetsRenderedTo);
    }
    
    /**
     * Adds a new offset rendered to.
     */
    public final void addOffsetRenderedTo(AtlasOffset offset) {
        if (!offsetsRenderedTo.contains(offset)) {
            offsetsRenderedTo.add(offset);
        }
    }
    
    /**
     * Adds an "eraser shape" to this shape that will delete existing draw paths.
     */
    public final void addEraserShape(Shape eraserShape) {        
        Vector2 eraserPoint = CommonActorOperations.dodleToLocalCoordinates(this, new Vector2(eraserShape.getX(), eraserShape.getY()));
        float dodleRotation = CommonActorOperations.getDodleRotation(this);
        
        eraserShapes.add(new EraserShape(eraserShape, eraserPoint, dodleRotation * -1, 1f / CommonActorOperations.getDodleScale(this)));
        this.regenerate();
    }
    
    /**
     * Returns the eraser shapes attached to this shape.
     */
    public final List<EraserShape> getEraserShapes() {
        return Collections.unmodifiableList(eraserShapes);
    }
    
    /**
     * Removes an "eraser shape".
     */
    public final void removeEraserShape(Shape eraserShape) {
        eraserShapes.remove(eraserShape);
        this.regenerate();
    }
    
    /**
     * Defines the available shape render modes.
     */
    public enum RenderMode {
        DIRECT,
        ATLAS
    }
}
