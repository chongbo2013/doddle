package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Common operations that apply to all Actors.
 */
public final class CommonActorOperations {
    private CommonActorOperations() {
    }

    /**
     * Updates origins for the actor and all parents.
     */
    public static void updateAllOrigins(DodlesActor actor) {
        actor.updateOrigin();
        BaseDodlesViewGroup parent = actor.getParentDodlesViewGroup();

        if (parent != null) {
            updateAllOrigins(parent);
        }
    }
    
    /**
     * Sets all parents to the visibility boolean passed in.
     */
    public static void setAllParentVisibility(DodlesActor actor, boolean visible) {
        BaseDodlesViewGroup parent = actor.getParentDodlesViewGroup();
        
        if (!(parent instanceof Scene)) {
            parent.setVisible(visible);
            setAllParentVisibility(parent, visible);
        }
    }

    /**
     * Translates the given local coordinates from the actor's coordinate system to Dodle-space.
     */
    public static Vector2 localToDodleCoordinates(DodlesActor actor, Vector2 localCoordinates) {
        if (((Actor) actor).getParent() == null) {
            return localCoordinates;
        }

        Vector2 stageCoordinates = ((Actor) actor).localToStageCoordinates(localCoordinates.cpy());
        return getRootGroup(actor).stageToLocalCoordinates(stageCoordinates);
    }

    /**
     * Translates the given coordinates in the source actor to the given coordinates in the target actor.
     */
    public static Vector2 localToLocalCoordinates(DodlesActor source, DodlesActor target, Vector2 sourceCoordinates) {
        Vector2 dodleCoordinates = localToDodleCoordinates(source, sourceCoordinates);
        return dodleToLocalCoordinates(target, dodleCoordinates);
    }

