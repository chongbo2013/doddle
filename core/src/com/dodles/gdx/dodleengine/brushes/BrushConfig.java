package com.dodles.gdx.dodleengine.brushes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.scenegraph.CustomToolConfig;
import com.dodles.gdx.dodleengine.util.JsonUtility;
import java.util.ArrayList;

/**
 * Brush configuration options.
 */
public class BrushConfig implements CustomToolConfig {
    public static final String CONFIG_TYPE = "BrushConfig";
    
    private ArrayList<Vector2> points;
    private String brush;
    private RulerMode rulerMode;
    
    /**
     * Creates a new brush configuration from the given JSON.
     */
    public static BrushConfig create(JsonValue json) {
        if (json.getString("type").equals(CONFIG_TYPE)) {
            return new BrushConfig(json);
        }
        
        return null;
    }
    
    public BrushConfig() {
    }
    
    private BrushConfig(JsonValue json) {
        points = JsonUtility.readVectorArray(json.get("points"));
        brush = json.getString("brush");
        rulerMode = RulerMode.valueOf(json.getString("rulerMode"));
    }
    
    @Override
    public final String getType() {
        return CONFIG_TYPE;
    }
    
    /**
     * Returns the points that define the brush stroke.
     */
    public final ArrayList<Vector2> getPoints() {
        return points;
    }
    
    /**
     * Sets the points that define the brush stroke.
     */
    public final void setPoints(ArrayList<Vector2> newPoints) {
        points = newPoints;
    }

    /**
     * Returns the brush used to draw the brush stroke.
     */
    public final String getBrush() {
        return brush;
    }
    
    /**
     * Sets the brush used to draw the brush stroke.
     */
    public final void setBrush(String newBrush) {
        brush = newBrush;
    }
    
    /**
     * Returns the ruler mode.
     */
    public final RulerMode getRulerMode() {
        return rulerMode;
    }
    
    /**
     * Sets the ruler mode.
     */
    public final void setRulerMode(RulerMode newRulerMode) {
        rulerMode = newRulerMode;
    }

    /**
     * Creates a copy of the BrushConfig.
     */
    public final BrushConfig cpy() {
        BrushConfig result = new BrushConfig();
        result.points = new ArrayList<Vector2>(points);
        result.brush = brush;
        result.rulerMode = rulerMode;
        return result;
    }

    @Override
    public final void writeConfig(Json json) {
        JsonUtility.writeVectorArray(points, json, "points");
        json.writeValue("type", CONFIG_TYPE);
        json.writeValue("brush", brush);
        json.writeValue("rulerMode", rulerMode.name());
    }
}
