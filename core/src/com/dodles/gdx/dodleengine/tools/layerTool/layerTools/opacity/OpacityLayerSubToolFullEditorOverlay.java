package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.opacity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfigKey;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.BaseSliderOverlay;

import javax.inject.Inject;

/**
 * Overlay that manages opacity.
 */
public class OpacityLayerSubToolFullEditorOverlay extends BaseSliderOverlay {
    private static final String PROPERTY_NAME = StrokeConfigKey.OPACITY.get();
    private final AssetProvider assetProvider;

    @Inject
    public OpacityLayerSubToolFullEditorOverlay(AssetProvider assetProvider, ToolRegistry toolRegistry, OkCancelStackManager okCancelStack, ObjectManager objectManager, CommandFactory commandFactory, CommandManager commandManager) {
        super(assetProvider, toolRegistry, okCancelStack, objectManager, commandFactory, commandManager);

        this.assetProvider = assetProvider;
    }

    @Override
    protected final Slider getSlider(Skin skin) {
        Slider slider = new Slider(1, 100, 1, false, skin);
        slider.setValue(100);

        return slider;
    }
    
    @Override
    protected final float translateValue(float value, boolean dodleToWidget) {
        if (dodleToWidget) {
            return value * 100f;
        } else {
            return value / 100f;
        }
    }

    @Override
    protected final String getPropertyName() {
        return PROPERTY_NAME;
    }

    @Override
    protected final void setConfigValue(StrokeConfig config, float value) {
        config.setOpacity(value);
    }
    
    @Override
    protected final float getConfigValue(StrokeConfig config) {
        return config.getOpacity();
    }

    @Override
    protected final Drawable getBackground() {
        Texture texture = assetProvider.getTexture(TextureAssets.EDITOR_LAYER_TOOL_OPACITY_SLIDER);
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
