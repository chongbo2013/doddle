package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;

/**
 * A "RootGroup" is a group at the root of a scene, traversal shouldn't go higher
 * than a RootGroup.
 */
public class RootGroup extends BaseDodlesGroup {
    public static final String ACTOR_TYPE = "RootGroup";

    /**
     * Defines the IDs of the Root Groups.
     */
    public enum RootGroupId {
        ROOT_GROUP_DODLE,
        ROOT_GROUP_DRAW,
        ROOT_GROUP_CAMERA
    }
    
    private RootGroupId rootGroupId;
    
    public RootGroup(RootGroupId id) {
        super(id.toString(), "N/A");
        
        this.rootGroupId = id;
    }
    
    /**
     * Returns the RootGroupId.
     */
    public final RootGroupId getRootGroupId() {
        return this.rootGroupId;
    }
    
    @Override
    public final String getType() {
        return ACTOR_TYPE;
    }

    @Override
    public final DodlesActor dodleClone(IdDatabase iddb, ObjectManager objectManager) {
        throw new GdxRuntimeException("Root groups shouldn't be cloned.");
    }

    @Override
    protected final float drawAlphaMultiplier() {
        return 1;
    }
}
