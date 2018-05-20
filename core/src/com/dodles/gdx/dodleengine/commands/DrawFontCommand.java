package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.TextShape;
import com.dodles.gdx.dodleengine.scenegraph.Transform;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.util.JsonUtility;
import javax.inject.Inject;

/**
 * A command that draws text on the canvas.
 */
public class DrawFontCommand implements Command {
    public static final String COMMAND_NAME = "drawFont";
    
    private final AssetProvider assetProvider;
    private final FrameBufferAtlasManager atlasManager;
    private final GroupHelper groupHelper;
    private final ObjectManager objectManager;
    
    private String id;
    private String groupID;
    private String phaseID;
    private Vector2 dodlePoint;
    private StrokeConfig strokeConfig;
    private String text;
    private String previousText;
    private TextShape textRenderer;

    @Inject
    public DrawFontCommand(AssetProvider assetProvider, FrameBufferAtlasManager atlasManager, GroupHelper groupHelper, ObjectManager objectManager) {
        this.assetProvider = assetProvider;
        this.atlasManager = atlasManager;
        this.groupHelper = groupHelper;
        this.objectManager = objectManager;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }
    
    /**
     * Initializes the command when creating new text.
     */
    public final void initCreate(String pID, String pGroupID, String pPhaseID, StrokeConfig pStrokeConfig, String pText, Vector2 pDodlePoint) {
        id = pID;
        groupID = pGroupID;
        phaseID = pPhaseID;
        strokeConfig = pStrokeConfig.cpy();
        text = pText;
        dodlePoint = pDodlePoint.cpy();
    }
    
    /**
     * Initializes the command when editing existing text.
     */
    public final void initEdit(String newID, String newText) {
        id = newID;
        text = newText;
    }

    @Override
    public final void execute() {
        if (dodlePoint == null) {
            // Editing text...
            textRenderer = (TextShape) objectManager.getActor(id);
            previousText = textRenderer.getText();
            textRenderer.setText(text);
        } else {
            // New text...
            textRenderer = new TextShape(id, objectManager.getTrackingID(), atlasManager, strokeConfig, assetProvider);
            Transform transform = new Transform();
            transform.setX(dodlePoint.x);
            transform.setY(dodlePoint.y);
            textRenderer.updateBaseTransform(transform);
            textRenderer.setText(text);
            
            objectManager.addActor(textRenderer);
            
            groupHelper.addChildToGroup(groupID, phaseID, textRenderer);
        }
        
        CommonActorOperations.updateAllOrigins(textRenderer);
    }

    @Override
    public final void undo() {
        if (previousText != null) {
            textRenderer.setText(previousText);
        } else {
            groupHelper.removeChildFromGroup(textRenderer);
        }
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("id", id);
        json.writeValue("text", text);
        
        if (dodlePoint != null) {
            json.writeValue("groupID", groupID);
            json.writeValue("dodlePoint", dodlePoint);


            json.writeObjectStart("strokeConfig");
            strokeConfig.writeConfig(json);
            json.writeObjectEnd();
        }
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        id = json.getString("id");
        text = json.getString("text"); 
        
        if (json.has("dodlePoint")) {
            groupID = json.getString("groupID");
            dodlePoint = JsonUtility.readVector(json.get("dodlePoint"));
            strokeConfig = new StrokeConfig(json.get("strokeConfig"));
        }
    }
}
