package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.direct.DirectTextGraphics;
import java.util.ArrayList;
import java.util.List;

/**
 * An actor that renders text to the canvas.
 */
public class TextShape extends Shape implements DodlesActor, Updatable {
    public static final String ACTOR_TYPE = "TextShape";
    
    private final AssetProvider assetProvider;
    private String text;
    
    public TextShape(String id, String trackingID, FrameBufferAtlasManager fbam, StrokeConfig strokeConfig, AssetProvider assetProvider) {
        super(id, trackingID, fbam, strokeConfig);
        
        this.assetProvider = assetProvider;
        setName(id);
        setTrackingID(trackingID);
        setStrokeConfig(strokeConfig);
    }
    
    public TextShape(JsonValue json, IdDatabase idDB, DodlesActorFactory actorFactory, FrameBufferAtlasManager fbam, AssetProvider assetProvider) {
        super(json, idDB, actorFactory, fbam);
        
        this.assetProvider = assetProvider;
        ActorMixins.importFromJson(this, idDB, json);
        setStrokeConfig(new StrokeConfig(json.get("strokeConfig")));
        setText(json.getString("text"));
    }
    
    @Override
    public final String getType() {
        return ACTOR_TYPE;
    }

    /**
     * Returns the text that will be rendered. 
     */
    public final String getText() {
        return text;
    }
    
    /**
     * Sets the text to be rendered. 
     */
    public final void setText(String newText) {
        text = newText;
        
        clearGenerators();
        
        addGenerator(new GraphicsGenerator() {
            @Override
            public List<Graphics> generateGraphics(Shape shape) {
                ArrayList<Graphics> result = new ArrayList<Graphics>();
                
                StrokeConfig sc = shape.getStrokeConfig();
                result.add(new DirectTextGraphics(assetProvider.getFont(sc.getFont()), text, sc.getSize(), sc.getColor()));

                return result;
            }
        });
    }
    
    @Override
    public final Shape dodleClone(IdDatabase idDB, ObjectManager objectManager) {
        TextShape clone = new TextShape(idDB.getNewID(getName()), getTrackingID(), getAtlasManager(), getStrokeConfig().cpy(), this.assetProvider);
        ActorMixins.commonClone(this, objectManager, clone);
        clone.setText(this.getText());
        return clone;
    }
    
    @Override
    protected final void extendConfig(Json json) {
        json.writeValue("text", text);
    }
}
