package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.geometry.Geometry;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.geometry.GeometryConfig;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import javax.inject.Inject;

/**
 * A command that draws geometric shapes on the canvas.
 */
public class DrawGeometryCommand implements Command {
    public static final String COMMAND_NAME = "drawGeometry";
    
    private final FrameBufferAtlasManager atlasManager;
    private final GroupHelper groupHelper;
    private final ObjectManager objectManager;
    
    private String id;
    private String groupID;
    private String phaseID;
    private StrokeConfig strokeConfig;
    private Geometry geometry;
    private GeometryConfig geometryConfig;
    private GeometryRegistry geometryRegistry;
    private Shape shape;
    private boolean createdShape;
    
    private StrokeConfig originalStrokeConfig;
    private GeometryConfig originalGeometryConfig;
    
    @Inject
    public DrawGeometryCommand(FrameBufferAtlasManager atlasManager, GroupHelper groupHelper, ObjectManager objectManager, GeometryRegistry geometryRegistry) {
        this.atlasManager = atlasManager;
        this.groupHelper = groupHelper;
        this.objectManager = objectManager;
        this.geometryRegistry = geometryRegistry;
    }
    
    /**
     * Initializes the command when creating a new shape.
     */
    public final void init(String pID, DodlesGroup group, StrokeConfig pStrokeConfig, Geometry pGeometry, GeometryConfig pGeometryConfig) {
        id = pID;
        strokeConfig = pStrokeConfig;
        geometry = pGeometry;
        geometryConfig = pGeometryConfig;
        
        if (group != null) {
            groupID = group.getName();
            phaseID = group.getVisiblePhaseID();
        }
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        Shape existingShape = (Shape) objectManager.getActor(id);
        
        if (existingShape != null) {
            originalStrokeConfig = existingShape.getStrokeConfig().cpy();
            originalGeometryConfig = ((GeometryConfig) existingShape.getCustomConfig()).cpy();
            shape = existingShape;
        } else {
            originalStrokeConfig = strokeConfig.cpy();
            originalGeometryConfig = geometryConfig.cpy();
            shape = new Shape(id, objectManager.getTrackingID(), atlasManager);
            createdShape = true;
        }
        
        shape.setStrokeConfig(strokeConfig);
        shape.setCustomConfig(geometryConfig);
        
        if (existingShape == null) {
            geometry.init(shape);
            
            objectManager.addActor(shape);
            groupHelper.addChildToGroup(groupID, phaseID, shape);
        }
        
        CommonActorOperations.updateAllOrigins(shape);
        
        shape.regenerate();
        
        existingShape = shape;
    }

    @Override
    public final void undo() {                
        if (createdShape) {
            strokeConfig = originalStrokeConfig;
            geometryConfig = originalGeometryConfig;
            groupHelper.removeChildFromGroup(shape);
            objectManager.removeActor(shape.getName());
            shape.dispose();
            shape = null;
        } else {
            shape.setStrokeConfig(originalStrokeConfig.cpy());
            shape.setCustomConfig(originalGeometryConfig.cpy());
            shape.regenerate();
        }
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("id", id);
        json.writeValue("groupID", groupID);
        json.writeValue("phaseID", phaseID);
        
        json.writeObjectStart("strokeConfig");
        strokeConfig.writeConfig(json);
        json.writeObjectEnd();
        
        json.writeObjectStart("geometryConfig");
        geometryConfig.writeConfig(json);
        json.writeObjectEnd();
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        id = json.getString("id");
        groupID = json.getString("groupID");
        phaseID = json.getString("phaseID", null);
        strokeConfig = new StrokeConfig(json.get("strokeConfig"));
        geometryConfig = GeometryConfig.create(json.get("geometryConfig"));
        geometry = geometryRegistry.getGeometry(geometryConfig.getType());
    }
    
    /**
     * Returns the shape the geometry is being rendered to.
     */
    public final Shape getShape() {
        return shape;
    }
    
    /**
     * Finalizes the stroke configuration.
     */
    public final void finalizeConfig() {
        strokeConfig = strokeConfig.cpy();
        shape.setStrokeConfig(strokeConfig);
        
        geometryConfig = geometryConfig.cpy();
        shape.setCustomConfig(geometryConfig);
        
        if (createdShape) {
            originalStrokeConfig = strokeConfig;
            originalGeometryConfig = geometryConfig;
        }
        
        CommonActorOperations.updateAllOrigins(shape);
    }
}
