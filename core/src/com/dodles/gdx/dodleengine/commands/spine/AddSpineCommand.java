package com.dodles.gdx.dodleengine.commands.spine;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.scenegraph.Spine;
import com.dodles.gdx.dodleengine.scenegraph.Transform;

import javax.inject.Inject;

/**
 * A command that adds a spine to a phase.
 */
public class AddSpineCommand implements Command {
    public static final String COMMAND_NAME = "addSpine";

    private final ObjectManager objectManager;
    private final AssetProvider assetProvider;

    private String id;
    private String phaseID;
    private String spineSkeletonJson;

    @Inject
    public AddSpineCommand(ObjectManager objectManager, AssetProvider assetProvider) {
        this.assetProvider = assetProvider;
        this.objectManager = objectManager;
    }

    /**
     * Initializes the command.
     */
    public final void init(String pID, String pPhaseID, String pSpineSkeletonJson) {
        id = pID;
        phaseID = pPhaseID;
        spineSkeletonJson = pSpineSkeletonJson;
    }

    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        Spine spine = new Spine(assetProvider, id, objectManager.getTrackingID(), spineSkeletonJson);
        Phase phase = (Phase) objectManager.getActor(phaseID);

        Rectangle phaseBounds = phase.getDrawBounds();
        Rectangle spineBounds = spine.getDrawBounds();
        Transform spineTransform = new Transform();
        spineTransform.setX(phaseBounds.x + ((phaseBounds.width - spineBounds.width) / 2));
        spineTransform.setY(phaseBounds.y + ((phaseBounds.height - spineBounds.height) / 2));
        spine.updateBaseTransform(spineTransform);

        phase.setSpine(spine);
        phase.setDisplayMode(Phase.DisplayMode.SPINE_FINAL);
        objectManager.addActor(spine);
    }

    @Override
    public final void undo() {
        objectManager.removeActor(id);
        Phase phase = (Phase) objectManager.getActor(phaseID);
        phase.setDisplayMode(Phase.DisplayMode.SOURCE);
        phase.setSpine(null);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("id", id);
        json.writeValue("phaseID", phaseID);
        json.writeValue("spineSkeletonJson", spineSkeletonJson);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        id = json.getString("id");
        phaseID = json.getString("phaseID");
        spineSkeletonJson = json.getString("spineSkeletonJson");
    }
}
