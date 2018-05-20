package com.dodles.gdx.dodleengine.commands.phase;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import javax.inject.Inject;

/**
 * Adds a new phase to an existing PhaseGroup.
 */
public class AddPhaseCommand implements Command {
    public static final String COMMAND_NAME = "addPhase";
    
    private final ObjectManager objectManager;
    
    private String phaseID;
    private String groupID;
    
    @Inject
    public AddPhaseCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }
    
    /**
     * Initializes the command, adding a phase with id "pID" to the dodlesgroup "pGroupID".
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
        DodlesGroup phaseGroup = (DodlesGroup) objectManager.getActor(groupID);
        DodlesActor newPhase = objectManager.getActor(phaseID);
        
        if (newPhase == null) {
            newPhase = phaseGroup.addEmptyPhase(phaseID, objectManager.getTrackingID());
            ((Phase) newPhase).setDefaultBounds(phaseGroup.getDrawBounds());
            objectManager.addActor(newPhase);
        } else if (!(newPhase instanceof Phase)) {
            throw new GdxRuntimeException("phase must be a phase...");
        } else {
            phaseGroup.addPhase((Phase) newPhase);
        }
    }

    @Override
    public final void undo() {
        DodlesGroup phaseGroup = (DodlesGroup) objectManager.getActor(groupID);
        phaseGroup.removePhase(phaseID);
        objectManager.removeActor(phaseID);
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
