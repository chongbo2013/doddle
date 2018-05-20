package com.dodles.gdx.dodleengine.editor;

import com.badlogic.gdx.graphics.Color;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.brushes.Brush;
import com.dodles.gdx.dodleengine.brushes.BrushRegistry;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.geometry.Geometry;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.geometry.circle.CircleGeometry;
import com.dodles.gdx.dodleengine.geometry.heart.HeartGeometry;
import com.dodles.gdx.dodleengine.geometry.polygon.PolygonGeometry;
import com.dodles.gdx.dodleengine.geometry.polygon.PolygonGeometryConfig;
import com.dodles.gdx.dodleengine.geometry.rectangle.RectangleGeometry;
import com.dodles.gdx.dodleengine.geometry.rectangle.RectangleGeometryConfig;
import com.dodles.gdx.dodleengine.geometry.star.StarGeometry;
import com.dodles.gdx.dodleengine.geometry.star.StarGeometryConfig;
import com.dodles.gdx.dodleengine.geometry.triangle.TriangleGeometry;
import com.dodles.gdx.dodleengine.geometry.triangle.TriangleGeometryConfig;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.geometry.GeometryTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.copy.CopyLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.fliphorizontally.FlipHorizontallyLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.flipvertically.FlipVerticallyLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.merge.MergeLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.trash.TrashLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.unmerge.UnmergeLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.zindex.ZIndexDownLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.zindex.ZIndexUpLayerSubTool;
import com.dodles.gdx.dodleengine.tools.nullTool.NullTool;

import javax.inject.Inject;

/**
 * The EditorStateManager manages the state and process the logic related to the state of the
 * editor. This is an interim solution needed in order to refactor the old libGDX-based editor
 * to seperate out UI view code from its state and logic, and make it easier for the react-based
 * editor to interface with the same logic. After the old libGDX-based editor code is removed,
 * we should consider refactoring the logic to remove this class and have the state and logic
 * managed by their respective subsystem managers.
 */
@PerDodleEngine
public class EditorStateManager {

    //region Properties & Variables
    private final float BRUSH_SIZE_TO_PIXEL_RATIO = 5f;

    // Sub-system References
    private BrushRegistry brushRegistry;
    private CommandManager commandManager;
    private EditorState editorState;
    private EventBus eventBus;
    private GeometryRegistry geometryRegistry;
    private LayerSubToolRegistry layerSubToolRegistry;
    private ToolRegistry toolRegistry;

    // Subsystem State Variables
    private final static String DEFAULT_BRUSH = null;
    private boolean canUndo = false;
    private boolean canRedo = false;

    //endregion Properties & Variables


