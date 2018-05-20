package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.dodles.gdx.dodleengine.editor.DensityManager;

/**
 * custom button to manage fontscale on the label.
 *
 * @author marknickel
 */
public class ManagerButton extends ImageTextButton {
    private String id;

    public ManagerButton(String id, String text, Skin skin) {
        super(text, skin);
        this.id = id;
        setFontScale();
    }

    public ManagerButton(String id, String text, ImageTextButton.ImageTextButtonStyle style) {
        super(text, style);
        this.id = id;
        setFontScale();
    }

    /**
     * set the font scale based on density.
     */
    private void setFontScale() {
        this.getLabel().setFontScale(DensityManager.getScale() / 1.5f);
    }

}
