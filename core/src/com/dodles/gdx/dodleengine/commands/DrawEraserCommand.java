package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.math.Rectangle;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.brushes.BrushRegistry;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Command for the eraser brush.
 */
public class DrawEraserCommand extends DrawStrokeCommand {
    public static final String COMMAND_NAME = "eraser";
    
    private final ArrayList<Shape> objectsModified = new ArrayList<Shape>();
    private final ObjectManager objectManager;

    @Inject    
    public DrawEraserCommand(BrushRegistry brushRegistry, GeometryRegistry geometryRegistry, ToolRegistry toolRegistry, DodleStageManager stageManager, EditorState editorState, ObjectManager objectManager, FrameBufferAtlasManager atlasManager, GroupHelper groupHelper) {
        super(brushRegistry, geometryRegistry, toolRegistry, stageManager, editorState, objectManager, atlasManager, groupHelper, false);
        
        this.objectManager = objectManager;
    }
    
    @Override
    public final void undo() {
        for (Shape curModified : objectsModified) {
            curModified.removeEraserShape(shape);
        }
        
        shape.dispose();
        shape = null;
    }
    
    /**
     * Ends a live drawing.
     */
    @Override
    public final void postExecute() {        
        for (DodlesActor leafActor : objectManager.getLeafActors(objectManager.getActiveLayer())) {
            Rectangle leafBounds = CommonActorOperations.getDodleBounds(leafActor);
            if (leafActor instanceof Shape && leafBounds.overlaps(shape.getDrawBounds())) {
                Shape leafShape = (Shape) leafActor;
                leafShape.addEraserShape(shape);
                objectsModified.add(leafShape);
            }
        }
    }
}
