package com.dodles.gdx.dodleengine.commands.spine;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.scenegraph.Spine;
import javax.inject.Inject;

/**
 * A command that removes a spine from a phase.
 */
public class DeleteSpineCommand implements Command {
    public static final String COMMAND_NAME = "deleteSpine";
    
    private final ObjectManager objectManager;
    
    private String id;
    private Spine deletedSpine;
    private Phase phaseSpineDeletedFrom;
    private Phase.DisplayMode oldDisplayMode;
    
    @Inject
    public DeleteSpineCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(String pID) {
        id = pID;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        deletedSpine = (Spine) objectManager.getActor(id);
        phaseSpineDeletedFrom = ((Phase) deletedSpine.getParent());
        oldDisplayMode = phaseSpineDeletedFrom.getDisplayMode();
        phaseSpineDeletedFrom.setSpine(null);
        phaseSpineDeletedFrom.setDisplayMode(Phase.DisplayMode.SOURCE);
        objectManager.removeActor(id);
    }

    @Override
    public final void undo() {
        phaseSpineDeletedFrom.setSpine(deletedSpine);
        phaseSpineDeletedFrom.setDisplayMode(oldDisplayMode);
        objectManager.addActor(deletedSpine);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("id", id);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        id = json.getString("id");
    }
    
}
