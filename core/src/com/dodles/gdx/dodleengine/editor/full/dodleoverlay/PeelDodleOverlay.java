package com.dodles.gdx.dodleengine.editor.full.dodleoverlay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.editor.PeelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import javax.inject.Inject;

/**
 * A dodle overlay that shows the peel icon.
 */
@PerDodleEngine
public class PeelDodleOverlay implements FullEditorDodleOverlay {
    public static final String OVERLAY_NAME = "Peel";
    
    private final AssetProvider assetProvider;
    private final EngineEventManager eventManager;
    private final PeelStackManager peelStackManager;
    
    @Inject
    public PeelDodleOverlay(AssetProvider assetProvider, EngineEventManager eventManager, FullEditorDodleOverlayRegistry fedoRegistry, PeelStackManager peelStackManager) {
        this.assetProvider = assetProvider;
        this.eventManager = eventManager;
        this.peelStackManager = peelStackManager;
        
        fedoRegistry.registerOverlay(this);
    }
    
    @Override
    public final String getName() {
        return OVERLAY_NAME;
    }

    @Override
    public final void initialize(Stack dodleOverlayStack, Skin skin) {
        final Table unpeelHost = new Table();
        unpeelHost.setFillParent(true);
        unpeelHost.setVisible(false);
        
        Texture texture = assetProvider.getTexture(TextureAssets.PEEL_PNG);
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        
        ImageButton unpeelButton = new ImageButton(drawable.tint(Color.GREEN));
        unpeelButton.getImageCell().size(FullEditorInterface.getInterfaceRowSize() * 2);

        unpeelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                peelStackManager.pop();
            }
        });

        eventManager.addListener(new EngineEventListener(EngineEventType.PEEL_STACK_CHANGED) {
            @Override
            public void listen(EngineEventData data) {
                int size = peelStackManager.size();                
                unpeelHost.setVisible(size > 0);
            }
        });
        
        unpeelHost.add(unpeelButton).expand().align(Align.topLeft);
        dodleOverlayStack.add(unpeelHost);
    }
}
