package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.chest;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.tools.ClickableTool;
import com.dodles.gdx.dodleengine.tools.layerTool.AbstractLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The "Chest" layer tool puts the selected object in the chest.
 */
@PerDodleEngine
public class ChestLayerToolSubTool extends AbstractLayerSubTool implements LayerSubTool, ClickableTool {
    public static final String TOOL_NAME = LayerTool.TOOL_NAME + ".To_Chest";
    public static final String ACTIVATED_COLOR = "tray-background";
    
    private final AddToChestModal addToChestModal;
    private final ObjectManager objectManager;

    @Inject
    public ChestLayerToolSubTool(AddToChestModal addToChestModal, AssetProvider assetProvider, LayerSubToolRegistry layerSubToolRegistry, ObjectManager objectManager) {
        super(assetProvider);
        
        this.addToChestModal = addToChestModal;
        this.objectManager = objectManager;

        layerSubToolRegistry.registerTool(this);
    }
    
    @Override
    public final String getName() {
        return TOOL_NAME;
    }

    @Override
    public final String getActivatedColor() {
        return ACTIVATED_COLOR;
    };

    @Override
    public final int getRow() {
        return 1;
    }

    @Override
    public final int getOrder() {
        return 4;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 336, 50, 44, 44);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        return getToolBarIconsTextureAtlas().findRegion("chest_" + i);
    }

    @Override
    public final String getButtonStyleName() {
        return "chest";
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return new ArrayList<InputHandler>();
    }
    
    @Override
    public void onActivation() {
    }

    @Override
    public void onDeactivation() {
    }

    @Override
    public final ClickListener onClick() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addToChestModal.open();
            }
        };
    }
}
