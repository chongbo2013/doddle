package com.dodles.gdx.dodleengine.commands.chest;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.GroupHelper;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.LinkActor;
import com.dodles.gdx.dodleengine.scenegraph.chest.CharacterInstance;
import javax.inject.Inject;

/**
 * Command that links an instance of a character to a scene.
 */
public class CreateSceneInstanceCommand implements Command {
    public static final String COMMAND_NAME = "createSceneInstance";

    private final GroupHelper groupHelper;
    private final ObjectManager objectManager;
    
    private String id;
    private String groupID;
    private String phaseID;
    private String instanceID;
    
    @Inject
    public CreateSceneInstanceCommand(GroupHelper groupHelper, ObjectManager objectManager) {
        this.groupHelper = groupHelper;
        this.objectManager = objectManager;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(String pID, BaseDodlesViewGroup pGroup, String pInstanceID) {
        id = pID;
        groupID = pGroup.getName();
        phaseID = pGroup.getActiveViewID();
        instanceID = pInstanceID;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        CharacterInstance instance = (CharacterInstance) objectManager.getActor(instanceID);
        
        LinkActor link = new LinkActor(id, objectManager.getTrackingID(), instance);
        
        instance.addSceneInstance(link);
        objectManager.addActor(link);
        groupHelper.addChildToGroup(groupID, phaseID, link);
    }

    @Override
    public final void undo() {
        LinkActor link = (LinkActor) objectManager.getActor(id);
        ((CharacterInstance) link.getLinkedActor()).removeSceneInstance(link);
        
        // We can't have the grouphelper remove the object from the display list
        // because it'll delete the instance as well!
        groupHelper.removeChildFromGroup(link, true);
        objectManager.removeActor(id);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("id", id);
        json.writeValue("groupID", groupID);
        json.writeValue("phaseID", phaseID);
        json.writeValue("instanceID", instanceID);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        id = json.getString("id");
        groupID = json.getString("groupID");
        phaseID = json.getString("phaseID");
        instanceID = json.getString("instanceID");
    }
}
