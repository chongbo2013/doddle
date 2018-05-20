package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.util.JsonUtility;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * A command that locks the given list of objects.
 */
public class LockCommand implements Command {
    public static final String COMMAND_NAME = "lock";

    private final ObjectManager objectManager;

    private ArrayList<String> ids;
    private boolean locking;

    @Inject
    public LockCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }

    /**
     * Init the command.
     */
    public final void init(ArrayList<String> incomingIDs, boolean vertical) {
        this.ids = incomingIDs;
        this.locking = vertical;
    }

    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        lockOrUnlock(locking);
    }

    @Override
    public final void undo() {
        lockOrUnlock(!locking);
    }

    private void lockOrUnlock(boolean isLocking) {
        if (isLocking) {
            objectManager.lockObjects(this.ids);
        } else {
            objectManager.unlockObjects(this.ids);
        }
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("ids", ids);
        json.writeValue("locking", locking);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        ids = JsonUtility.readStringArray(json.get("ids"));
        locking = json.getBoolean("locking");
    }
}
