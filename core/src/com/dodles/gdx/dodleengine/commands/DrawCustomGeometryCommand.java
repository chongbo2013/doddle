package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.brushes.Brush;
import com.dodles.gdx.dodleengine.brushes.BrushConfig;
import com.dodles.gdx.dodleengine.brushes.BrushRegistry;
import com.dodles.gdx.dodleengine.brushes.PaintBrush;
import com.dodles.gdx.dodleengine.brushes.RulerMode;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.geometry.Geometry;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.geometry.custom.CustomGeometry;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.geometry.GeometryTool;
import com.dodles.gdx.dodleengine.util.JsonUtility;
import com.dodles.gdx.dodleengine.util.LineSmoothingUtility.PointExtractor;
import com.dodles.gdx.dodleengine.util.LineSmoothingUtility.Simplify;
import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A command that draws a brush stroke on the canvas.
 */
public class DrawCustomGeometryCommand implements Command {
    public static final String COMMAND_NAME = "custom_geometry";

    // CHECKSTYLE.OFF: VisibilityModifierCheck // Turned off so we can extend these to the DrawLineCommand.
    protected final BrushRegistry brushRegistry;
    protected final GeometryRegistry geometryRegistry;
    protected final ToolRegistry toolRegistry;
    protected final DodleStageManager stageManager;
    protected final EditorState editorState;
    protected final ObjectManager objectManager;
    protected final FrameBufferAtlasManager atlasManager;
    protected final GroupHelper groupHelper;
    protected final boolean addToObjects;

    protected String id;
    protected String groupID;
    protected String phaseID;
    protected Brush brush;
    protected ArrayList<Vector2> points;
    protected StrokeConfig strokeConfig;
    protected Shape shape;
    protected List<Vector2> realPoints;
    protected ArrayList<Vector2> undoPoints;
    // CHECKSTYLE.ON: VisibilityModifierCheck // Turned off so we can extend these to the DrawLineCommand.

    @Inject
    public DrawCustomGeometryCommand(
            BrushRegistry brushRegistry,
            GeometryRegistry geometryRegistry,
            ToolRegistry toolRegistry,
            DodleStageManager stageManager,
            EditorState editorState,
            ObjectManager objectManager,
            FrameBufferAtlasManager atlasManager,
            GroupHelper groupHelper
    ) {
        this(
                brushRegistry,
                geometryRegistry,
                toolRegistry,
                stageManager,
                editorState,
                objectManager,
                atlasManager,
                groupHelper,
                false
        );
    }

    public DrawCustomGeometryCommand(
            BrushRegistry brushRegistry,
            GeometryRegistry geometryRegistry,
            ToolRegistry toolRegistry,
            DodleStageManager stageManager,
            EditorState editorState,
            ObjectManager objectManager,
            FrameBufferAtlasManager atlasManager,
            GroupHelper groupHelper,
            boolean addToObjects
    ) {
        this.brushRegistry = brushRegistry;
        this.geometryRegistry = geometryRegistry;
        this.toolRegistry = toolRegistry;
        this.stageManager = stageManager;
        this.editorState = editorState;
        this.objectManager = objectManager;
        this.atlasManager = atlasManager;
        this.groupHelper = groupHelper;
        this.addToObjects = addToObjects;
    }

    // CHECKSTYLE.OFF: DesignForExtension // Turned off so we can extend it to the DrawLineCommand.
    @Override
    public String getName() {
        return COMMAND_NAME;
    }
    // CHECKSTYLE.ON: DesignForExtension // Turned off so we can extend it to the DrawLineCommand.

    /**
     * Starts live drawing.
     */
    public void startLiveDrawing(Vector2 dodlePoint) {
        points = new ArrayList<Vector2>();
        realPoints = new ArrayList<Vector2>();

        strokeConfig = editorState.getStrokeConfig().cpy();
        brush = brushRegistry.getBrush(PaintBrush.BRUSH_NAME);

        initializeBrush();

        // TODO: Handle direct draw?

        id = UUID.uuid();
        shape.setName(id);
        stageManager.getDrawGroup().addActor(shape);

        updateLiveDrawing(dodlePoint, false);
    }

    /**
     * Updates a live drawing in progress.
     */
    public void updateLiveDrawing(Vector2 dodlePoint, boolean forceRender) {
        if (brush == null || shape == null) {
            return;
        }

        if (points.isEmpty()) {
            points.add(dodlePoint);
            realPoints.add(dodlePoint);
        } else {
            float distance = realPoints.get(realPoints.size() - 1).dst(dodlePoint);

            if (forceRender || distance > 0.5) {
                realPoints.add(dodlePoint);

                float xTotal = 0;
                float yTotal = 0;
                float numItems = 6;

                int loopStart = realPoints.size() - 6;

                if (loopStart < 0) {
                    numItems = realPoints.size();
                    loopStart = 0;
                }

                for (int i = loopStart; i < realPoints.size(); i++) {
                    xTotal += realPoints.get(i).x;
                    yTotal += realPoints.get(i).y;
                }

                points.add(new Vector2(xTotal / numItems, yTotal / numItems));
            }
        }

        brush.mouseMove(points.get(points.size() - 1));
    }

    /**
     * Update the points in this command.
     */
    public final void update(ArrayList<Vector2> newPoints) {
        points = newPoints;
    }

