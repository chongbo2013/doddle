/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dodles.gdx.dodleengine.tools.font;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.dodles.gdx.dodleengine.assets.FontRenderer;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;

/**
 *
 * @author mike.rosack
 */
public class DodlesTextField  extends TextField {
    private float fontSize;

    public DodlesTextField(String text, Skin skin, BitmapFont font, StrokeConfig strokeConfig) {
        super(text, skin, "dodles-editor");
        
        this.fontSize = strokeConfig.getSize();
        
        TextFieldStyle style = getStyle();
        style.font = font;
        style.font.getData().setScale(FontRenderer.calculateFontScale(fontSize));
        style.fontColor = strokeConfig.getColor().cpy();
        style.disabledFontColor = style.fontColor;
        style.messageFontColor = style.fontColor;
        setStyle(style);
        setText(text);
    }

    /**
     * Sets the font size of the text field.
     */
    public final void setFontSize(int newFontSize) {
        fontSize = newFontSize;

        // Reset the style to recalculate the font size...
        setStyle(getStyle());
    }

    @Override
    public final void draw(Batch batch, float parentAlpha) {
        getStyle().font.getData().setScale(FontRenderer.calculateFontScale(fontSize));
        super.draw(batch, parentAlpha);
    }

    @Override
    public final void setStyle(TextField.TextFieldStyle style) {
        super.setStyle(style);

        if (text != null) {
            setPasswordMode(false);  // We need to call this to get updateDisplayText to run :(
        }
    }

    @Override
    public final float getPrefWidth() {
        return 2000;
    }
}
