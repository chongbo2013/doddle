package com.dodles.gdx.dodleengine.commands.phase;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActorFactory;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Phase;

import javax.inject.Inject;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Updates a Phase - for use within the OkStack.
 */
public class UpdatePhaseValuesCommand implements Command {
    public static final String COMMAND_NAME = "updatePhaseValues";

    private ObjectManager objectManager = null;
    private DodlesActorFactory dodlesActorFactory = null;

    private String groupID;

    private String undoPhaseValueUpdates;
    private String undoVisiblePhaseName;

    public UpdatePhaseValuesCommand() {

    }

    @Inject
    public UpdatePhaseValuesCommand(ObjectManager objectManager, DodlesActorFactory dodlesActorFactory) {
        this.objectManager = objectManager;
        this.dodlesActorFactory = dodlesActorFactory;
    }
    
    /**
     * Initializes the command, updating the phase values for the given group ID.
     */
    // CHECKSTYLE.OFF: DesignForExtension - subclasses need to override
    public void init(String pGroupID) {
        groupID = pGroupID;
        undoPhaseValueUpdates = serializePhaseValues();

        DodlesGroup phaseGroup = (DodlesGroup) objectManager.getActor(groupID);
        Phase phase = phaseGroup.getVisiblePhase();
        undoVisiblePhaseName = phase.getDisplayName();
    }
    // CHECKSTYLE.ON: DesignForExtension

    // CHECKSTYLE.OFF: DesignForExtension - subclasses need to override
    @Override
    public String getName() {
        return COMMAND_NAME;
    }
    // CHECKSTYLE.ON: DesignForExtension

    // CHECKSTYLE.OFF: DesignForExtension - subclasses need to override
    @Override
    public void execute() {
        undoPhaseValueUpdates = serializePhaseValues();
        deserializePhaseValues(undoPhaseValueUpdates);
    }
    // CHECKSTYLE.ON: DesignForExtension

    // CHECKSTYLE.OFF: DesignForExtension - subclasses need to override
    @Override
    public void undo() {
        deserializePhaseValues(undoPhaseValueUpdates);
    }
    // CHECKSTYLE.ON: DesignForExtension

    // CHECKSTYLE.OFF: DesignForExtension - subclasses need to override
    @Override
    public void writeConfig(Json json) {
        json.writeValue("groupID", groupID);
        json.writeValue("undoPhaseValueUpdates", undoPhaseValueUpdates);
        json.writeValue("undoVisiblePhaseName", undoVisiblePhaseName);
    }
    // CHECKSTYLE.ON: DesignForExtension

    // CHECKSTYLE.OFF: DesignForExtension - subclasses need to override
    @Override
    public void loadConfig(JsonValue json, CommandFactory factory) {
        groupID = json.getString("groupID");
        undoPhaseValueUpdates = json.getString("undoPhaseValueUpdates");
        undoVisiblePhaseName = json.getString("undoVisiblePhaseName");
    }
    // CHECKSTYLE.ON: DesignForExtension

    /**
     * serialize the Phase Values to json.
     * @return
     */
    public final String serializePhaseValues() {
        StringWriter writer = new StringWriter();
        Json json = new Json(JsonWriter.OutputType.json);
        json.setWriter(writer);
        json.setQuoteLongValues(true);

        DodlesGroup phaseGroup = (DodlesGroup) objectManager.getActor(groupID);
        phaseValuesToJson(json, phaseGroup);
        return writer.toString();
    }

    /**
     * deserialize the json to a Phase object.
     * @param phaseValuesJson
     */
    public final void deserializePhaseValues(String phaseValuesJson) {
        DodlesGroup phaseGroup = (DodlesGroup) objectManager.getActor(groupID);
        objectManager.removeActor(groupID);
        JsonValue value = new JsonReader().parse(phaseValuesJson);
        jsonToPhaseValues(value, phaseGroup);
        objectManager.addActor(phaseGroup);
        objectManager.selectActor(phaseGroup.getVisiblePhase());
    }

    /**
     * general helper method to convert a list of JSON phases into a phaseGroup.
     * @param json
     * @param phaseGroup
     */
    private void jsonToPhaseValues(JsonValue json, DodlesGroup phaseGroup) {
        if (json != null) {
            // assume we were able to parse something.  purge all phases
            ArrayList<String> phaseIDs = new ArrayList<String>();
            for (Phase p : phaseGroup.getPhases()) {
                phaseIDs.add(p.getName());
            }

            for (String id : phaseIDs) {
                phaseGroup.removePhase(id);
            }

            for (int i = 0; i < json.size; i++) {
                IdDatabase idDB = new IdDatabase();
                Phase phase = new Phase(dodlesActorFactory, idDB, json.get(i));
                phaseGroup.addPhase(phase);
                if (phase.getDisplayName().equals(undoVisiblePhaseName)) {
                    phaseGroup.setVisiblePhase(phase.getName());
                }
            }
        }
    }

    /**
     * general helper method to convert a list of Phase objects into JSON.
     * @param json
     * @param phaseGroup
     */
    private void phaseValuesToJson(Json json, DodlesGroup phaseGroup) {
        json.writeArrayStart();
        for (Phase phase : phaseGroup.getPhases()) {
            phase.writeConfig(json);
        }
        json.writeArrayEnd();
    }

    /**
     * get the UndoPhaseValues String json.
     * @return
     */
    public final String getUndoPhaseValueUpdates() {
        return undoPhaseValueUpdates;
    }

    /**
     * set the undo phase values json.
     * @param undoPhaseValueUpdates
     */
    public final void setUndoPhaseValueUpdates(String undoPhaseValueUpdates) {
        this.undoPhaseValueUpdates = undoPhaseValueUpdates;
    }
}
