package com.dodles.gdx.dodleengine;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dodles.gdx.dodleengine.editor.OkCancelStackFrame;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import com.dodles.gdx.dodleengine.scenegraph.SceneCamera;
import com.dodles.gdx.dodleengine.scenegraph.Transform;
import com.dodles.gdx.dodleengine.scenegraph.Updatable;
import com.dodles.gdx.dodleengine.scenegraph.chest.ChestCharacter;
import com.dodles.gdx.dodleengine.tools.scene.SceneUIListPanel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Manages object tracking and selection.
 */
@PerDodleEngine
public class ObjectManager {
    private final CameraManager cameraManager;
    private final EventBus eventBus;
    private final EngineEventManager eventManager;
    private final DodleStageManager stageManager;
    private final OkCancelStackManager okCancelStack;

    private HashMap<String, DodlesActor> all = new HashMap<String, DodlesActor>();
    private DodlesGroup newObjectGroup = null;
    private BaseDodlesViewGroup activeLayer;
    private String rootSceneID;
    private boolean selectingLocked = false;

    private HashSet<DodlesActor> selectedActors = new HashSet<DodlesActor>();
    private HashSet<String> lockedActorIDs = new HashSet<String>();

    private Scene activeScene;
    private ArrayList<Scene> scenes = new ArrayList<Scene>();

    private HashMap<String, ChestCharacter> chestCharacters = new HashMap<String, ChestCharacter>();
    private HashMap<String, Float> alphaMap = new HashMap<String, Float>();

    private String trackingID;
    private HashMap<String, DodleReference> references = new HashMap<String, DodleReference>();

    @Inject
    public ObjectManager(
            CameraManager cameraManager,
            EventBus eventBus,
            EngineEventManager eventManager,
            DodleStageManager stageManager,
            OkCancelStackManager okCancelStackManager
    ) {
        this.cameraManager = cameraManager;
        this.eventBus = eventBus;
        this.eventManager = eventManager;
        this.stageManager = stageManager;
        this.okCancelStack = okCancelStackManager;
    }

    /**
     * Resets the object manager.
     */
    public final void reset(String newTrackingID, String newRootSceneID, boolean addRootScene) {
        this.trackingID = newTrackingID;
        this.references.clear();
        this.chestCharacters.clear();
        this.rootSceneID = newRootSceneID;
        newObjectGroup = null;
        activeLayer = null;
        all.clear();

        stageManager.getDodleGroup().clear();

        clearScenes();

        if (addRootScene) {
            addScene(rootSceneID);
            Scene s = getActiveScene();
            s.setDisplayName(SceneUIListPanel.DEFAULT_SCENE_NAME + " 1");
        }
    }

    /**
     * Returns the new object group.
     */
    public final DodlesGroup getNewObjectGroup() {
        return newObjectGroup;
    }

    /**
     * Sets the new object group.
     */
    public final void setNewObjectGroup(DodlesGroup newNewObjectGroup) {
        newObjectGroup = newNewObjectGroup;
    }

    /**
     * Returns the actor with the given ID.
     */
    public final DodlesActor getActor(String id) {
        return all.get(id);
    }

    /**
     * Adds the actor to the object display list.
     */
    public final void addActor(DodlesActor actor) {
        if (actor.getName() == null) {
            throw new GdxRuntimeException("Actor must have a name set!");
        }

        all.put(actor.getName(), actor);

        if (actor instanceof BaseDodlesViewGroup) {
            for (Object view : ((BaseDodlesViewGroup) actor).getViews()) {
                BaseDodlesGroup bdgView = (BaseDodlesGroup) view;
                all.put(bdgView.getName(), bdgView);
            }
        }

        DodleReference ref = references.get(actor.getTrackingID());

        if (ref != null) {
            ref.addRef(actor.getName());
        }
    }

