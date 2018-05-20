package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Describes a Command that will change the state of the DodleEngine.
 */
public interface Command {
    /**
     * Returns the name of the command.
     */
    String getName();
    
    /**
     * Executes the command.
     */
    void execute();
    
    /**
     * Undoes the command.
     */
    void undo();
    
    /**
     * Writes the command configuration to the given Json writer.
     */
    void writeConfig(Json json);
    
    /**
     * Loads the command configuration from the given JsonValue.
     */
    void loadConfig(JsonValue json, CommandFactory factory);
}
