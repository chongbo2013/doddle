package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import javax.inject.Inject;

/**
 * A command that alters the z-index of an actor in it's parent group.
 */
public class ZIndexCommand implements Command {
    public static final String COMMAND_NAME = "zindex";
    
    private final ObjectManager objectManager;
    
    private String id;
    private boolean increment;
    
    @Inject
    public ZIndexCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(String pID, boolean pIncrement) {
        id = pID;
        increment = pIncrement;
    }

    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        execute(increment);
    }

    @Override
    public final void undo() {
        execute(!increment);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("id", id);
        json.writeValue("increment", increment);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        id = json.getString("id");
        increment = json.getBoolean("increment");
    }
    
    private void execute(boolean inc) {
        Actor actor = (Actor) objectManager.getActor(id);
        Group parent = actor.getParent();
        int curIndex = parent.getChildren().indexOf(actor, true);
        int targetIndex = curIndex + 1;
        
        if (!inc) {
            targetIndex = curIndex - 1;
        }
        
        if (targetIndex < 0 || targetIndex >= parent.getChildren().size) {
            return;
        }
        
        actor.getParent().swapActor(curIndex, targetIndex);
    }
}