    /**
     * Removes the actor from the object display list.
     */
    public final void removeActor(String id) {

        DodlesActor actor = all.get(id);

        if (actor != null) {
            // Remove from scene
            all.remove(id);

            // Clear selected actors if applicable
             if (selectedActors.contains(actor)) {
                 selectedActors.remove(actor);
                 updateStateUiAfterSelection();
             }

            // Cleanup References
            DodleReference ref = references.get(actor.getTrackingID());

            if (ref != null) {
                ref.decRef(actor.getName());

                if (ref.getRefCount() == 0) {
                    references.remove(actor.getTrackingID());
                }
            }
        }
    }

    /**
     * Returns the active layer.
     */
    public final BaseDodlesViewGroup getActiveLayer() {
        if (activeLayer == null) {
            activeLayer = getScene();
        }

        return activeLayer;
    }

    /**
     * Returns all leaf-level actors that are descendants of the given actor.
     */
    public final Array<DodlesActor> getLeafActors(DodlesActor actor) {
        Array<DodlesActor> result = new Array<DodlesActor>();

        if (actor == null) {
            actor = getActiveLayer();
        }

        if ((!selectingLocked && lockedActorIDs.contains(actor.getName())) || alphaMap.containsKey(actor.getName())) {
            return result;
        }

        if (actor instanceof BaseDodlesViewGroup) {
            BaseDodlesViewGroup group = (BaseDodlesViewGroup) actor;

            Array<Actor> children = new Array<Actor>(group.getChildren());
            children.reverse();

            for (Actor child : children) {
                result.addAll(getLeafActors((DodlesActor) child));
            }
        } else {
            result.add(actor);
        }

        return result;
    }

    /**
     * Sets the active layer.
     */
    public final void setActiveLayer(BaseDodlesViewGroup layer) {
        activeLayer = layer;
    }

    /**
     * Returns all actors in the active layer, highest z-index first.
     */
    public final Array<DodlesActor> activeActors() {
        return activeActors(true);
    }

    /**
     * Returns all actors in the active layer, with either highest z-index first (true) or last.
     */
    public final Array<DodlesActor> activeActors(boolean highestZIndexFirst) {
        DodlesActor curActiveLayer = getActiveLayer();
        Array<DodlesActor> result = new Array<DodlesActor>();

        if (curActiveLayer instanceof BaseDodlesViewGroup) {
            BaseDodlesViewGroup group = (BaseDodlesViewGroup) curActiveLayer;

            for (Actor a : (SnapshotArray<Actor>) group.getChildren()) {
                DodlesActor actor = (DodlesActor) a;

                if (!actor.isVisible()) {
                    continue;
                }

                boolean actorLocked = lockedActorIDs.contains(actor.getName());

                if (actorLocked != selectingLocked || alphaMap.containsKey(actor.getName())) {
                    continue;
                }

                result.add(actor);
            }
        }

        if (highestZIndexFirst) {
            result.reverse();
        }

        return result;
    }

    /**
     * Clears all selected actors.
     */
    public final void clearSelectedActors() {
        selectedActors.clear();
        updateStateUiAfterSelection();
    }

    /**
     * Attempts to select an active actor at the given point, returning true if successful.
     */
    public final boolean select(Vector2 dodlePoint) {
        DodlesActor previouslySelectedActor = getSelectedActor();

        selectedActors.clear();

        DodlesActor actor = findActiveActor(dodlePoint);

        if (actor != null && (previouslySelectedActor == null || previouslySelectedActor == actor)) {
            selectActor(actor);
            return true;
        }

        updateStateUiAfterSelection();

        return false;
    }

    /**
     * Selects an active leaf actor at the given point.
     */
    public final DodlesActor selectLeaf(Vector2 dodlePoint) {
        Array<DodlesActor> leafActors = getLeafActors(getActiveLayer());
        return findActor(leafActors, dodlePoint);
    }

    /**
     * Adds the actor at the given point to the multi select list.
     */
    public final boolean addToMultiSelect(DodlesActor actor) {
        return selectedActors.add(actor);
    }

    /**
     * Finds an active actor at the given point.
     */
    public final DodlesActor findActiveActor(Vector2 dodlePosition) {
        return findActor(activeActors(), dodlePosition);
    }