    //region Constructor
    @Inject
    public EditorStateManager(
            BrushRegistry brushRegistry,
            final CommandManager commandManager,
            EditorState editorState,
            EventBus eventBus,
            GeometryRegistry geometryRegistry,
            LayerSubToolRegistry layerSubToolRegistry,
            ToolRegistry toolRegistry
    ){

        // Store references to sub-systems
        this.brushRegistry = brushRegistry;
        this.commandManager = commandManager;
        this.editorState = editorState;
        this.eventBus = eventBus;
        this.geometryRegistry = geometryRegistry;
        this.layerSubToolRegistry = layerSubToolRegistry;
        this.toolRegistry = toolRegistry;

        // Initialize Registries
        toolRegistry.setActiveTool(NullTool.TOOL_NAME);
        brushRegistry.setActiveBrush(DEFAULT_BRUSH);
        geometryRegistry.setActiveGeometry(null);


        // Initialize Subsystem States
        canRedo = commandManager.canRedo();
        canUndo = commandManager.canUndo();

        // Listen for events from the UI layer
        this.eventBus.addSubscriber(new EventSubscriber(EventTopic.EDITOR) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                switch (eventType) {
                    case CHANGE_TOOL:
                        onToolChange(data.getFirstStringParam());
                            break;
                    case CHANGE_BRUSH:
                        onBrushChange(data.getFirstStringParam());
                        break;
                    case CHANGE_BRUSH_COLOR:
                        onBrushColorChange(Color.valueOf(data.getFirstStringParam()));
                        break;
                    case CHANGE_BRUSH_SIZE:
                        onBrushSizeChange(Float.parseFloat(data.getFirstStringParam()));
                        break;
                    case CHANGE_BRUSH_OPACITY:
                        onBrushOpacityChange(Float.parseFloat(data.getFirstStringParam()));
                        break;
                    case CHANGE_SHAPE:
                        onShapeChange(data.getFirstStringParam());
                        break;
                    case CHANGE_SHAPE_COLOR:
                        onShapeColorChange(Color.valueOf(data.getFirstStringParam()));
                        break;
                    case CHANGE_SHAPE_OPACITY:
                        onShapeOpacityChange(Float.parseFloat(data.getFirstStringParam()));
                        break;
                    case CHANGE_SHAPE_SIZE:
                        onShapeSizeChange(Float.parseFloat(data.getFirstStringParam()));
                        break;
                    case CHANGE_SHAPE_ROUNDING:
                        onShapeRoundingChange(Float.parseFloat(data.getFirstStringParam()));
                        break;
                    case CHANGE_SHAPE_STAR_DEPTH:
                        onShapeStarDepthChange(Float.parseFloat(data.getFirstStringParam()));
                        break;
                    case UNDO:
                        onUndo();
                        break;
                    case REDO:
                        onRedo();
                        break;
                    case LAYER_GROUP:
                        onLayerMerge();
                        break;
                    case LAYER_UNGROUP:
                        onLayerUnmerge();
                        break;
                    case LAYER_FLIP_HORIZONTAL:
                        onLayerFlipHorizontally();
                        break;
                    case LAYER_FLIP_VERTICAL:
                        onLayerFlipVertically();
                        break;
                    case LAYER_FORWARD:
                        onLayerMoveForward();
                        break;
                    case LAYER_FRONT:
                        onLayerMoveToFront();
                        break;
                    case LAYER_BACKWARD:
                        onLayerMoveBackward();
                        break;
                    case LAYER_BACK:
                        onLayerMoveToBack();
                        break;
                    case LAYER_COPY:
                        onLayerCopy();
                        break;
                    case LAYER_DELETE:
                        onLayerDelete();
                        break;
                    case ENTER_CUSTOM_SHAPE_MODE:
                        onEnterCustomShapeMode();
                        break;
                    case ANIM_TOGGLE_PIVOT_POINT_MODE:
                    case ANIMATION_START:
                    case ANIMATION_STOP:
                    case SET_ANIMATION_TOOL_STATE:
                    case OK_CANCEL_STACK_POP_OK:
                    case OK_CANCEL_STACK_POP_CANCEL:
                    case SAVE_DODLE:
                    case LOAD_DODLE:
                    case DELETE_DODLE:
                        // do nothing. handled by related subsystem
                        break;
                    default:
                        System.out.println("EditorStateManager - Received Unhandled Event: " + eventTopic.toString() + "::" + eventType.toString());
                        if (data != null) {
                            System.out.println("EditorStateManager - event data: " + data.forReact());
                        }
                }
            }
        });

        this.eventBus.addSubscriber(new EventSubscriber(EventTopic.DEFAULT) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                switch (eventType) {
                    case COMMAND_STACK_CHANGED:
                        onCommandStackChanged();
                        break;
                }
            }
        });
    }
    //endregion Constructor

    //region Tool Change Logic
    public void onToolChange(String toolName) {
        Tool activeTool = toolRegistry.getActiveTool();
        String activeToolName = (activeTool==null ? NullTool.TOOL_NAME : activeTool.getName());
        if (toolName.equals(activeToolName)) {
            toolRegistry.setActiveTool(null);
        } else {
            toolRegistry.setActiveTool(toolName);
        }
    }
    //endregion Tool Change Logic

    //region Brush Change Logic
    public void onBrushChange(String brushName) {
        String newBrushName = DEFAULT_BRUSH;
        if (brushName != null && !brushName.isEmpty()) {
            System.out.println("currently thinking " + newBrushName + " and not " + brushName);
            // if valid brush name is supplied, consider using it
            Brush activeBrush = brushRegistry.getActiveBrush();
            if (activeBrush == null || !activeBrush.getName().equals(brushName)) {
                // if there is no active brush, use desire brush (so long as its not currently selected)
                newBrushName = brushName;
            }
            System.out.println("currently thinking " + newBrushName + " and not " + brushName);
        }
        brushRegistry.setActiveBrush(newBrushName);
    }
    //endregion Brush Change Logic

    //region Color Management

    public void onBrushColorChange(Color color) {
        // Looking to implement color change for text/shapes? See the logic in ColorSelectorOverlay::setSelectedColor
        // TODO: merge editorState into editorStateManager
        editorState.getStrokeConfig().setColor(color);
    }

    //endregion Color Management

    //region Opacity Management

    public void onBrushOpacityChange(float opacity) {
        System.out.println("LIBGDX - New Brush Opacity" + opacity);
        editorState.getStrokeConfig().setOpacity(opacity);
    }

    //endregion Opacity Management

    //region Opacity Management

    public void onBrushSizeChange(float size) {
        float adjustedSize = size * BRUSH_SIZE_TO_PIXEL_RATIO;
        editorState.getStrokeConfig().setSize(adjustedSize);
    }

    //endregion Opacity Management


    //region Shape Change Logic

    public void onShapeChange(String shapeName) {
        if(shapeName != null) {
            Geometry inputGeometry = geometryRegistry.getGeometry(shapeName);
            if(inputGeometry instanceof RectangleGeometry) {
                ((RectangleGeometry) inputGeometry).intializeRectangleCorners();
            }
            geometryRegistry.setActiveGeometry(shapeName, null, editorState.getStrokeConfig());
            ((GeometryTool) toolRegistry.getActiveTool()).addShapeToCanvas(inputGeometry, inputGeometry.getDefaultGeometryConfig());
        } else {
            geometryRegistry.setActiveGeometry(null);
        }

    }
    //endregion Shape Change Logic

    //region Shape Change Logic
    public void onShapeColorChange(Color color) {
        editorState.getStrokeConfig().setColor(color);
        editorState.getStrokeConfig().setFill(color);
    }

    public void onShapeOpacityChange(float opacity) {
        System.out.println("LIBGDX - New Shape Opacity" + opacity);
        editorState.getStrokeConfig().setOpacity(opacity);
        eventBus.publish(EventTopic.DEFAULT, EventType.REGENERATE_SHAPE);
    }

    public void onShapeSizeChange(float size) {
        geometryRegistry.getGeometryConfig().setSize(size);
        eventBus.publish(EventTopic.DEFAULT, EventType.REGENERATE_SHAPE);
    }

    public void onShapeStarDepthChange(float depth) {
        ((StarGeometryConfig) geometryRegistry.getGeometryConfig()).setDepth(Math.round(depth));
        eventBus.publish(EventTopic.DEFAULT, EventType.REGENERATE_SHAPE);
    }

    public void onShapeRoundingChange(float size) {
        String GeometryType = geometryRegistry.getActiveGeometry().getDefaultGeometryConfig().getType();

        if (GeometryType.equals(RectangleGeometry.GEOMETRY_NAME)) {
            ((RectangleGeometryConfig) geometryRegistry.getGeometryConfig()).setCornerRadius(size);
        } else if (GeometryType.equals(CircleGeometry.GEOMETRY_NAME)) {
            System.out.println("Circle rounding is not supported");
        } else if (GeometryType.equals(PolygonGeometry.GEOMETRY_NAME)) {
            ((PolygonGeometryConfig) geometryRegistry.getGeometryConfig()).setCornerRadius(size);
        } else if (GeometryType.equals(TriangleGeometry.GEOMETRY_NAME)) {
            ((TriangleGeometryConfig) geometryRegistry.getGeometryConfig()).setCornerRadius(size);
        } else if (GeometryType.equals(HeartGeometry.GEOMETRY_NAME)) {
            System.out.println("Heart rounding is not supported");
        } else if (GeometryType.equals(StarGeometry.GEOMETRY_NAME)) {
            ((StarGeometryConfig) geometryRegistry.getGeometryConfig()).setCornerRadius(size);
        }
        eventBus.publish(EventTopic.DEFAULT, EventType.REGENERATE_SHAPE);
    }

    public void onEnterCustomShapeMode() {
        System.out.println("EditorStateManager::onEnterCustomShapeMode");
        GeometryTool geometryTool = (GeometryTool) toolRegistry.getActiveTool();
        if (geometryTool != null) {
            geometryTool.setCustomShapeModeActive(true);
        }
    }

    //endregion Shape Change Logic


    //region Undo / Redo

    private void onUndo() {
        commandManager.undo();
    }

    private void onRedo() {
        commandManager.redo();
    }

    private void onCommandStackChanged() {
        boolean newCanUndo = commandManager.canUndo();
        boolean newCanRedo = commandManager.canRedo();

        if (canUndo != newCanUndo || canRedo != newCanRedo) {
            canUndo = newCanUndo;
            canRedo = newCanRedo;
            eventBus.publish(
                    EventTopic.DEFAULT,
                    EventType.UNDO_REDO_CHANGED,
                    String.valueOf(canUndo),
                    String.valueOf(canRedo)
            );
        }

    }

    //endregion Undo / Redo

    //region Layer Tools

    private void onLayerCopy() {
        ((CopyLayerSubTool) layerSubToolRegistry.getTool(CopyLayerSubTool.TOOL_NAME)).copySelectedObjects();
    }

    private void onLayerDelete() {
        ((TrashLayerSubTool) layerSubToolRegistry.getTool(TrashLayerSubTool.TOOL_NAME)).deleteSelectedObjects();
    }

    private void onLayerMerge() {
        ((MergeLayerSubTool) layerSubToolRegistry.getTool(MergeLayerSubTool.TOOL_NAME)).executeMerge();
    }

    private void onLayerUnmerge() {
        ((UnmergeLayerSubTool) layerSubToolRegistry.getTool(UnmergeLayerSubTool.TOOL_NAME)).executeUnmerge();
    }

    private void onLayerFlipHorizontally() {
        ((FlipHorizontallyLayerSubTool) layerSubToolRegistry.getTool(FlipHorizontallyLayerSubTool.TOOL_NAME)).executeFlip();
    }

    private void onLayerFlipVertically() {
        ((FlipVerticallyLayerSubTool) layerSubToolRegistry.getTool(FlipVerticallyLayerSubTool.TOOL_NAME)).executeFlip();
    }

    private void onLayerMoveBackward() {
        ((ZIndexDownLayerSubTool) layerSubToolRegistry.getTool(ZIndexDownLayerSubTool.TOOL_NAME)).execute();
    }

    private void onLayerMoveForward() {
        ((ZIndexUpLayerSubTool) layerSubToolRegistry.getTool(ZIndexUpLayerSubTool.TOOL_NAME)).execute();
    }

    private void onLayerMoveToBack() {
        // ((ZIndexDownLayerSubTool) layerSubToolRegistry.getTool(ZIndexDownLayerSubTool.TOOL_NAME)).execute();
    }

    private void onLayerMoveToFront() {
        // ((ZIndexDownLayerSubTool) layerSubToolRegistry.getTool(ZIndexDownLayerSubTool.TOOL_NAME)).execute();
    }

    //endregion Layer Tools
}
