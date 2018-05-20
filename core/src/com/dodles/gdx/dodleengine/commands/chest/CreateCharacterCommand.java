package com.dodles.gdx.dodleengine.commands.chest;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.GroupHelper;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.chest.ChestCharacter;
import javax.inject.Inject;

/**
 * A command that creates a character in the chest.
 */
public class CreateCharacterCommand implements Command {
    public static final String COMMAND_NAME = "createCharacter";
    
    private final ObjectManager objectManager;
    private final GroupHelper groupHelper;
    
    private String id;
    private String originalParentID;
    private String originalPhaseID;
    private String characterName;
    
    @Inject
    public CreateCharacterCommand(GroupHelper groupHelper, ObjectManager objectManager) {
        this.groupHelper = groupHelper;
        this.objectManager = objectManager;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(String newID, String newCharacterName) {
        id = newID;
        characterName = newCharacterName;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        executeLive();
    }
    
    /**
     * Executes the command, returning true if it created the character successfully.
     */
    public final boolean executeLive() {
        DodlesActor actor = objectManager.getActor(id);
        ChestCharacter newCharacter = new ChestCharacter(characterName, actor);
        
        if (objectManager.addToChest(newCharacter)) {
            originalParentID = actor.getParentDodlesViewGroup().getName();
            originalPhaseID = actor.getParentViewID();
            groupHelper.removeChildFromGroup(actor, true);
            return true;
        }
        
        return false;
    }

    @Override
    public final void undo() {
        if (originalParentID != null) {
            objectManager.removeFromChest(characterName);
            groupHelper.addChildIDToGroup(originalParentID, originalPhaseID, id);
        }
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("id", id);
        json.writeValue("characterName", characterName);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        id = json.getString("id");
        characterName = json.getString("characterName");
    }
}
