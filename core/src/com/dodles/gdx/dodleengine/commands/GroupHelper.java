package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;


/**
 * Helper methods for grouping-related functionality.
 */
@PerDodleEngine
public class GroupHelper {
    private final ObjectManager objectManager;

    @Inject
    public GroupHelper(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }

    /**
     * Adds the child to the group.
     */
    public final void addChildIDToGroup(String groupID, String phaseID, String childID) {
        ArrayList<String> ids = new ArrayList<String>();
        ids.add(childID);
        addChildIDsToGroup(groupID, phaseID, ids);
    }

    /**
     * Adds the children with the given IDs to the group with the given ID.
     */
    public final void addChildIDsToGroup(String groupID, String phaseID, List<String> childIDs) {
        ArrayList<DodlesActor> children = new ArrayList<DodlesActor>();

        for (String childID : childIDs) {
            DodlesActor actor = objectManager.getActor(childID);

            if (actor != null) {
                children.add(actor);
            }
        }

        if (children.size() > 0) {
            addChildrenToGroup(groupID, phaseID, children);
        }
    }

    /**
     * Adds the child to the group with the given ID.
     */
    public final void addChildToGroup(String groupID, String phaseID, DodlesActor child) {
        ArrayList<DodlesActor> children = new ArrayList<DodlesActor>();
        children.add(child);
        addChildrenToGroup(groupID, phaseID, children);
    }

    /**
     * Adds the children to the group with the given ID.
     */
    public final void addChildrenToGroup(String groupID, String phaseID, List<DodlesActor> children) {
        BaseDodlesViewGroup group = (BaseDodlesViewGroup) objectManager.getActor(groupID);

        for (DodlesActor child : children) {
            if (removeChildFromGroup(child, true)) {
                // Adjust transform on child object to put it in the proper place in the new group
                Vector2 newChildPoint = CommonActorOperations.dodleToLocalCoordinates(group, new Vector2(child.getX(), child.getY()));

                child.setX(newChildPoint.x);
                child.setY(newChildPoint.y);
                child.setScaleX(child.getScaleX() / CommonActorOperations.getDodleScale(group));
                child.setScaleY(child.getScaleY() / CommonActorOperations.getDodleScale(group));
                child.setRotation(child.getRotation() - CommonActorOperations.getDodleRotation(group));
            }

            group.addActor((Actor) child, phaseID);
            ((DodlesActor) child).updateOrigin();
        }

        ((DodlesActor) group).updateOrigin();
    }

    /**
     * Removes the children with the given IDs from their parent groups.
     *
     * @param childIDs
     */
    public final void removeChildIDsFromGroup(List<String> childIDs) {
        ArrayList<DodlesActor> children = new ArrayList<DodlesActor>();

        for (String childID : childIDs) {
            children.add(objectManager.getActor(childID));
        }

        removeChildrenFromGroup(children);
    }

    /**
     * Removes the children from their parent groups.
     */
    public final void removeChildrenFromGroup(List<DodlesActor> children) {
        for (DodlesActor child : children) {
            removeChildFromGroup(child);
        }
    }

    /**
     * Removes the child from it's parent group.
     */
    public final boolean removeChildFromGroup(DodlesActor child) {
        return removeChildFromGroup(child, false);
    }

    /**
     * Removes the child from it's parent group, optionally keeping it on the master display list.
     */
    public final boolean removeChildFromGroup(DodlesActor child, boolean keepOnDisplayList) {
        return removeChildFromGroup(child, keepOnDisplayList, true);
    }

    /**
     * Removes the child from it's parent group, optionally keeping it on the master display list.
     */
    public final boolean removeChildFromGroup(DodlesActor child, boolean keepOnDisplayList, boolean unapplyTransformations) {
        BaseDodlesViewGroup group = child.getParentDodlesViewGroup();
        if (group != null) {
            if (unapplyTransformations) {
                // Unapply transformations applied by the group
                Vector2 newChildPoint = CommonActorOperations.localToDodleCoordinates(group, new Vector2(child.getX(), child.getY()));

                child.setX(newChildPoint.x);
                child.setY(newChildPoint.y);
                child.setScaleX(child.getScaleX() * CommonActorOperations.getDodleScale(group));
                child.setScaleY(child.getScaleY() * CommonActorOperations.getDodleScale(group));
                child.setRotation(child.getRotation() + CommonActorOperations.getDodleRotation(group));
            }

            group.removeActor((Actor) child);

            if (group.getChildren().size > 0 && !(group instanceof Scene)) {
                ((DodlesActor) group).updateOrigin();
            }

            if (!keepOnDisplayList) {
                removeActorFromDisplayList(child);
            }

            return true;
        }

        return false;
    }

    /**
     * Adds an actor (and any children) to the display list (ObjectManager).
     */
    public final void addActorToDisplayList(DodlesActor actor) {
        if (!(actor instanceof Scene)) {
            objectManager.addActor(actor);
        }

        if (actor instanceof Group) {
            for (Actor child : ((Group) actor).getChildren()) {
                addActorToDisplayList((DodlesActor) child);
            }
        }
    }

    /**
     * Removes an actor (and any children) from the display list.
     */
    public final void removeActorFromDisplayList(DodlesActor actor) {
        objectManager.removeActor(actor.getName());

        if (actor instanceof Group) {
            for (Actor child : ((Group) actor).getChildren()) {
                removeActorFromDisplayList((DodlesActor) child);
            }
        }
    }

    /**
     * Explodes the group, adding all children to the group's parent.
     */
    public final List<String> explodeGroup(String id) {
        DodlesActor actor = objectManager.getActor(id);
        ArrayList<String> childIDs = new ArrayList<String>();
        String originalParentID = actor.getParentDodlesViewGroup().getName();
        String originalPhaseID = actor.getParentViewID();

        if (!(actor instanceof Group)) {
            return childIDs;
        }

        SnapshotArray<Actor> children = ((Group) actor).getChildren();

        for (Actor child : children.begin()) {
            // This is a snapshot of the backing array, so may contain nulls...
            if (child != null) {
                childIDs.add(child.getName());

                removeChildFromGroup((DodlesActor) child, true);
                addChildToGroup(originalParentID, originalPhaseID, (DodlesActor) child);
            }
        }

        children.end();

        return childIDs;
    }
}
