package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;

/**
 * A view within a scene.
 */
public class Layer extends BaseDodlesGroup implements DodlesView, ProcessAfterLoad {
    public static final String ACTOR_TYPE = "Layer";

    /**
     * allowed display modes to be used with the Eyeball.
     * {"eye-open_1", "eye-halfclosed_1", "eye-closed_1"};
     */
    public enum DisplayMode {
        VISIBLE("eye-open_1"),
        PARTIAL("eye-halfclosed_1"),
        HIDDEN("eye-closed_1");

        private final String iconName;

        DisplayMode(String n) {
            this.iconName = n;
        }

        /**
         * Returns the icon name.
         */
        public static DisplayMode getByIconName(String name) {
            for (DisplayMode dm : DisplayMode.values()) {
                if (dm.iconName.equals(name)) {
                    return dm;
                }
            }
            return null;
        }

        /**
         * get the iconName.
         * @return
         */
        public final String getIconName() {
            return iconName;
        }
    }
 
    private String tempPassthroughSceneID;
    private Scene passthroughScene;
    private DisplayMode displayMode = DisplayMode.VISIBLE;
    private boolean blockDisplayMode;
    
    public Layer(String id, String trackingID) {
        super(id, trackingID);
    }
    
    public Layer(DodlesActorFactory actorFactory, IdDatabase idDB, JsonValue json) {
        super(actorFactory, idDB, json);
        
        if (json.has("passthroughSceneID")) {
            // Wait until after graph is loaded to attach passthrough scene in case it hasn't been loaded yet.
            tempPassthroughSceneID = json.getString("passthroughSceneID");
        }
        if (json.has("displayMode")) {
            displayMode = DisplayMode.valueOf(json.getString("displayMode"));
        }
    }

    @Override
    public final String getType() {
        return ACTOR_TYPE;
    }
    
    @Override
    public final Rectangle getDrawBounds() {
        if (passthroughScene != null) {
            return passthroughScene.getDrawBounds();
        }
        
        return super.getDrawBounds();
    }

    @Override
    public final DodlesActor dodleClone(IdDatabase idDB, ObjectManager objectManager) {
        SnapshotArray<Actor> children = getChildren();

        Layer newLayer = new Layer(idDB.getNewID(getName()), getTrackingID());
        newLayer.setNumber(this.getNumber());
        newLayer.setDisplayName(this.getDisplayName());
        newLayer.setDisplayMode(this.getDisplayMode());
        newLayer.setPassthroughScene(this.getPassthroughScene());
        ActorMixins.commonClone(this, objectManager, newLayer);

        int size = children.size;
        for (int i = 0; i < size; i++) {
            DodlesActor child = (DodlesActor) children.get(i);
            DodlesActor clone = child.dodleClone(idDB, objectManager);
            objectManager.addActor(clone);
            newLayer.addActor((Actor) clone);
        }

        return newLayer;
    }
    
    @Override
    public final void onWriteConfig(Json json) {
        if (passthroughScene != null) {
            json.writeValue("passthroughSceneID", passthroughScene.getName());
        }

        if (displayMode != null) {
            json.writeValue("displayMode", displayMode.name());
        }
    }
    
    @Override
    protected final boolean drawOverride(Batch batch, float parentAlpha, float offsetX, float offsetY) {
        if (displayMode == DisplayMode.HIDDEN) {
            return true;
        }
        
        if (passthroughScene != null) {
            Matrix4 originalTransform = batch.getTransformMatrix().cpy();
            Affine2 currentWorld = new Affine2();
            currentWorld.set(originalTransform);
            boolean prevVisible = passthroughScene.isVisible();
            passthroughScene.setVisible(true);
            passthroughScene.setForceTransform(true);
            passthroughScene.setWorldTransformOverride(currentWorld.mul(ActorMixins.getActorTransformMatrix(passthroughScene).mul(ActorMixins.getActorTransformMatrix(this))));
            
            passthroughScene.draw(batch, parentAlpha * drawAlphaMultiplier());
            
            passthroughScene.setWorldTransformOverride(null);
            batch.setTransformMatrix(originalTransform);
            passthroughScene.setForceTransform(false);
            passthroughScene.setVisible(prevVisible);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Sets the scene to passthrough rendering to for the layer.
     */
    public final void setPassthroughScene(Scene scene) {
        passthroughScene = scene;
    }

    @Override
    public final void afterLoad(ObjectManager objectManager) {
        if (tempPassthroughSceneID != null) {
            passthroughScene = objectManager.getScene(tempPassthroughSceneID);
            tempPassthroughSceneID = null;
        }
    }
    
    @Override
    protected final float drawAlphaMultiplier() {
        if (displayMode == DisplayMode.PARTIAL && !blockDisplayMode) {
            return 0.05f;
        }
        
        // HIDDEN is handled in drawOverride
        
        return 1;
    }

    /**
     * gets the display mode for opacity setting of the Layer.
     * @return
     */
    public final DisplayMode getDisplayMode() {
        return displayMode;
    }

    /**
     * sets the display mode for the Layer.
     * @param displayMode
     */
    public final void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
    }

    /**
     * get the passthrough scene.
     * @return
     */
    public final Scene getPassthroughScene() {
        return passthroughScene;
    }

    /**
     * get the hold property for displayMode.
     * @return
     */
    public final boolean getBlockDisplayMode() {
        return blockDisplayMode;
    }

    /**
     * set the blockDisplayMode property.
     * @param blockDisplayMode
     */
    public final void setBlockDisplayMode(boolean blockDisplayMode) {
        this.blockDisplayMode = blockDisplayMode;
    }
}
