package com.dodles.gdx.dodleengine.scenegraph.chest;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.scenegraph.ActorMixins;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActorFactory;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.LinkActor;
import com.dodles.gdx.dodleengine.scenegraph.RootGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a chest character.
 */
public class CharacterInstance extends BaseDodlesGroup {
    public static final String ACTOR_TYPE = "CharacterInstance";
    
    private final ObjectManager objectManager;
    private final ChestCharacter template;
    private final DodleStageManager stageManager;
    private final ArrayList<LinkActor> sceneInstances = new ArrayList<LinkActor>();
    
    private IdDatabase idDB = new IdDatabase();
    
    public CharacterInstance(String id, String trackingID, ObjectManager objectManager, ChestCharacter template, DodleStageManager stageManager) {
        super(id, trackingID);
        
        this.objectManager = objectManager;
        this.template = template;
        this.stageManager = stageManager;
        
        // Add to rootgroup, but set invisible so rootgroup calculations work
        RootGroup rootGroup = stageManager.getDodleGroup();
        rootGroup.addActor(this);
        this.setVisible(false);
        
        rebuildClone();
    }
    
    public CharacterInstance(DodlesActorFactory actorFactory, IdDatabase idDB, JsonValue json, ObjectManager objectManager, ChestCharacter template, DodleStageManager stageManager) {
        super(actorFactory, idDB, json);
        
        this.objectManager = objectManager;
        this.stageManager = stageManager;
        this.template = template;
        
        // Add to rootgroup, but set invisible so rootgroup calculations work
        RootGroup rootGroup = stageManager.getDodleGroup();
        rootGroup.addActor(this);
        this.setVisible(false);
    }
    
    @Override
    public final String getType() {
        return ACTOR_TYPE;
    }
    
    /**
     * Rebuilds the clone from the template character.
     */
    public final void rebuildClone() {
        DodlesActor curClone = getClone();
        
        if (curClone != null && curClone.getParentDodlesViewGroup() != null) {
            this.removeActor((Actor) curClone);
        }
        
        DodlesActor newClone = template.getActor().dodleClone(idDB, objectManager);
        setClone(newClone);
    }
    
    /**
     * Returns the template character.
     */
    public final ChestCharacter getTemplateCharacter() {
        return template;
    }
    
    /**
     * Adds a new scene instance of this character instance.
     */
    public final void addSceneInstance(LinkActor linkActor) {
        sceneInstances.add(linkActor);
    }
    
    /**
     * Returns the scene instances for this character instance.
     */
    public final List<LinkActor> getSceneInstances() {
        return sceneInstances;
    }
    
    /**
     * Removes a scene instance.
     */
    public final void removeSceneInstance(LinkActor linkActor) {
        sceneInstances.remove(linkActor);
    }
    
    private DodlesActor getClone() {
        if (this.getChildren().size > 0) {
            return (DodlesActor) this.getChildren().get(0);
        }
        
        return null;
    }
    
    private void setClone(DodlesActor actor) {
        this.clear();
        this.addActor((Actor) actor);
    }

    @Override
    public final DodlesActor dodleClone(IdDatabase iddb, ObjectManager pObjectManager) {
        DodlesGroup newGroup = new DodlesGroup(idDB.getNewID(getName()), getTrackingID());
        ActorMixins.commonClone(this, pObjectManager, newGroup);

        return newGroup;
    }

    @Override
    protected final float drawAlphaMultiplier() {
        return 1;
    }
}
