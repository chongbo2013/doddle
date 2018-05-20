package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dodles.gdx.dodleengine.DodleReference;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActorFactory;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.ProcessAfterLoad;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import de.hypergraphs.hyena.core.shared.data.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;

/**
 * Command that imports a dodle into the current dodle..
 */
public class ImportDodleCommand implements Command, Importable {
    public static final String COMMAND_NAME = "importdodle";
    
    private final DodlesActorFactory actorFactory;
    private final GroupHelper groupHelper;
    private final ObjectManager objectManager;
    
    private String dodleID;
    private String jsonData;
    private DodlesGroup hostGroup;
    
    // Use the same ID DB for multiple undo/redos to make sure the output is the same
    private IdDatabase idDB = new IdDatabase();
    
    @Inject
    public ImportDodleCommand(DodlesActorFactory actorFactory, GroupHelper groupHelper, ObjectManager objectManager) {
        this.actorFactory = actorFactory;
        this.groupHelper = groupHelper;
        this.objectManager = objectManager;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(String newDodleID, String newJsonData) {
        // TODO: Select target object/scene id?  For right now, just import all objects in root scene.
        dodleID = newDodleID;
        jsonData = newJsonData;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        // TODO: Support animations/characters...
        JsonValue data = new JsonReader().parse(jsonData);
        String importedTrackingID = data.getString("trackingID");
        HashMap<String, DodleReference> importedReferences = DodleReference.loadReferences(data);
        
        objectManager.addReference(new DodleReference(importedTrackingID, dodleID));
        
        // TODO: how to support multiple scenes/an object inside a scene?
        ArrayList<Scene> scenes = actorFactory.loadScenes(idDB, data);
        Scene sceneToImport = scenes.get(0);
        Scene destScene = objectManager.getScene();
        hostGroup = new DodlesGroup(UUID.uuid(), importedTrackingID);
        hostGroup.setOriginalID(idDB.getOldID(sceneToImport.getName()));
        hostGroup.getVisiblePhase().setOriginalID(idDB.getOldID(sceneToImport.getActiveViewID()));
        
        SnapshotArray<Actor> actors = sceneToImport.getChildren();
        Actor[] actorSnapshot = actors.begin();
        
        for (int i = 0, n = actors.size; i < n; i++) {
            ArrayList<DodlesActor> flattenedActors = new ArrayList<DodlesActor>();
            flattenActors((DodlesActor) actorSnapshot[i], flattenedActors);
            
            for (DodlesActor flattened : flattenedActors) {
                // TODO: Can we remove this after things get settled?
                if (flattened.getTrackingID() == null) {
                    flattened.setTrackingID(importedTrackingID);
                }
                
                objectManager.addReference(importedReferences.get(flattened.getTrackingID()));
                objectManager.addActor(flattened);
            }
            
            hostGroup.addActor(actorSnapshot[i], null);
        }
        
        actors.end();
        
        destScene.addActor(hostGroup, null);
        objectManager.addActor(hostGroup);
        hostGroup.updateOrigin();
        
        for (String newID : idDB.getNewIDs()) {
            DodlesActor curActor = objectManager.getActor(newID);
            
            if (curActor instanceof ProcessAfterLoad) {
                ((ProcessAfterLoad) curActor).afterLoad(objectManager);
            }
        }
    }

    @Override
    public final void undo() {
        groupHelper.removeChildFromGroup(hostGroup);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("dodleID", dodleID);
        json.writeValue("jsonData", jsonData);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        dodleID = json.getString("dodleID");
        jsonData = json.getString("jsonData");
    }
    
    private void flattenActors(DodlesActor actor, ArrayList<DodlesActor> actors) {
        actors.add(actor);
        
        if (actor instanceof BaseDodlesViewGroup) {
            BaseDodlesViewGroup group = (BaseDodlesViewGroup) actor;
            
            for (Actor child : (SnapshotArray<Actor>) group.getChildren()) {
                flattenActors((DodlesActor) child, actors);
            }
        }
    }

    @Override
    public final String getObjectID() {
        return hostGroup.getName();
    }
}