    /**
     * Finds an actor from the supplied list at the given point.
     */
    public final DodlesActor findActor(Array<DodlesActor> actors, Vector2 dodlePosition) {
        for (DodlesActor actor : actors) {
            Vector2 local = CommonActorOperations.dodleToLocalCoordinates(actor, dodlePosition);

            // The built-in libgdx hit testing won't work here because of how we calculate bounds. :(
            Rectangle bounds = actor.getDrawBounds();
            if (bounds != null && bounds.contains(local)) {
                return actor;
            }
        }

        return null;
    }

    /**
     * Selects an actor by ID.
     */
    public final void selectActor(String actorID) {
        selectActor(all.get(actorID));
    }

    /**
     * Selects the given actor.
     */
    public final void selectActor(DodlesActor actor) {
        selectedActors.clear();
        selectedActors.add(actor);
        updateStateUiAfterSelection();
    }

    /**
     * Returns indicator of whether or not an object or objects are selected
     */
    public final boolean hasActorsSelected() {
        return (selectedActors.size() > 0);
    }

    /**
     * Returns the selected actors.
     */
    public final List<DodlesActor> getSelectedActors() {
        ArrayList<DodlesActor> result = new ArrayList<DodlesActor>();
        result.addAll(selectedActors);
        return result;
    }

    /**
     * Returns the first selected actor.
     */
    public final DodlesActor getSelectedActor() {
        if (selectedActors.size() > 0) {
            return selectedActors.iterator().next();
        }

        return null;
    }

    /**
     * Returns the locked actor ids.
     */
    public final ArrayList<String> getLockedActorIDs() {
        ArrayList<String> result = new ArrayList<String>();
        result.addAll(lockedActorIDs);
        return result;
    }

    /**
     * Returns information about all scenes in the dodle.
     */
    public final List<SceneData> allSceneData() {
        ArrayList<SceneData> result = new ArrayList<SceneData>();

        for (int i = 0; i < scenes.size(); i++) {
            Scene scene = scenes.get(i);
            if (scene.getDisplayName() == null) {
                scene.setDisplayName(i + 1 + "");
            }
            if (scene.getNumber() == null) {
                scene.setNumber(new Integer(i + 1));
            }
            result.add(new SceneData(i + 1, scene, scene == activeScene));
        }

        return result;
    }

    /**
     * get the next number in the scene list.
     *
     * @return
     */
    public final Integer getMaxSceneId() {
        return getNextNumberInList(scenes.toArray());
    }

    /**
     * get the next number in the Layer list.
     *
     * @return
     */
    public final Integer getMaxLayerId() {
        return getNextNumberInList(getActiveScene().getLayers().toArray());
    }

    /**
     * analyze the List to determine what the next number should be.
     * - size of array vs max value of the Number + 1.
     *
     * @return
     */
    public static final Integer getNextNumberInList(Object[] actors) {
        // simple starting case
        Integer nextId = new Integer(actors.length + 1);
        //DodleEngine.getLogger().log("ObjectManager", "sizeofArray: " + scenes.size() + "  nextId: " + nextId);

        for (int i = 0; i < actors.length; i++) {
            BaseDodlesGroup s = (BaseDodlesGroup) actors[i];
            //DodleEngine.getLogger().log("ObjectManager", "   sceneNumber: " + s.getNumber() + " nextSceneNumber: " + (s.getNumber() + 1) + " == nextId: " + nextId);
            if ((s.getNumber() != null) && ((s.getNumber() + 1) > nextId)) {
                nextId = new Integer(s.getNumber() + 1);
                //DodleEngine.getLogger().log("ObjectManager", "   new nextId: " + nextId);
            }
        }
        //DodleEngine.getLogger().log("ObjectManager", "   returning - " + nextId);
        return nextId;
    }

    /**
     * Returns the current scene.
     */
    public final Scene getScene() {
        return activeScene;
    }

    /**
     * Gets the scene by index (1-based).
     */
    public final Scene getScene(int number) {
        return scenes.get(number - 1);
    }

