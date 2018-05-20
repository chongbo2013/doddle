package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.scenegraph.CustomToolConfig;
import com.dodles.gdx.dodleengine.scenegraph.GraphicsGenerator;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg.NanoVgPathGraphics;
import com.gurella.engine.graphics.vector.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Configures path segments for drawing to a shape.
 */
public class PathConfig implements CustomToolConfig {
    public static final String CONFIG_TYPE = "PathConfig";
    
    private List<Path.PathData> pathData;
    
    public PathConfig(List<Path.PathData> pathData) {
        this.pathData = pathData;
    }
    
    private PathConfig(JsonValue json) {
        JsonValue pathDataJson = json.get("pathData");
        pathData = new ArrayList<Path.PathData>();
        
        for (int i = 0; i < pathDataJson.size; i++) {
            pathData.add(new Path.PathData(pathDataJson.get(i)));
        }
    }
    
    /**
     * Creates a new brush configuration from the given JSON.
     */
    public static PathConfig create(JsonValue json) {
        if (json.getString("type").equals(CONFIG_TYPE)) {
            return new PathConfig(json);
        }
        
        return null;
    }
    
    /**
     * Returns the path commands.
     */
    public final List<Path.PathData> getPathData() {
        return pathData;
    }

    @Override
    public final CustomToolConfig cpy() {
        return new PathConfig(new ArrayList<Path.PathData>(pathData));
    }

    @Override
    public final String getType() {
        return CONFIG_TYPE;
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("type", CONFIG_TYPE);
        json.writeArrayStart("pathData");
        
        for (Path.PathData curPathData : pathData) {
            json.writeObjectStart();
            curPathData.writeConfig(json);
            json.writeObjectEnd();
        }
        
        json.writeArrayEnd();
    }
    
    /**
     * Initializes the shape with a generator that draws the path.
     */
    public static void init(Shape shape) {
        if (shape.getCustomConfig() instanceof PathConfig) {
            final PathConfig config = (PathConfig) shape.getCustomConfig();

            shape.addGenerator(new GraphicsGenerator() {
                @Override
                public List<Graphics> generateGraphics(Shape newShape) {                
                    ArrayList<Graphics> result = new ArrayList<Graphics>();

                    StrokeConfig sc = newShape.getStrokeConfig();
                    result.add(new NanoVgPathGraphics(config.getPathData(), sc.getSize(), sc.getColor(), sc.getFill(), false));

                    return result;
                }
            });
        }
    }
}
