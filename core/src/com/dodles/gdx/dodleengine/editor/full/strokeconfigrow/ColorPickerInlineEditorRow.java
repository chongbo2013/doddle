package com.dodles.gdx.dodleengine.editor.full.strokeconfigrow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.editor.inline.InlineEditorInterface;
import java.util.HashMap;
import javax.inject.Inject;

/**
 * Single row color picker for predefined colors.
 */
@PerDodleEngine
public class ColorPickerInlineEditorRow extends AbstractEditorView {
    private final HashMap<String, Button> buttonColorMap = new HashMap<String, Button>();
    private final AssetProvider assetProvider;
    private final EditorState editorState;
    
    private ScrollPane scrollPane;

    @Inject
    public ColorPickerInlineEditorRow(AssetProvider assetProvider, EditorState editorState) {
        this.assetProvider = assetProvider;
        this.editorState = editorState;
    }
    
    @Override
    public final void activate(Skin skin, String newState) {
        if (scrollPane == null) {
            Table colors = new Table();
            ButtonGroup group = new ButtonGroup();
            
            for (Color color : ColorSelectorOverlay.DEFAULT_COLORS) {
                createColorButton(color, colors, group);
            }
            
            scrollPane = new ScrollPane(colors);
            scrollPane.setFillParent(true);
            this.addActor(scrollPane);
        }
        
        Color curColor = editorState.getStrokeConfig().getColor();
        
        Button currentColorButton = buttonColorMap.get(new Color(curColor.r, curColor.g, curColor.b, 1).toString());
        
        if (currentColorButton != null) {
            currentColorButton.setChecked(true);
        }
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return scrollPane;
    }
    
    private void createColorButton(final Color color, Table table, ButtonGroup group) {
        Texture texture = assetProvider.getTexture(TextureAssets.EDITOR_WHITE_COLOR_ROUND_BUTTON);
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        Button button = new Button(drawable.tint(color));
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                editorState.getStrokeConfig().setColor(color);
            }
        });
        
        buttonColorMap.put(color.toString(), button);
        
        Texture checkedTexture = assetProvider.getTexture(TextureAssets.EDITOR_WHITE_COLOR_ROUND_BUTTON_SELECTED);
        TextureRegionDrawable checkedDrawable = new TextureRegionDrawable(new TextureRegion(checkedTexture));
        button.getStyle().checked = checkedDrawable.tint(color);
        
        table.add(button).size(InlineEditorInterface.getInterfaceRowSize(), InlineEditorInterface.getInterfaceRowSize()).expandX();
        group.add(button);
    }
    
}
