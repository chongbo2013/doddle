package com.dodles.gdx.dodleengine.commands.chest;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.chest.CharacterInstance;
import com.dodles.gdx.dodleengine.scenegraph.chest.ChestCharacter;
import javax.inject.Inject;

/**
 * A command that creates an instance of a chest character.
 */
public class CreateInstanceCommand implements Command {
    public static final String COMMAND_NAME = "createInstance";

    private final ObjectManager objectManager;
    private final DodleStageManager stageManager;
    
    private String characterID;
    private String instanceID;
    
    @Inject
    public CreateInstanceCommand(ObjectManager objectManager, DodleStageManager stageManager) {
        this.objectManager = objectManager;
        this.stageManager = stageManager;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(String newCharacterID, String newInstanceID) {
        characterID = newCharacterID;
        instanceID = newInstanceID;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        ChestCharacter character = objectManager.getChestCharacterByActorID(characterID);
        CharacterInstance instance = new CharacterInstance(instanceID, objectManager.getTrackingID(), objectManager, character, stageManager);
        character.addInstance(instance);
        objectManager.addActor(instance);
    }

    @Override
    public final void undo() {
        ChestCharacter character = objectManager.getChestCharacterByActorID(characterID);
        CharacterInstance instance = (CharacterInstance) objectManager.getActor(instanceID);
        character.removeInstance(instance);
        objectManager.removeActor(instanceID);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("characterID", characterID);
        json.writeValue("instanceID", instanceID);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        characterID = json.getString("characterID");
        instanceID = json.getString("instanceID");
    }
    
}
