package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;

import javax.inject.Inject;

/**
 * Command that deletes an object.
 */
public class DeleteObjectCommand implements Command {
    public static final String COMMAND_NAME = "delete";

    private final ObjectManager objectManager;
    private final GroupHelper groupHelper;

    private String targetID;
    private DodlesActor deletedObject;
    private String deletedParentID;
    private String deletedPhaseID;

    @Inject
    public DeleteObjectCommand(ObjectManager objectManager, GroupHelper groupHelper) {
        this.objectManager = objectManager;
        this.groupHelper = groupHelper;
    }

    /**
     * Initializes the command.
     */
    public final void init(String id) {
        this.targetID = id;
    }

    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        this.deletedObject = objectManager.getActor(targetID);
        this.deletedParentID = this.deletedObject.getParentDodlesViewGroup().getName();
        this.deletedPhaseID = this.deletedObject.getParentViewID();
        groupHelper.removeChildFromGroup(deletedObject, false, false);
    }

    @Override
    public final void undo() {
        groupHelper.addChildToGroup(this.deletedParentID, this.deletedPhaseID, deletedObject);
        objectManager.addActor(deletedObject);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("targetID", targetID);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        targetID = json.getString("targetID");
    }
}
