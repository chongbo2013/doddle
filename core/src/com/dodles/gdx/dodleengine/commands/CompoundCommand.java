package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

/**
 * A command that contains other commands and is executed in one undo/redo step in the editor.
 */
public class CompoundCommand implements Command {
    public static final String COMMAND_NAME = "compound";
    
    private List<Command> commands = new ArrayList<Command>();
    
    @Inject
    public CompoundCommand() {
    }
    
    /**
     * Initializes the command.
     */
    public final void init(Command... newCommands) {
        commands = Arrays.asList(newCommands);
    }
    /**
     * Initializes the command.
     */
    public final void init(List<Command> newCommands) {
        commands = newCommands;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }
    
    /**
     * Adds a command to the command list.
     */
    public final void addCommand(Command command) {
        commands.add(command);
    }
    
    @Override
    public final void execute() {
        for (Command command : commands) {
            command.execute();
        }
    }

    @Override
    public final void undo() {
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }
    
    @Override
    public final void writeConfig(Json json) {
        json.writeArrayStart("commands");
        
        for (Command command : commands) {
            json.writeObjectStart();
            json.writeValue("name", command.getName());
            json.writeObjectStart("config");
            command.writeConfig(json);
            json.writeObjectEnd();
            json.writeObjectEnd();
        }
        
        json.writeArrayEnd();
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory commandFactory) {
        commands.clear();
        JsonValue jsonCommands = json.get("commands");
        
        for (JsonValue jsonCommand : jsonCommands.iterator()) {
            Command command = commandFactory.createCommand(jsonCommand.getString("name"));
            command.loadConfig(jsonCommand.get("config"), commandFactory);
            commands.add(command);
        }
    }
}
