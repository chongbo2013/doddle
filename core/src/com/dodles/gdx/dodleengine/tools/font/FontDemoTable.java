package com.dodles.gdx.dodleengine.tools.font;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.BitmapFontAssets;
import com.dodles.gdx.dodleengine.assets.FontRenderer;
import com.dodles.gdx.dodleengine.editor.DensityManager;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;

/**
 * Table that demos a font for selection.
 */
public class FontDemoTable extends Table {
    private final AssetProvider assetProvider;
    
    private Label fontName;
    private Label exampleText;
    
    public FontDemoTable(AssetProvider assetProvider, BitmapFontAssets bmpFont, Color color) {
        super();
        
        this.assetProvider = assetProvider;
        Label.LabelStyle style = new Label.LabelStyle(assetProvider.getFont(bmpFont), color);

        fontName = new Label("", style);
        fontName.setFontScale(FontRenderer.calculateFontScale(30) * DensityManager.getScale());

        exampleText = new Label("", style);
        exampleText.setAlignment(Align.right);
        exampleText.setFontScale(FontRenderer.calculateFontScale(30) * DensityManager.getScale());
        
        setFontStyle(bmpFont, color);

        this.pad(5);
        this.add(fontName).expand().fill();
        this.add(exampleText).expand().fill();
    }
    
    /**
     * Sets the color of the text.
     */
    public final void setFontStyle(StrokeConfig config) {
        setFontStyle(config.getFont(), config.getColor().cpy());
    }
    
    private void setFontStyle(BitmapFontAssets bmpFont, Color color) {
        Label.LabelStyle style = fontName.getStyle();
        style.fontColor = color;
        style.font = assetProvider.getFont(bmpFont);
        
        fontName.setStyle(style);
        fontName.setText(bmpFont.getDisplayName());
        
        exampleText.setStyle(style);
        exampleText.setText("ABC abc 123");
    }
}