    /**
     * Clears the scenes in the dodle.
     */
    public final void clearScenes() {
        scenes.clear();
        activeScene = null;
    }

    /**
     * Gets the scene by object ID.
     */
    public final Scene getScene(String id) {
        for (Scene scene : scenes) {
            if (scene.getName().equals(id)) {
                return scene;
            }
        }

        return null;
    }

    /**
     * Returns all the scenes in the dodle.
     */
    public final List<Scene> getScenes() {
        return Collections.unmodifiableList(scenes);
    }

    /**
     * very unique usage, just get the first scene in the list regardless of the id.
     *
     * @return
     */
    public final Scene getFirstScene() {
        if (scenes.size() > 0) {
            return scenes.get(0);
        } else {
            return null;
        }
    }

    /**
     * Adds a scene to the dodle.
     */
    //public final void addScene(int number, String id) {
    //    addScene(number, new Scene(id, trackingID));
    //}
    public final Scene addScene(String id) {
        Scene scene = new Scene(id, trackingID);
        addScene(scene);
        return scene;
    }

    /**
     * Adds a scene to the dodle.
     */
    //public final void addScene(int number, Scene newScene) {
    //    scenes.add(number - 1, newScene);
    //    stageManager.getDodleGroup().addActor(newScene);
    //    addActor(newScene);
    //    setActiveScene(number);
    //}

    /**
     * Adds a Scene to the dodle.  If the scene ID or displayName are null, generate new ones.
     */
    public final void addScene(Scene newScene) {
        Integer nextId = getNextNumberInList(scenes.toArray());
        if (newScene.getNumber() == null) {
            newScene.setNumber(nextId);
        }
        if (newScene.getDisplayName() == null) {
            newScene.setDisplayName(SceneUIListPanel.DEFAULT_SCENE_NAME + " " + nextId.toString());
        }
        scenes.add(newScene);
        stageManager.getDodleGroup().addActor(newScene);
        addActor(newScene);
        setActiveScene(newScene.getName());
    }

    /**
     * Removes the given scene from the dodle.
     */
    public final void removeScene(String sceneID) {
        Scene scene = getScene(sceneID);
        scenes.remove(scene);
        removeActor(scene.getName());
        setActiveScene(scenes.get(scenes.size() - 1).getName());
    }

    /**
     * Sets the active scene by index (1-based).
     */
    //public final void setActiveScene(int number) {
    //    setActiveScene(scenes.get(number - 1).getName());
    //}

    /**
     * Sets the active scene.
     */
    public final void setActiveScene(String sceneID) {
        if (activeScene != null && activeScene.getName().equals(sceneID)) {
            return;
        }

        newObjectGroup = null;
        selectedActors.clear();

        activeScene = getScene(sceneID);
        activeScene.setVisible(true);
        activeLayer = activeScene;

        for (Scene scene : scenes) {
            if (scene != activeScene) {
                scene.setVisible(false);
            }
        }

        cameraManager.getSceneCamera(getScene(sceneID)).updateCamera();
    }

    /**
     * retrieve the active Scene.
     *
     * @return
     */
    public final Scene getActiveScene() {
        return activeScene;
    }

    /**
     * Returns all actors in the dodle.
     */
    public final Collection<DodlesActor> allActors() {
        return all.values();
    }

    /**
     * Returns all actors in the current scene (including the scene camera).
     */
    public final Map<String, DodlesActor> allActorsInScene(String sceneID) {
        Map<String, DodlesActor> result = getAllDescendants(getActor(sceneID));

        result.put(SceneCamera.CAMERA_ID, cameraManager.getSceneCamera(getScene(sceneID)));

        return result;
    }

    /**
     * Returns all characters in the chest.
     */
    public final Collection<ChestCharacter> allChestCharacters() {
        return chestCharacters.values();
    }

    /**
     * Adds the character to the chest.
     */
    public final boolean addToChest(ChestCharacter character) {
        if (chestCharacters.containsKey(character.getCharacterName())) {
            return false;
        }

        chestCharacters.put(character.getCharacterName(), character);

        return true;
    }

