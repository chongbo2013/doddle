package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.tools.scene.SceneUIListPanel;
import com.dodles.gdx.dodleengine.util.NumbersToLetters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A dodles group that passes through child operations to it's active "view".
 */
public abstract class BaseDodlesViewGroup<TView extends BaseDodlesGroup> extends BaseDodlesGroup {
    public static final String DEFAULT_VIEW_SUFFIX = "-defaultview";
    
    private final HashMap<String, TView> viewMap = new HashMap<String, TView>();

    
    public BaseDodlesViewGroup(String id, String trackingID) {
        super(id, trackingID);
        clearChildren(trackingID);
    }
    
    public BaseDodlesViewGroup(DodlesActorFactory actorFactory, IdDatabase idDB, JsonValue json) {
        super(actorFactory, idDB, json);
    }
    
    /**
     * Returns the "active" view, which new objects will be added to.
     */
    public abstract TView getActiveView();
    
    /**
     * Returns the ID of the active view.
     */
    public final String getActiveViewID() {
        return getActiveView().getName();
    }
    
    @Override
    @Deprecated
    public final void addActor(Actor actor) {
        throw new UnsupportedOperationException("Use the viewID override!");
        
    }
    
    /**
     * Adds an actor to the specified phase.
     */
    public final void addActor(Actor actor, String viewID) {
        this.viewMap.get(validateViewID(viewID)).addActor(actor);
    }
    
    @Override
    @Deprecated
    public final void addActorAt(int index, Actor actor) {
        throw new UnsupportedOperationException("Use the viewID override!");
    }
    
    /**
     * Adds an actor at the specified index to the specified phase.
     */
    public final void addActorAt(int index, Actor actor, String viewID) {
        this.viewMap.get(validateViewID(viewID)).addActorAt(index, actor);
    }
    
    @Override
    @Deprecated
    public final void addActorBefore(Actor actorBefore, Actor actor) {
        throw new UnsupportedOperationException("Use the phaseID override!");
    }
    
    /**
     * Adds an actor before the actorBefore, in the specified phase.
     */
    public final void addActorBefore(Actor actorBefore, Actor actor, String viewID) {
        this.viewMap.get(validateViewID(viewID)).addActorBefore(actorBefore, actor);
    }
    
    @Override
    @Deprecated
    public final void addActorAfter(Actor actorAfter, Actor actor) {
        throw new UnsupportedOperationException("Use the phaseID override!");
    }
    
    /**
     * Adds an actor after actorAfter, in the specified view.
     */
    public final void addActorAfter(Actor actorAfter, Actor actor, String viewID) {
        this.viewMap.get(validateViewID(viewID)).addActorAfter(actorAfter, actor);
    }
    
    @Override
    public final boolean removeActor(Actor actor, boolean unfocus) {
        boolean result = false;
        
        // remove from any view
        for (TView view : viewMap.values()) {
            result |= view.removeActor(actor);
        }
        
        return result;
    }
    
    @Override
    public final void clearChildren() {
        throw new UnsupportedOperationException("Use the trackingID override!");
    }

    @Override
    public final SnapshotArray<Actor> getChildren() {
        return getActiveView().getChildren();
    }

    /**
     * we are inception, make sure to get the real children as these are the droids we are interested in.
     * @return
     */
    public final SnapshotArray<Actor> getReallyRealChildren() {
        return super.getChildren();
    }
    
    @Override
    protected final void childrenChanged() {
        if (viewMap != null) {
            // Can be null during object init
            viewMap.clear();
        
            for (Actor view : super.getChildren()) {
                viewMap.put(view.getName(), (TView) view);
            }
        }
    }
    
    /**
     * Clears children, adding a blank view with the given tracking ID.
     */
    public final void clearChildren(String newTrackingID) {
        clearViews();
        onClearChildren();
    }
    
    /**
     * Replaces the views with the given list.
     */
    public final void replaceViews(ArrayList<TView> views) {
        super.clearChildren();
        viewMap.clear();
        
        for (TView view : views) {
            addView(view);
        }
    }
    
    /**
     * Called after clearChildren.
     */
    protected void onClearChildren() {
    }
    
    /**
     * Just clears the views.
     */
    protected final void clearViews() {
        super.clearChildren();
        viewMap.clear();
        onClearViews();
    }
    
    /**
     * Called after clearViews.
     */
    protected void onClearViews() {
    }
    
    /**
     * Returns the phase with the given ID.
     */
    public final TView getView(String viewID) {
        return viewMap.get(validateViewID(viewID));
    }
    
    /**
     * Returns all phases for the group.
     */
    public final List<TView> getViews() {
        ArrayList<TView> result = new ArrayList<TView>();
        
        for (Actor child : super.getChildren()) {
            result.add((TView) child);
        }
        
        return Collections.unmodifiableList(result);
    }
    
    /**
     * Returns the view map.
     */
    protected final HashMap<String, TView> getViewMap() {
        return viewMap;
    }
    
    /**
     * Adds a new, preexisting phase.
     */
    public final void addView(TView view) {
        if (!viewMap.containsKey(view.getName())) {
            viewMap.put(view.getName(), view);
            assignNumberNameValue(view);
            super.addActor(view);
        }
    }
    
    /**
     * Adds a new, preexisting phase.
     */
    public final void addViewAt(int index, TView view) {
        if (!viewMap.containsKey(view.getName())) {
            viewMap.put(view.getName(), view);
            assignNumberNameValue(view);
            super.addActorAt(index, view);
        }
    }
    
    /**
     * Removes the phase with the given ID.
     */
    public final void removeView(String id) {
        super.removeActor(getView(id), true);
        viewMap.remove(id);
    }
    
    /**
     * If the given id is null, returns the default view suffix.
     */
    protected final String validateViewID(String id) {
        if (id == null) {
            return getName() + DEFAULT_VIEW_SUFFIX;
        }
        
        return id;
    }

    // CHECKSTYLE.OFF: DesignForExtension - seriously, we really know what we are doing...
    @Override
    protected void onWriteConfig(Json json) {
        super.onWriteConfig(json);
    }
    // CHECKSTYLE.ON: DesignForExtension

    private void assignNumberNameValue(TView view) {
        SnapshotArray<Actor> actors;
        Integer nextId = null;
        if (view.getNumber() == null || view.getDisplayName() == null) {
            actors = super.getChildren();
            nextId = ObjectManager.getNextNumberInList(actors.toArray());

            if (view.getNumber() == null) {
                view.setNumber(nextId);
            }
            if (view.getDisplayName() == null) {
                if (view instanceof Scene) {
                    view.setDisplayName(SceneUIListPanel.DEFAULT_SCENE_NAME + " " + nextId);
                } else if (view instanceof Layer) {
                    String name = NumbersToLetters.numberToLetters(nextId - 1);
                    view.setDisplayName(SceneUIListPanel.DEFAULT_LAYER_NAME + " " + name);
                }
            }
        }
    }
}
