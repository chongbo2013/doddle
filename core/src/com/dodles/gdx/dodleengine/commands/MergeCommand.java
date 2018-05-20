package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.util.JsonUtility;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * A command that merges multiple objects into a single group, or just creates a group.
 */
public class MergeCommand implements Command {
    public static final String COMMAND_NAME = "merge";
    
    private final ObjectManager objectManager;
    private final GroupHelper groupHelper;
    
    private String id;
    private String phaseID;
    private ArrayList<String> selectedObjectIDs;
    private String newGroupParentID;
    private String newGroupParentPhaseID;
    private boolean merge;
    
    @Inject
    public MergeCommand(ObjectManager objectManager, GroupHelper groupHelper) {
        this.objectManager = objectManager;
        this.groupHelper = groupHelper;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(String pID, String pPhaseID, String pNewGroupParentID, String pNewGroupParentPhaseID, boolean pMerge) {
        init(pID, pPhaseID, null, pNewGroupParentID, pNewGroupParentPhaseID, pMerge);
    }

    /**
     * Initializes the command.
     */
    public final void init(String pID, String pPhaseID, ArrayList<String> pSelectedObjectIDs, String pNewGroupParentID, String pNewGroupParentPhaseID, boolean pMerge) {
        id = pID;
        phaseID = pPhaseID;
        selectedObjectIDs = pSelectedObjectIDs;
        newGroupParentID = pNewGroupParentID;
        newGroupParentPhaseID = pNewGroupParentPhaseID;
        merge = pMerge;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        mergeOrUnmerge(merge);
    }

    @Override
    public final void undo() {
        mergeOrUnmerge(!merge);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("id", id);
        json.writeValue("phaseID", phaseID);
        json.writeValue("selectedObjectIDs", selectedObjectIDs);
        json.writeValue("newGroupParentID", newGroupParentID);
        json.writeValue("newGroupParentPhaseID", newGroupParentPhaseID);
        json.writeValue("merge", merge);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        id = json.getString("id");
        phaseID = json.getString("phaseID", null);
        newGroupParentID = json.getString("newGroupParentID");
        merge = json.getBoolean("merge");
        newGroupParentPhaseID = json.getString("newGroupParentPhaseID", null);
        
        selectedObjectIDs = null;
        if (json.hasChild("selectedObjectIDs")) {
            selectedObjectIDs = JsonUtility.readStringArray(json.get("selectedObjectIDs"));
        }
    }
    
    private void mergeOrUnmerge(boolean merging) {
        if (merging) {
            if (newGroupParentID != null) {
                DodlesGroup newGroup = new DodlesGroup(id, objectManager.getTrackingID());
                ((BaseDodlesViewGroup) objectManager.getActor(newGroupParentID)).addActor(newGroup, newGroupParentPhaseID);
                objectManager.addActor(newGroup);
            }
            
            if (selectedObjectIDs != null) {
                // Make sure children are added in z-index order...
                for (DodlesActor actor : objectManager.activeActors(false)) {
                    if (selectedObjectIDs.contains(actor.getName())) {
                        groupHelper.addChildToGroup(id, phaseID, actor);
                    }
                }
            }
        } else {
            DodlesActor actor = objectManager.getActor(id);
            newGroupParentID = actor.getParentDodlesViewGroup().getName();
            newGroupParentPhaseID = actor.getParentViewID();
            selectedObjectIDs = new ArrayList<String>();
            
            if (actor instanceof Group) {
                for (Actor child : ((Group) actor).getChildren()) {
                    selectedObjectIDs.add(child.getName());
                }

                groupHelper.explodeGroup(id);
                groupHelper.removeChildFromGroup(actor);
            }
        }
    }
}
