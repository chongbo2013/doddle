package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.scenegraph.ProcessAfterLoad;
import com.dodles.gdx.dodleengine.scenegraph.Transform;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * A command that flips an object.
 */
public class CopyCommand implements Command {
    public static final String COMMAND_NAME = "copy";
    private static final int DEFAULT_OFFSET = 40;

    private final ObjectManager objectManager;
    private final GroupHelper groupHelper;

    private String id;
    private int offsetX = DEFAULT_OFFSET;
    private int offsetY = DEFAULT_OFFSET;
    private IdDatabase idDB;
    private DodlesActor clonedActor;

    @Inject
    public CopyCommand(ObjectManager objectManager, GroupHelper groupHelper) {
        this.objectManager = objectManager;
        this.groupHelper = groupHelper;
    }

    /**
     * Initalizes the command.
     */
    public final void init(String pID, IdDatabase idDb) {
        init(pID, idDb, DEFAULT_OFFSET, DEFAULT_OFFSET);
    }
    
    /**
     * Initalizes the command.
     */
    public final void init(String pID, IdDatabase idDb, int pOffsetX, int pOffsetY) {
        this.id = pID;
        this.idDB = idDb;
        this.offsetX = pOffsetX;
        this.offsetY = pOffsetY;
    }

    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        DodlesActor toCopy = objectManager.getActor(this.id);

        clonedActor = toCopy.dodleClone(idDB, objectManager);
        
        if (toCopy instanceof Phase) {
            toCopy.getParentDodlesViewGroup().addView((Phase) clonedActor);
        } else {
            toCopy.getParentDodlesViewGroup().addActor((Actor) clonedActor, toCopy.getParentViewID());
        }

        final Transform ct = toCopy.getBaseTransform();
        Transform nt = new Transform() { {
            setRotation(ct.getRotation());
            setScaleX(ct.getScaleX());
            setScaleY(ct.getScaleY());
            setX(ct.getX() + offsetX);
            setY(ct.getY() + offsetY);
        } };

        clonedActor.updateBaseTransform(nt);

        clonedActor.updateOrigin();
        
        for (String newID : idDB.getNewIDs()) {
            DodlesActor curActor = objectManager.getActor(newID);
            
            if (curActor instanceof ProcessAfterLoad) {
                ((ProcessAfterLoad) curActor).afterLoad(objectManager);
            }
        }
    }

    @Override
    public final void undo() {
        if (!(clonedActor instanceof Phase)) {
            groupHelper.removeChildFromGroup(clonedActor);
        }
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("id", id);
        json.writeObjectStart("idDB");
        idDB.writeConfig(json);
        json.writeObjectEnd();
        json.writeValue("offsetX", offsetX);
        json.writeValue("offsetY", offsetY);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        HashMap<String, String> db = new HashMap<String, String>();

        id = json.getString("id");
        idDB = new IdDatabase(json.get("idDB"));
        offsetX = json.getInt("offsetX");
        offsetY = json.getInt("offsetY");


    }

    /**
     * Get the group id of the cloned shape.
     */
    public final String getCloneID() {
        return clonedActor.getName();
    }
}
