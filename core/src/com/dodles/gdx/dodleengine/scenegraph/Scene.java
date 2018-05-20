package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.tools.scene.SceneUIListPanel;

/**
 * The root group for a Scene.
 */
public class Scene extends BaseDodlesViewGroup<Layer> {
    public static final String ACTOR_TYPE = "Scene";

    public Scene(String id, String trackingID) {
        super(id, trackingID);
    }

    public Scene(String id, Integer number, String trackingID) {
        super(id, trackingID);
        setNumber(number);
    }
    
    public Scene(DodlesActorFactory actorFactory, IdDatabase idDB, JsonValue json) {
        super(actorFactory, idDB, json);
        ActorMixins.importFromJson(this, idDB, json);

        // Make sure phase map is initialized
        childrenChanged();
    }
    
    @Override
    public final String getType() {
        return ACTOR_TYPE;
    }

    @Override
    public final Layer getActiveView() {
        SnapshotArray<Actor> actors = new SnapshotArray<Actor>(getReallyRealChildren());
        actors.reverse();
        
        for (Actor actor : actors) {
            Layer layer = (Layer) actor;
            
            if (layer.getDisplayMode() == Layer.DisplayMode.VISIBLE) {
                return layer;
            }
        }
        
        // TODO: What to do if no layers are selected?
        return getViews().get(0);
    }
    
    @Override
    protected final void onClearChildren() {
        addView(new Layer(validateViewID(null), getTrackingID()));
    }

    @Override
    public final DodlesActor dodleClone(IdDatabase idDB, ObjectManager objectManager) {
        Scene newScene = new Scene(idDB.getNewID(getName()), getTrackingID());
        ActorMixins.commonClone(this, objectManager, newScene);
        Integer nextId = objectManager.getMaxSceneId();
        newScene.setNumber(nextId);
        newScene.setDisplayName(SceneUIListPanel.DEFAULT_SCENE_NAME + " " + nextId);
        objectManager.addScene(newScene);
        newScene.clearViews();
        
        for (Layer layer : getViews()) {
            Layer cloneLayer = (Layer) layer.dodleClone(idDB, objectManager);
            newScene.addView(cloneLayer);
            objectManager.addActor(cloneLayer);
        }

        return newScene;
    }
    
    /**
     * Adds a layer to the scene at a specific index.
     */
    public final void addLayer(int position, Layer layer) {
        this.addViewAt(position, layer);
    }

    /**
     * Adds a layer to the Scene.
     * @param layer
     */
    public final void addLayer(Layer layer) {
        this.addView(layer);
    }

    /**
     * get all the Layers for this Scene.
     */
    public final SnapshotArray<Actor> getLayers() {
        return super.getReallyRealChildren();
    }

    @Override
    protected final void onWriteConfig(Json json) {
        super.onWriteConfig(json);
    }

    @Override
    protected final float drawAlphaMultiplier() {
        return 1;
    }
}