    /**
     * Gets the character with the given name from the chest.
     */
    public final ChestCharacter getChestCharacterByName(String name) {
        return chestCharacters.get(name);
    }

    /**
     * Gets the character with the given ID from the chest.
     */
    public final ChestCharacter getChestCharacterByActorID(String actorID) {
        for (ChestCharacter character : chestCharacters.values()) {
            if (character.getActor().getName().equals(actorID)) {
                return character;
            }
        }

        return null;
    }

    /**
     * Removes the character with the given name from the chest.
     */
    public final void removeFromChest(String name) {
        chestCharacters.remove(name);
    }

    /**
     * Returns all objects that are descendents of the given actor.
     */
    public final Map<String, DodlesActor> getAllDescendants(DodlesActor root) {
        HashMap<String, DodlesActor> result = new HashMap<String, DodlesActor>();
        getAllDescendants(root, result);
        return result;
    }

    /**
     * Drills into the selected actor.
     */
    public final void drill() {
        final DodlesActor object = getSelectedActor();

        if (object != null && object instanceof BaseDodlesViewGroup) {
            final BaseDodlesViewGroup previousLayer = getActiveLayer();
            setActiveLayer((BaseDodlesViewGroup) object);
            unHideObjects();

            hideInactiveLayers();
            final Transform lastTransform = cameraManager.focus(getActiveLayer());

            clearSelectedActors();
            stageManager.updateStateUi();

            okCancelStack.push(new OkCancelStackFrame("Drill", true, false) {
                @Override
                public void execute() {
                    unHideObjects();
                    clearSelectedActors();
                    setActiveLayer(previousLayer);
                    if (!(previousLayer instanceof Scene)) {
                        selectActor(all.get(object.getName()));
                    }
                    hideInactiveLayers();

                    cameraManager.focus(lastTransform);
                }
            });
        }
    }

    /**
     * Unhide all hidden object.
     */
    public final void unHideObjects() {
        for (DodlesActor actor : all.values()) {
            ((Actor) actor).setVisible(true);
        }
    }

    private void hideInactiveLayers() {
        hideEverythingBut(activeLayer);
    }

    private void hideEverythingBut(BaseDodlesViewGroup group) {
        BaseDodlesViewGroup parent = group.getParentDodlesViewGroup();
        if (parent != null) {
            for (Actor sibling : (SnapshotArray<Actor>) parent.getChildren()) {
                if (sibling != group) {
                    sibling.setVisible(false);
                }
            }
        }

        if (!(group instanceof Scene)) {
            hideEverythingBut(parent);
        }
    }

    /**
     * Peels off the active layer.
     */
    public final ArrayList<String> peel() {
        return peel(getSelectedActor().getName());
    }

    /**
     * Overloaded peel call.
     */
    public final ArrayList<String> peel(final String id) {
        return peel(id, new ArrayList<String>());
    }

    /**
     * Peel the given id and any siblings of higher z-index value.
     */
    public final ArrayList<String> peel(final String id, ArrayList<String> result) {
        final DodlesActor object = getActor(id);
        final BaseDodlesViewGroup group = object.getParentDodlesViewGroup();
        final Actor actor = (Actor) object;
        if (actor != null) {
            result.addAll(peelObject(actor));

            if (group != null) {
                SnapshotArray<Actor> children = group.getChildren();

                int curIndex = children.indexOf(actor, true);
                for (Actor child : (SnapshotArray<Actor>) group.getChildren()) {
                    if (children.indexOf(child, true) > curIndex && !alphaMap.containsKey(child.getName())) {
                        result.addAll(peelObject(child));
                    }
                }
            }

            clearSelectedActors();
            stageManager.updateStateUi();
        }

        return result;
    }

