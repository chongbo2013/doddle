package com.dodles.gdx.dodleengine.scenegraph.chest;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActorFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a character in the chest.
 */
public class ChestCharacter {
    private final DodlesActor actor;
    
    private ArrayList<CharacterInstance> instances = new ArrayList<CharacterInstance>();
    private String characterName;
    
    public ChestCharacter(String characterName, DodlesActor actor) {
        this.characterName = characterName;
        this.actor = actor;
    }
    
    public ChestCharacter(DodlesActorFactory actorFactory, IdDatabase idDB, JsonValue json, ObjectManager objectManager, DodleStageManager stageManager) {
        this.characterName = json.getString("characterName");
        this.actor = actorFactory.createFromJson(idDB, json.get("actor").get(0));
        
        JsonValue instancesJson = json.get("instances");
        
        for (int i = 0; i < instancesJson.size; i++) {
            CharacterInstance instance = new CharacterInstance(actorFactory, idDB, instancesJson.get(i), objectManager, this, stageManager);
            instances.add(instance);
            objectManager.addActor(instance);
        }
    }
    
    /**
     * Returns the name of the character.
     */
    public final String getCharacterName() {
        return characterName;
    }
    
    /**
     * Sets the name of the character.
     */
    public final void setCharacterName(String newCharacterName) {
        this.characterName = newCharacterName;
    }
    
    /**
     * Returns the actor for this character.
     */
    public final DodlesActor getActor() {
        return actor;
    }
    
    /**
     * Adds an instance of this character.
     */
    public final void addInstance(CharacterInstance instance) {
        instances.add(instance);
    }
    
    /**
     * Returns the instances for this character.
     */
    public final List<CharacterInstance> getInstances() {
        return instances;
    }
    
    /**
     * Removes an instance of this character.
     */
    public final void removeInstance(CharacterInstance instance) {
        instances.remove(instance);
    }
    
    /**
     * Writes the character configuration to JSON.
     */
    public final void writeConfig(Json json) {
        json.writeObjectStart();
        
        // Yeah, this isn't really an array, but DodlesActors assume they're in an array... :(
        json.writeArrayStart("actor");
        actor.writeConfig(json);
        json.writeArrayEnd();
        
        json.writeArrayStart("instances");
        for (CharacterInstance instance : instances) {
            instance.writeConfig(json);
        }
        json.writeArrayEnd();
        
        json.writeValue("characterName", characterName);
        
        json.writeObjectEnd();
    }
}
