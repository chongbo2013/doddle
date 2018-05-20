package com.dodles.gdx.dodleengine.editor.full.dodleoverlay;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import javax.inject.Inject;

/**
 * A dodle overlay that shows the transform (scale/rotate) indicator.
 */
@PerDodleEngine
public class TransformIndicatorDodleOverlay implements FullEditorDodleOverlay {
    public static final String OVERLAY_NAME = "TransformIndicator";

    private final AssetProvider assetProvider;
    private final EngineEventManager eventManager;
    
    @Inject
    public TransformIndicatorDodleOverlay(AssetProvider assetProvider, EngineEventManager eventManager, FullEditorDodleOverlayRegistry fedoRegistry) {
        this.assetProvider = assetProvider;
        this.eventManager = eventManager;
        
        fedoRegistry.registerOverlay(this);
    }
    
    @Override
    public final String getName() {
        return OVERLAY_NAME;
    }

    @Override
    public final void initialize(Stack dodleOverlayStack, Skin skin) {
        final Table overlayTable = new Table();
        overlayTable.setFillParent(true);
        overlayTable.setVisible(false);
        
        Texture rotateTexture =  assetProvider.getTexture(TextureAssets.ROTATE_PNG);
        final TextureRegionDrawable rotate = new TextureRegionDrawable(new TextureRegion(rotateTexture));
        Texture pinchTexture =  assetProvider.getTexture(TextureAssets.ZOOM_PNG);
        final TextureRegionDrawable pinch = new TextureRegionDrawable(new TextureRegion(pinchTexture));

        eventManager.addListener(new EngineEventListener(EngineEventType.DISPLAY_TRANSFORM_OVERLAY) {
            @Override
            public void listen(EngineEventData data) {
                String param = data.getFirstStringParam();
                
                if (param != null) {
                    overlayTable.setVisible(true);
                    
                    if (param.equals("rotate")) {
                        overlayTable.setBackground(rotate);
                    } else if (param.equals("zoom")) {
                        overlayTable.setBackground(pinch);
                    }
                } else {
                    overlayTable.setVisible(false);
                }
            }
        });
        
        dodleOverlayStack.add(overlayTable);
    }
}
