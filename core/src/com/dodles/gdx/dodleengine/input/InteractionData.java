package com.dodles.gdx.dodleengine.input;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.RootGroup;

/**
 * Data that's common between all interaction types.
 */
public class InteractionData {
    private Vector2 globalPoint;
    private Vector2 dodlePoint;
    private int numPointers;
    
    public InteractionData(RootGroup group, float x, float y) {
        this(group, x, y, 1);
    }

    public InteractionData(RootGroup group, float x, float y, int pNumPointers) {
        globalPoint = new Vector2(x, y);
        dodlePoint = group.stageToLocalCoordinates(globalPoint.cpy());
        numPointers = pNumPointers;
    }
    
    /**
     * Gets the point of the interaction in global space.
     */
    public final Vector2 getGlobalPoint() {
        return globalPoint;
    }
    
    /**
     * Gets the point of the interaction in dodle space.
     */
    public final Vector2 getDodlePoint() {
        return dodlePoint;
    }

    /**
     * Gets the number of pointers.
     */
    public final int getNumPointers() {
        return numPointers;
    }
}
