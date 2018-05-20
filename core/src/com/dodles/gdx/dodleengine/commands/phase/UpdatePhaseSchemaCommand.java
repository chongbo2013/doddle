package com.dodles.gdx.dodleengine.commands.phase;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActorFactory;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseSchema;

import javax.inject.Inject;
import java.io.StringWriter;

/**
 * Updates a Phase - for use within the OkStack.
 */
public class UpdatePhaseSchemaCommand extends UpdatePhaseValuesCommand {
    public static final String COMMAND_NAME = "updatePhaseSchema";

    private final ObjectManager objectManager;
    private final DodlesActorFactory dodlesActorFactory;

    private String groupID;

    private String undoPhaseSchemaUpdates;

    @Inject
    public UpdatePhaseSchemaCommand(ObjectManager objectManager, DodlesActorFactory dodlesActorFactory) {
        super(objectManager, dodlesActorFactory);
        this.objectManager = objectManager;
        this.dodlesActorFactory = dodlesActorFactory;
    }
    
    /**
     * Initializes the command, updating the phase schema for the given group ID.
     */
    public final void init(String pGroupID) {
        super.init(pGroupID);
        groupID = pGroupID;
        undoPhaseSchemaUpdates = serializePhaseSchema();

        super.setUndoPhaseValueUpdates(super.serializePhaseValues());

    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        undoPhaseSchemaUpdates = serializePhaseSchema();
        deserializePhaseSchema(undoPhaseSchemaUpdates);

        super.setUndoPhaseValueUpdates(super.serializePhaseValues());
        super.deserializePhaseValues(super.getUndoPhaseValueUpdates());

    }

    @Override
    public final void undo() {
        deserializePhaseSchema(undoPhaseSchemaUpdates);
        super.deserializePhaseValues(super.getUndoPhaseValueUpdates());
    }

    @Override
    public final void writeConfig(Json json) {
        super.writeConfig(json);
        json.writeValue("groupID", groupID);
        json.writeValue("undoPhaseSchemaUpdates", undoPhaseSchemaUpdates);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        super.loadConfig(json, factory);
        groupID = json.getString("groupID");
        undoPhaseSchemaUpdates = json.getString("undoPhaseSchemaUpdates");
    }

    private String serializePhaseSchema() {
        StringWriter writer = new StringWriter();
        Json json = new Json(JsonWriter.OutputType.json);
        json.setWriter(writer);
        json.setQuoteLongValues(true);

        DodlesGroup phaseGroup = (DodlesGroup) objectManager.getActor(groupID);
        PhaseSchema phaseSchema = phaseGroup.getPhaseSchema();
        if (phaseSchema != null) {
            json.writeObjectStart();
            phaseSchema.writeConfig(json);
            json.writeObjectEnd();
            return writer.toString();
        } else {
            return null;
        }
    }

    private void deserializePhaseSchema(String phaseSchemaJson) {
        DodlesGroup phaseGroup = (DodlesGroup) objectManager.getActor(groupID);
        if (phaseSchemaJson != null) {
            JsonValue data = new JsonReader().parse(phaseSchemaJson);

            PhaseSchema phaseSchema = new PhaseSchema(data);
            phaseGroup.setPhaseSchema(phaseSchema);
        } else {
            phaseGroup.setPhaseSchema(null);
        }
    }
}
