package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Manages DodleEngine commands.
 */
@PerDodleEngine
public class CommandManager {
    private final Stack<Command> debugStack = new Stack<Command>();
    private final Stack<Command> commandStack = new Stack<Command>();
    private final Stack<Command> redoStack = new Stack<Command>();
    private final EventBus eventBus;

    private CommandFactory commandFactory;

    @Inject
    public CommandManager(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * We need the command factory to register itself with the command manager
     * to prevent circular dependency issues. :(
     */
    public final void registerCommandFactory(CommandFactory cf) {
        commandFactory = cf;
    }

    /**
     * Returns the size of the command stack.
     */
    public final int size() {
        return commandStack.size();
    }

    /**
     * Resets the state of the CommandManager.
     */
    public final void reset() {
        debugStack.clear();
        commandStack.clear();
        redoStack.clear();
        sendCommandStackChangedEvent();
    }

    /**
     * Adds a new command and executes it immediately.
     */
    public final void add(Command command) {
        commandStack.add(command);
        debugStack.add(command);
        redoStack.clear();
        sendCommandStackChangedEvent();
    }

    /**
     * Undoes the command at the top of the stack.
     */
    public final void undo() {
        debugStack.add(new DebugUndoCommand());
        undoRedo(true);
    }

    /**
     * Redoes the last undone command (if any).
     */
    public final void redo() {
        debugStack.add(new DebugRedoCommand());
        undoRedo(false);
    }

    /**
     * Returns true if it's possible to redo.
     */
    public final boolean canRedo() {
        return redoStack.size() > 0;
    }

    /**
     * Exports the commands on the stack to the given Json writer.
     */
    public final void exportCommands(Json json) {
        json.writeArrayStart("commands");
        writeCommands(json, commandStack);
        json.writeArrayEnd();

        exportDebugStack(json);

        //TODO: implement references...
        json.writeArrayStart("references");
        json.writeArrayEnd();
    }
    
    /**
     * Exports the debug command stack to the given Json writer.
     */
    public final void exportDebugStack(Json json) {
        json.writeArrayStart("debug");
        writeCommands(json, debugStack);
        json.writeArrayEnd();
    }

    /**
     * Imports the given commands into the DodleEngine.
     */
    public final void importCommands(JsonValue json) {
        if (commandStack.size() > 0) {
            throw new GdxRuntimeException("Commands already exist - reset must be called before importing JSON!");
        }

        //TODO: implement references...

        for (Command command : parseCommands(commandFactory, json)) {
            command.execute();
            commandStack.add(command);
        }
    }

    /**
     * Parses the commands contained in the JSON into a list.
     */
    public static final List<Command> parseCommands(CommandFactory commandFactory, JsonValue json) {
        JsonValue jsonCommands = json.get("commands");
        ArrayList<Command> result = new ArrayList<Command>();

        for (JsonValue jsonCommand : jsonCommands.iterator()) {
            Command command = commandFactory.createCommand(jsonCommand.getString("name"));
            command.loadConfig(jsonCommand.get("config"), commandFactory);
            result.add(command);
        }

        return result;
    }

    private void writeCommands(Json json, Stack<Command> stack) {
        for (Command command : stack) {
            json.writeObjectStart();
            json.writeValue("name", command.getName());
            json.writeObjectStart("config");
            command.writeConfig(json);
            json.writeObjectEnd();
            json.writeObjectEnd();
        }
    }

    private void undoRedo(boolean undo) {
        Stack<Command> popStack = commandStack;
        Stack<Command> pushStack = redoStack;

        if (!undo) {
            popStack = redoStack;
            pushStack = commandStack;
        }

        if (popStack.size() > 0) {
            Command command = popStack.pop();
            pushStack.push(command);

            if (undo) {
                command.undo();
            } else {
                command.execute();
            }

            sendCommandStackChangedEvent();
        }
    }

    /**
     * Can we undo from here.
     */
    public final boolean canUndo() {
        return commandStack.size() > 0;
    }

    /**
     * Sends event to notify listeners that command stacks changed
     */
    private void sendCommandStackChangedEvent() {
        eventBus.publish(EventTopic.DEFAULT, EventType.COMMAND_STACK_CHANGED, new EventData());
    }

    /**
     * Fake command for logging undos.
     */
    private class DebugUndoCommand extends DebugCommand {
        public DebugUndoCommand() {
            super("UNDO");
        }
    }

    /**
     * Fake command for logging redos.
     */
    private class DebugRedoCommand extends DebugCommand {
        public DebugRedoCommand() {
            super("REDO");
        }
    }

    /**
     * Fake command abstract class.
     */
    private abstract class DebugCommand implements Command {
        private final String name;

        public DebugCommand(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void execute() {
        }

        @Override
        public void undo() {
        }

        @Override
        public void writeConfig(Json json) {
        }

        @Override
        public void loadConfig(JsonValue json, CommandFactory factory) {
        }
    }
}