    /**
     * Translates the given local rectangle from the actor's coordinate system to Dodle-space.
     */
    public static Rectangle localToDodleRectangle(DodlesActor local, Rectangle localRectangle) {
        Vector2 topLeft = localToDodleCoordinates(local, new Vector2(localRectangle.x, localRectangle.y));
        Vector2 bottomLeft = localToDodleCoordinates(local, new Vector2(localRectangle.x, localRectangle.y + localRectangle.height));
        Vector2 bottomRight = localToDodleCoordinates(local, new Vector2(localRectangle.x + localRectangle.width, localRectangle.y + localRectangle.height));
        Vector2 topRight = localToDodleCoordinates(local, new Vector2(localRectangle.x + localRectangle.width, localRectangle.y));

        float minX = Math.min(topLeft.x, Math.min(bottomLeft.x, Math.min(bottomRight.x, topRight.x)));
        float minY = Math.min(topLeft.y, Math.min(bottomLeft.y, Math.min(bottomRight.y, topRight.y)));
        float maxX = Math.max(topLeft.x, Math.max(bottomLeft.x, Math.max(bottomRight.x, topRight.x)));
        float maxY = Math.max(topLeft.y, Math.max(bottomLeft.y, Math.max(bottomRight.y, topRight.y)));

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Translates the given Dodle-space coordinates to the actor's local coordinate system.
     */
    public static Vector2 dodleToLocalCoordinates(DodlesActor actor, Vector2 dodleCoordinates) {
        if (actor == null || dodleCoordinates == null) {
            return null;
        }
        Vector2 stageCoordinates = getRootGroup(actor).localToStageCoordinates(dodleCoordinates.cpy());
        return ((Actor) actor).stageToLocalCoordinates(stageCoordinates);
    }

    /**
     * Translates the given Dodle-space rectangle to the actor's local coordinate system.
     */
    public static Rectangle dodleToLocalRectangle(DodlesActor local, Rectangle dodleRectangle) {
        Vector2 topLeft = dodleToLocalCoordinates(local, new Vector2(dodleRectangle.x, dodleRectangle.y));
        Vector2 bottomLeft = dodleToLocalCoordinates(local, new Vector2(dodleRectangle.x, dodleRectangle.y + dodleRectangle.height));
        Vector2 bottomRight = dodleToLocalCoordinates(local, new Vector2(dodleRectangle.x + dodleRectangle.width, dodleRectangle.y + dodleRectangle.height));
        Vector2 topRight = dodleToLocalCoordinates(local, new Vector2(dodleRectangle.x + dodleRectangle.width, dodleRectangle.y));

        float minX = Math.min(topLeft.x, Math.min(bottomLeft.x, Math.min(bottomRight.x, topRight.x)));
        float minY = Math.min(topLeft.y, Math.min(bottomLeft.y, Math.min(bottomRight.y, topRight.y)));
        float maxX = Math.max(topLeft.x, Math.max(bottomLeft.x, Math.max(bottomRight.x, topRight.x)));
        float maxY = Math.max(topLeft.y, Math.max(bottomLeft.y, Math.max(bottomRight.y, topRight.y)));

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Returns the root group the actor is a descendant of.
     */
    public static RootGroup getRootGroup(DodlesActor actor) {
        if (actor instanceof RootGroup) {
            return (RootGroup) actor;
        }

        return getRootGroup((DodlesActor) ((Actor) actor).getParent());
    }

    /**
     * Returns the bounds of the actor in dodle coordinates.
     */
    public static Rectangle getDodleBounds(DodlesActor actor) {
        return localToDodleRectangle(actor, actor.getDrawBounds());
    }

    /**
     * Returns the scene the actor is attached to.
     */
    public static Scene getScene(DodlesActor actor) {
        if (actor instanceof Scene) {
            return (Scene) actor;
        }

        return getScene((DodlesActor) ((Actor) actor).getParent());
    }

    /**
     * Returns the closest parent dodlesgroup for the actor.
     */
    public static BaseDodlesViewGroup getParentDodlesViewGroup(DodlesActor actor) {
        Actor curActor = (Actor) actor;

        do {
            curActor = curActor.getParent();
        } while (curActor != null && !(curActor instanceof BaseDodlesViewGroup));

        return (BaseDodlesViewGroup) curActor;
    }

    /**
     * Returns the phase this actor belongs to.
     */
    public static BaseDodlesGroup getParentView(DodlesActor actor) {
        Actor curActor = (Actor) actor;

        do {
            curActor = curActor.getParent();
        } while (curActor != null && !(curActor instanceof DodlesView));

        return (BaseDodlesGroup) curActor;
    }

    /**
     * Returns the bounds of the actor in the target actor's coordinate system.
     */
    public static Rectangle getLocalBounds(DodlesActor actor, DodlesActor target) {
        Rectangle bounds = actor.getDrawBounds();

        if (bounds == null) {
            return new Rectangle();
        }

        RootGroup root = getRootGroup(actor);

        Vector2 topLeft = dodleToLocalCoordinates(target, localToDodleCoordinates(actor, new Vector2(bounds.x, bounds.y)));
        Vector2 bottomLeft = dodleToLocalCoordinates(target, localToDodleCoordinates(actor, new Vector2(bounds.x, bounds.y + bounds.height)));
        Vector2 bottomRight = dodleToLocalCoordinates(target, localToDodleCoordinates(actor, new Vector2(bounds.x + bounds.width, bounds.y + bounds.height)));
        Vector2 topRight = dodleToLocalCoordinates(target, localToDodleCoordinates(actor, new Vector2(bounds.x + bounds.width, bounds.y)));

        float minX = Math.min(topLeft.x, Math.min(bottomLeft.x, Math.min(bottomRight.x, topRight.x)));
        float minY = Math.min(topLeft.y, Math.min(bottomLeft.y, Math.min(bottomRight.y, topRight.y)));
        float maxX = Math.max(topLeft.x, Math.max(bottomLeft.x, Math.max(bottomRight.x, topRight.x)));
        float maxY = Math.max(topLeft.y, Math.max(bottomLeft.y, Math.max(bottomRight.y, topRight.y)));

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Returns the rotation for the actor as seen from Dodle-space.
     */
    public static float getDodleRotation(DodlesActor actor) {
        if (actor instanceof RootGroup) {
            return 0;
        }

        return actor.getRotation() + getDodleRotation((DodlesActor) ((Actor) actor).getParent());
    }

    /**
     * Returns the scale for the actor as seen from Dodle-space.
     */
    public static float getDodleScale(DodlesActor actor) {
        if (actor instanceof RootGroup) {
            return 1;
        }

        return actor.getScaleX() * getDodleScale((DodlesActor) ((Actor) actor).getParent());
    }
}
