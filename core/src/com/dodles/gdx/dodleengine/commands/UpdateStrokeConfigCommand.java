package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfigKey;
import com.dodles.gdx.dodleengine.scenegraph.Updatable;
import com.dodles.gdx.dodleengine.util.JsonUtility;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

/**
 * A command that updates the stroke config of Shape actors.
 */
public class UpdateStrokeConfigCommand implements Command {
    public static final String COMMAND_NAME = "updateStrokeConfig";
    private final ObjectManager objectManager;

    private HashMap<String, StrokeConfig> oldStrokeConfigs = new HashMap<String, StrokeConfig>();
    private ArrayList<String> ids;
    private StrokeConfig newStrokeConfig;
    private String property;

    @Inject
    public UpdateStrokeConfigCommand(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }

    /**
     * Init the command.
     */
    public final void init(ArrayList<String> newIds, StrokeConfig newNewStrokeConfig, String newProperty) {
        this.ids = newIds;
        this.newStrokeConfig = newNewStrokeConfig;
        this.property = newProperty;
    }

    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        for (String id : ids) {
            this.updateGraphics(objectManager.getActor(id), newStrokeConfig);
        }
    }

    @Override
    public final void undo() {
        for (String id : ids) {
            this.updateGraphics(objectManager.getActor(id), null);
        }
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("ids", ids);
        json.writeObjectStart("newstrokeConfig");
        newStrokeConfig.writeConfig(json);
        json.writeObjectEnd();
        if (this.property != null && !this.property.equals("")) {
            json.writeValue("property", property);
        }
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        ids = JsonUtility.readStringArray(json.get("ids"));
        newStrokeConfig = new StrokeConfig(json.get("newStrokeConfig"));

        if (json.has("property")) {
            property = json.getString("property");
        } else {
            property = null;
        }
    }

    private void updateGraphics(DodlesActor actor, StrokeConfig replaceStrokeConfig) {
        if (actor instanceof Group) {
            for (Actor child : ((Group) actor).getChildren()) {
                updateGraphics((DodlesActor) child, replaceStrokeConfig);
            }
        } else {
            updateStrokeConfig(actor, replaceStrokeConfig);
        }
    }

    private void updateStrokeConfig(DodlesActor child, StrokeConfig replaceStrokeConfig) {
        StrokeConfig strokeConfig = replaceStrokeConfig;

        if (strokeConfig == null) {
            strokeConfig = this.oldStrokeConfigs.get(child.getName());
        }

        if (child instanceof Updatable) {
            Updatable childShape = (Updatable) child;
            StrokeConfig osc = childShape.getStrokeConfig();

            this.oldStrokeConfigs.put(child.getName(), osc.cpy());

            if (this.property != null && !this.property.equals("")) {
                if (property.equals(StrokeConfigKey.COLOR.get())) {
                    osc.setColor(strokeConfig.getColor());
                } else if (property.equals(StrokeConfigKey.FONT.get())) {
                    osc.setFont(strokeConfig.getFont());
                } else if (property.equals(StrokeConfigKey.SIZE.get())) {
                    osc.setSize(strokeConfig.getSize());
                } else if (property.equals(StrokeConfigKey.OPACITY.get())) {
                    osc.setOpacity(strokeConfig.getOpacity());
                } else if (property.equals(StrokeConfigKey.FILL.get())) {
                    osc.setFill(strokeConfig.getFill());
                }

                childShape.setStrokeConfig(osc);
            } else {
                childShape.setStrokeConfig(strokeConfig);
            }
            
            childShape.regenerate();
            childShape.updateOrigin();
        }
    }
}
