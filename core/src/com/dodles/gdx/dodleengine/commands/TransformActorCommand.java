package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Transform;
import javax.inject.Inject;

/**
 * A command that changes the position, scale or rotation of an actor.
 */
public class TransformActorCommand implements Command {
    public static final String COMMAND_NAME = "transformObject";
    
    private final ObjectManager objectManager;
    
    private String targetID;
    private Transform transform;
    private Transform originalTransform;
    private DodlesActor liveTransformTarget;
    
    @Inject
    public TransformActorCommand(ObjectManager om) {
        objectManager = om;
    }
    
    @Override
    public final String getName() {
        return COMMAND_NAME;
    }
    
    /**
     * Starts the transform process during live editing.
     */
    public final void startTransformLive(DodlesActor target) {
        liveTransformTarget = target;
        targetID = target.getName();
        originalTransform = target.getBaseTransform();
    }
    
    /**
     * Completes the live transform process.
     */
    public final void finishTransformLive() {
        liveTransformTarget.updateBaseTransform(new Transform(liveTransformTarget));
        transform = liveTransformTarget.getBaseTransform();
        liveTransformTarget = null;
    }

    @Override
    public final void execute() {
        DodlesActor target = objectManager.getActor(targetID);
        originalTransform = target.getBaseTransform();
        target.updateBaseTransform(transform);
    }

    @Override
    public final void undo() {
        DodlesActor target = objectManager.getActor(targetID);
        target.updateBaseTransform(originalTransform);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("targetID", targetID);
        
        json.writeObjectStart("transform");
        transform.writeConfig(json);
        json.writeObjectEnd();
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        targetID = json.getString("targetID");
        transform = new Transform(json.get("transform"));
    }
}
