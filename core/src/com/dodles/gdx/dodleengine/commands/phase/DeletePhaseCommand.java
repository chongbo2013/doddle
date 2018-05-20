package com.dodles.gdx.dodleengine.commands.phase;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Phase;

import javax.inject.Inject;

/**
 * Deletes a phase from an existing PhaseGroup.
 */
public class DeletePhaseCommand implements Command {
    public static final String COMMAND_NAME = "deletePhase";

    private final ObjectManager objectManager;

    private String phaseID;
    private String groupID;
    private Phase phase;

    @Inject
    public DeletePhaseCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }
    
    /**
     * Initializes the command, deleting a phase with id "pID" from the dodlesgroup "pGroupID".
     */
    public final void init(String pID, String pGroupID) {
        phaseID = pID;
        groupID = pGroupID;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        // hold a reference to the Phase
        DodlesGroup phaseGroup = (DodlesGroup) objectManager.getActor(groupID);
        phase = phaseGroup.getPhase(phaseID);

        phaseGroup.removePhase(phaseID);
        objectManager.removeActor(phaseID);
    }

    @Override
    public final void undo() {
        DodlesGroup phaseGroup = (DodlesGroup) objectManager.getActor(groupID);

        // recalculate the boundaries as other things may have changed
        phase.setDefaultBounds(phaseGroup.getDrawBounds());
        phaseGroup.addPhase(phase);
        objectManager.addActor(phase);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("phaseID", phaseID);
        json.writeValue("groupID", groupID);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        phaseID = json.getString("phaseID");
        groupID = json.getString("groupID");
    }
}
