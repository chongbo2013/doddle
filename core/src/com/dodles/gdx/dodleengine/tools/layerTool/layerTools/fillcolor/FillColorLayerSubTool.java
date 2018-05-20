package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.fillcolor;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.editor.full.strokeconfigrow.ColorSelectorMode;
import com.dodles.gdx.dodleengine.editor.full.strokeconfigrow.ColorSelectorOverlay;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfigKey;
import com.dodles.gdx.dodleengine.tools.layerTool.AbstractLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * The "Fill Color" layer tool updates the fill color of the selected objects.
 */
@PerDodleEngine
public class FillColorLayerSubTool extends AbstractLayerSubTool implements LayerSubTool {
    public static final String TOOL_NAME = LayerTool.TOOL_NAME + ".Fill_Color";
    public static final String ACTIVATED_COLOR = "tray-background";
    private final ObjectManager objectManager;

    @Inject
    public FillColorLayerSubTool(AssetProvider assetProvider, FullEditorViewState fullViewState, LayerSubToolRegistry layerSubToolRegistry, ObjectManager objectManager, ColorSelectorOverlay overlay) {
        super(assetProvider);
        this.objectManager = objectManager;

        layerSubToolRegistry.registerTool(this);
        overlay.setMode(ColorSelectorMode.SELECTED);
        overlay.setProperty(StrokeConfigKey.FILL);
        fullViewState.registerOverlayView(TOOL_NAME, overlay);
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
        return 3;
    }

    @Override
    public final int getOrder() {
        return 1;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 2, 98, 44, 44);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        //return getToolBarIconsTextureAtlas().findRegion("save",i);
        return null;
    }

    @Override
    public final String getButtonStyleName() {
        return null;
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return Collections.emptyList();
    }
    
    @Override
    public void onActivation() {
    }

    @Override
    public void onDeactivation() {
    }
}
