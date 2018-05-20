package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Transform;

import javax.inject.Inject;

/**
 * A command that flips an object.
 */
public class FlipCommand implements Command {
    public static final String COMMAND_NAME = "flip";

    private final ObjectManager objectManager;

    private String id;
    private boolean isVertical;

    @Inject
    public FlipCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }

    /**
     * Init the command.
     */
    public final void init(String incomingID, boolean vertical) {
        this.id = incomingID;
        this.isVertical = vertical;
    }

    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        flip();
    }

    private void flip() {
        DodlesActor group = objectManager.getActor(this.id);
        Transform newTransform = group.getBaseTransform().cpy();

        if (!this.isVertical) {
            newTransform.setScaleY(newTransform.getScaleY() * -1);
        } else {
            newTransform.setScaleX(newTransform.getScaleX() * -1);
        }

        group.updateBaseTransform(newTransform);
    }

    @Override
    public final void undo() {
        flip();
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("id", id);
        json.writeValue("isVertical", isVertical);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        id = json.getString("id");
        isVertical = json.getBoolean("isVertical");
    }
}
