package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.strokesize;

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
 * Overlay that manages stroke size.
 */
public class StrokeSizeLayerSubToolFullEditorOverlay extends BaseSliderOverlay {
    private static final String PROPERTY_NAME = StrokeConfigKey.SIZE.get();
    private final AssetProvider assetProvider;

    @Inject
    public StrokeSizeLayerSubToolFullEditorOverlay(AssetProvider assetProvider, ToolRegistry toolRegistry, OkCancelStackManager okCancelStack, ObjectManager objectManager, CommandFactory commandFactory, CommandManager commandManager) {
        super(assetProvider, toolRegistry, okCancelStack, objectManager, commandFactory, commandManager);

        this.assetProvider = assetProvider;
    }

    @Override
    protected final Slider getSlider(Skin skin) {
        Slider slider = new Slider(1, 50, 1, false, skin);
        slider.setValue(1);

        return slider;
    }

    @Override
    protected final float translateValue(float value, boolean dodleToWidget) {
        if (value <= 25) {
            return value;
        } else if (dodleToWidget) {
            return 25 + (value - 25) / 3;
        } else {
            return 25 + (value - 25) * 3;
        }
    }

    @Override
    protected final String getPropertyName() {
        return PROPERTY_NAME;
    }

    @Override
    protected final void setConfigValue(StrokeConfig config, float value) {
        config.setSize(Math.round(value));
    }
    
    @Override
    protected final float getConfigValue(StrokeConfig config) {
        return config.getSize();
    }

    @Override
    protected final Drawable getBackground() {
        Texture texture = assetProvider.getTexture(TextureAssets.EDITOR_LAYER_TOOL_SIZE_SLIDER);
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