    /**
     * Ends a live drawing.
     */
    private static PointExtractor<Vector2> latLngPointExtractor = new PointExtractor<Vector2>() {
        @Override
        public double getX(Vector2 point) {
            return point.x * 100;
        }

        @Override
        public double getY(Vector2 point) {
            return point.y * 100;
        }
    };

    public final boolean endLiveDrawing(Vector2 dodlePoint) {
        if (brush == null) {
            return false;
        }

        while (points.size() < 3) {
            updateLiveDrawing(dodlePoint, true);
        }

        while (dodlePoint.dst(points.get(points.size() - 1)) > 0.5) {
            updateLiveDrawing(dodlePoint, true);
        }

        if (shape.getDrawBounds() == null) {
            // Nothing was really drawn...
            return false;
        }

        if (addToObjects) {
            groupID = objectManager.getNewObjectGroup().getName();
            phaseID = objectManager.getNewObjectGroup().getVisiblePhaseID();
        }

        shape.remove();
        finalizeStroke();

        postExecute();

        return true;
    }

    /**
     * Hook for subclasses after EndLiveDrawing finishes.
     */

    protected void postExecute() {
        Vector2[] coords =  points.toArray(new Vector2[points.size()]);
        Simplify<Vector2> simplify = new Simplify<Vector2>(new Vector2[0], latLngPointExtractor);
        Vector2[] simplified = simplify.simplify(coords, 2f, false);

        if(shape != null) {
            groupHelper.removeChildFromGroup(shape);
            shape.dispose();
            shape = null;
            this.points = new ArrayList(Arrays.asList(simplified));
            Geometry inputGeometry = new CustomGeometry(geometryRegistry);
            ((CustomGeometry)inputGeometry).setPointsArray(this.points);
            geometryRegistry.setActiveGeometry("CustomShape", null, editorState.getStrokeConfig());
            ((GeometryTool) toolRegistry.getActiveTool()).addShapeToCanvas(inputGeometry, inputGeometry.getDefaultGeometryConfig());
        }
    }
    
    // CHECKSTYLE.OFF: DesignForExtension // Turned off so we can extend these methods to the DrawLineCommand.
    @Override
    public void execute() {
        shape = (Shape) objectManager.getActor(id);
        if (shape != null) {
            if (shape.getCustomConfig() != null) {
                undoPoints = ((BrushConfig) shape.getCustomConfig()).getPoints();
            }

            if (shape.getParentDodlesViewGroup() != null) {
                groupHelper.removeChildFromGroup(shape);
            }
        }

        initializeBrush();

        for (Vector2 point : points) {
            brush.mouseMove(point);
        }
        finalizeStroke();

        postExecute();
    }

    @Override
    public void undo() {
        if (this.undoPoints != null) {
            if (shape != null) {
                shape.dispose();
            }

            // re-execute with the previous points...
            ArrayList<Vector2> origPoints = this.points;
            this.points = this.undoPoints;
            this.execute();
            this.points = origPoints;
        } else {
            groupHelper.removeChildFromGroup(shape);
            shape.dispose();
            shape = null;
        }
    }

    @Override
    public void writeConfig(Json json) {
        json.writeValue("id", id);
        json.writeValue("groupID", groupID);
        json.writeValue("phaseID", phaseID);
        json.writeValue("brush", brush.getName());
        json.writeValue("points", ((BrushConfig) shape.getCustomConfig()).getPoints());

        json.writeObjectStart("strokeConfig");
        strokeConfig.writeConfig(json);
        json.writeObjectEnd();
    }

    @Override
    public void loadConfig(JsonValue json, CommandFactory factory) {
        id = json.getString("id");
        groupID = json.getString("groupID");
        phaseID = json.getString("phaseID", null);
        brush = brushRegistry.getBrush(json.getString("brush"));
        if (brush == null) {
            for (Brush brush1 : brushRegistry.getBrushes()) {
                brush = brush1;
            }
        }
        points = JsonUtility.readVectorArray(json.get("points"));
        strokeConfig = new StrokeConfig(json.get("strokeConfig"));
    }
    // CHECKSTYLE.ON: DesignForExtension // Turned off so we can extend it to the DrawLineCommand.

    /**
     * Returns a value indicating whether this command should add it's output to the global object list.
     */
    public final boolean shouldAddToObjects() {
        return addToObjects;
    }

    /**
     * Initializes the brush for the stroke.
     */
    protected final void initializeBrush() {
        if (shape == null) {
            shape = (Shape) objectManager.getActor(id);
        }

        if (shape == null) {
            shape = new Shape(id, objectManager.getTrackingID(), atlasManager);
            shape.setStrokeConfig(strokeConfig);
        } else {
            shape.clearGenerators();
        }

        brush.init(shape, RulerMode.NONE);
    }

    private void finalizeStroke() {
        if (addToObjects) {
            objectManager.addActor(shape);
            groupHelper.addChildToGroup(groupID, phaseID, shape);
        }

        BrushConfig newConfig = new BrushConfig();
        newConfig.setPoints(points);
        newConfig.setBrush(brush.getName());
        newConfig.setRulerMode(RulerMode.NONE);
        shape.setCustomConfig(newConfig);
    }
}