    private ArrayList<String> peelObject(Actor actor) {
        ArrayList<String> result = new ArrayList<String>();
        result.add(actor.getName());
        if (actor instanceof Updatable) {
            Updatable thing = (Updatable) actor;
            alphaMap.put(actor.getName(), thing.getStrokeConfig().getOpacity());
            thing.getStrokeConfig().setOpacity(.05f);
            thing.regenerate();
        } else if (actor instanceof BaseDodlesViewGroup) {
            Array<DodlesActor> children = getLeafActors((DodlesActor) actor);
            for (DodlesActor child : children) {
                result.addAll(peelObject((Actor) child));
            }
            alphaMap.put(actor.getName(), 1f);
        }

        return result;
    }


    /**
     * Puts all objects that are descendents of the given actor into the given HashMap.
     */
    private void getAllDescendants(DodlesActor root, HashMap<String, DodlesActor> result) {
        result.put(root.getName(), root);

        if (root instanceof BaseDodlesViewGroup) {
            BaseDodlesViewGroup group = (BaseDodlesViewGroup) root;

            for (Actor child : (SnapshotArray<Actor>) group.getChildren()) {
                getAllDescendants((DodlesActor) child, result);
            }
        }
    }

    /**
     * Updates the state UI after making a selection.
     */
    private void updateStateUiAfterSelection() {
        stageManager.updateStateUi();
        eventBus.publish(EventTopic.DEFAULT, EventType.SELECTED_OBJECT_CHANGED);
        eventManager.fireEvent(EngineEventType.SELECTED_OBJECT_CHANGED);
    }

    /**
     * Unlock the given objects.
     */
    public final void unlockObjects(ArrayList<String> ids) {
        lockedActorIDs.removeAll(ids);
    }

    /**
     * Lock the given objects.
     */
    public final void lockObjects(ArrayList<String> ids) {
        lockedActorIDs.addAll(ids);
    }

    /**
     * Determine whether this objectManager will select locked actors.
     */
    public final void setSelectingLocked(boolean selectingLocked) {
        this.selectingLocked = selectingLocked;
    }

    /**
     * Unpeel the given actor.
     */
    public final void unpeel(final String id) {
        unpeel(new ArrayList<String>() {
            {
                add(id);
            }
        });
    }

    /**
     * Unpeel the list of given actors.
     */
    public final void unpeel(ArrayList<String> ids) {
        for (String id : ids) {
            final DodlesActor object = getActor(id);
            if (object instanceof Updatable) {
                Updatable actor = (Updatable) object;
                if (actor != null) {
                    actor.getStrokeConfig().setOpacity(alphaMap.get(id));
                    actor.regenerate();
                }
            }
            alphaMap.remove(id);
        }

        clearSelectedActors();
        stageManager.updateStateUi();
    }

    /**
     * Returns the tracking ID of the active dodle.
     */
    public final String getTrackingID() {
        return trackingID;
    }

    /**
     * Adds a new dodle reference.
     */
    public final void addReference(DodleReference reference) {
        if (reference != null && !references.containsKey(reference.getTrackingID())) {
            references.put(reference.getTrackingID(), reference);
        }
    }

    /**
     * Imports dodle references from JSON.
     */
    public final void importReferences(JsonValue json) {
        references.clear();

        for (DodleReference ref : DodleReference.loadReferences(json).values()) {
            references.put(ref.getTrackingID(), ref);
        }
    }

    /**
     * Exports dodle references to JSON.
     */
    public final void exportReferences(Json json) {
        json.writeArrayStart("references");

        for (DodleReference reference : references.values()) {
            json.writeObjectStart();
            reference.writeConfig(json);
            json.writeObjectEnd();
        }

        json.writeArrayEnd();
    }

    /**
     * Information about scenes that needs to be exposed outside the object manager.
     */
    public class SceneData {
        private int number;
        private Scene scene;
        private boolean isActive;

        public SceneData(int number, Scene scene, boolean isActive) {
            this.number = number;
            this.scene = scene;
            this.isActive = isActive;
        }

        /**
         * Returns the scene number.
         */
        public final int getNumber() {
            return number;
        }

        /**
         * Returns the scene.
         */
        public final Scene getScene() {
            return scene;
        }

        /**
         * Returns a value indicating whether this is the active scene.
         */
        public final boolean isActive() {
            return isActive;
        }
    }
}
