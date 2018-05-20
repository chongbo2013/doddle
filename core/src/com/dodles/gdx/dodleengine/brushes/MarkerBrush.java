package com.dodles.gdx.dodleengine.brushes;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.scenegraph.GraphicsGenerator;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.direct.DirectTextureGraphics;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * "Marker" brush.
 */
@PerDodleEngine
public class MarkerBrush extends AbstractBrush {
    public static final String BRUSH_NAME = "marker";
    public static final int STAMP_SCALE = 4;
    private EditorState editorState;
    
    private HashMap<Float, Texture> particleCache = new HashMap<Float, Texture>();

    @Inject
    public MarkerBrush(AssetProvider assetProvider, BrushRegistry brushRegistry, CommandFactory commandFactory, EditorState editorState) {
        super(assetProvider, commandFactory);
        this.editorState = editorState;
        brushRegistry.registerBrush(this);
    }

    @Override
    public final String getName() {
        return BRUSH_NAME;
    }
    
    @Override
    public final int getOrder() {
        return 5;
    }

    @Override
    public final TextureRegion getActiveIcon() {
        return new TextureRegion(getBrushIconsTexture(), 838, 2, 101, 102);
    }

    @Override
    public final TextureRegion getInactiveIcon() {
        return new TextureRegion(getBrushIconsTexture(), 4, 121, 102, 102);
    }

    @Override
    protected final void onMouseMove(Shape shape) {
        final ArrayList<Vector2> points = getNewSmoothedPoints();
        
        if (points.size() > 0) {
            shape.addGenerator(new GraphicsGenerator() {
                @Override
                public List<Graphics> generateGraphics(Shape shape) {
                    ArrayList<Graphics> result = new ArrayList<Graphics>();
                    StrokeConfig strokeConfig = shape.getStrokeConfig();
                    float strokeSize = strokeConfig.getSize();
        
                    for (Vector2 point : points) {
                        Texture sprite = generateSprite(strokeConfig);
                        result.add(new DirectTextureGraphics(sprite, 0f, 0f, 1, strokeSize * STAMP_SCALE, point.x - 0.5f, point.y - strokeSize / 2f, 1, strokeSize, 0, strokeConfig.getColor()));
                    }
                    
                    return result;
                }
            });
        }
    }

    @Override
    protected final StrokeConfig getDefaultStrokeConfig() {
        StrokeConfig dsg = new StrokeConfig();
        dsg.setColor(editorState.getStrokeConfig().getColor());
        dsg.setSize(editorState.getStrokeConfig().getSize());
        dsg.setOpacity(editorState.getStrokeConfig().getOpacity());
        return dsg;
    }

    @Override
    protected final boolean keepDuplicatePoints() {
        return false;
    }
    
    private Texture generateSprite(StrokeConfig strokeConfig) {
        float strokeSize = strokeConfig.getSize();
        float stampSize = strokeSize * STAMP_SCALE;
        
        if (!particleCache.containsKey(strokeSize)) {
            Pixmap cache = new Pixmap(1, Math.round(stampSize), Format.RGBA8888);
            cache.setColor(1, 1, 1, 1);
            cache.fillRectangle(0, 0, 1, Math.round(stampSize) / 2);
            
            float alphaAdj = 1f / (float) (stampSize / 2);
            
            for (int i = 0; i < stampSize / 2; i++) {
                cache.setColor(1, 1, 1, 1f - alphaAdj * i);
                cache.fillRectangle(0, (Math.round(stampSize) / 2) + i, 1, 1);
            }
            
            particleCache.put(strokeSize, new Texture(cache));
            cache.dispose();
        }
        
        return particleCache.get(strokeSize);
    }
}
