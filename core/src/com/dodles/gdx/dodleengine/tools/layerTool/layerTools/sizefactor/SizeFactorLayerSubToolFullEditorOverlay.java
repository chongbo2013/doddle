package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.sizefactor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.BaseSliderOverlay;

import javax.inject.Inject;

/**
 * Overlay that manages stroke particle size.
 */
public class SizeFactorLayerSubToolFullEditorOverlay extends BaseSliderOverlay {
    private static final String PROPERTY_NAME = "sizefactor";

    @Inject
    public SizeFactorLayerSubToolFullEditorOverlay(AssetProvider assetProvider, ToolRegistry toolRegistry, OkCancelStackManager okCancelStack, ObjectManager objectManager, CommandFactory commandFactory, CommandManager commandManager) {
        super(assetProvider, toolRegistry, okCancelStack, objectManager, commandFactory, commandManager);
    }

    @Override
    protected final Slider getSlider(Skin skin) {
        Slider slider = new Slider(0.01f, 5f, 0.01f, false, skin);
        slider.setValue(0.01f);

        return slider;
    }

    @Override
    protected final float translateValue(float value, boolean dodleToWidget) {
        return value;
    }

    @Override
    protected final String getPropertyName() {
        return PROPERTY_NAME;
    }

    @Override
    protected final void setConfigValue(StrokeConfig config, float value) {
        //config.setSize(Math.round(value));
    }
    
    @Override
    protected final float getConfigValue(StrokeConfig config) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected final Drawable getBackground() {
        return null;
    }
}
