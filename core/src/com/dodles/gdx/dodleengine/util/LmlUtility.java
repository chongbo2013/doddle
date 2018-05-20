package com.dodles.gdx.dodleengine.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;

/**
 * Helper functions for working with LML.
 */
public final class LmlUtility {
    private LmlUtility() {
    }
    
    /**
     * Returns the table cell containing the actor with the given LML ID.
     */
    public static Cell getCell(WidgetGroup rootWidget, String lmlId) {
        return getCell(rootWidget.findActor(lmlId));
    }
    
    /**
     * Returns the table cell containing the actor.
     */
    public static Cell getCell(Actor actor) {
        Table parentTable = (Table) actor.getParent();
        return parentTable.getCell(actor);
    }
    
    /**
     * Sets the percent width of the actor with the given LML ID relative to it's parent.
     */
    public static void setPctWidthRelativeToParent(WidgetGroup rootWidget, String lmlId, float pctWidth) {
        Actor actor = rootWidget.findActor(lmlId);
        setPctWidthRelativeToParent(actor, pctWidth);
    }
    
    /**
     * Sets the percent width of the actor relative to it's parent.
     */
    public static void setPctWidthRelativeToParent(Actor actor, float pctWidth) {
        getCell(actor).width(Value.percentWidth(pctWidth, actor.getParent()));
    }
    
    /**
     * Sets the percent height of the actor with the given LML ID relative to it's parent.
     */
    public static void setPctHeightRelativeToParent(WidgetGroup rootWidget, String lmlId, float pctHeight) {
        Actor actor = rootWidget.findActor(lmlId);
        setPctHeightRelativeToParent(actor, pctHeight);
    }
    
    /**
     * Sets the percent height of the actor relative to it's parent.
     */
    public static void setPctHeightRelativeToParent(Actor actor, float pctHeight) {
        getCell(actor).height(Value.percentHeight(pctHeight, actor.getParent()));
    }
    
    /**
     * Creates a button with an appropriately stretched texture.
     */
    public static ImageButton createButton(TextureAtlas atlas, String buttonName, float rowSizePct) {
        return createButton(atlas, buttonName, null, rowSizePct);
    }
    
    /**
     * Creates a button with an appropriately stretched normal and checked texture.
     */
    public static ImageButton createButton(TextureAtlas atlas, String buttonName, String checkedName, float rowSizePct) {
        AtlasRegion buttonRegion = atlas.findRegion(buttonName);
        
        if (buttonRegion == null) {
            return null;
        }
        
        AtlasRegion checkedRegion = null;
        
        if (checkedName != null) {
            checkedRegion = atlas.findRegion(checkedName);
        }
        
        return createButton(buttonRegion, checkedRegion, rowSizePct);
    }
    
    /**
     * Creates a button with an appropriately stretched normal and checked texture.
     */
    public static ImageButton createButton(AtlasRegion buttonImage, AtlasRegion checkedImage, float rowSizePct) {
        if (buttonImage == null) {
            return null;
        }
        
        TextureRegionDrawable region = new TextureRegionDrawable(buttonImage);
        TextureRegionDrawable checkedRegion = region;
        
        if (checkedImage != null) {
            checkedRegion = new TextureRegionDrawable(checkedImage);
        }
        
        ImageButton button = new ImageButton(region, region, checkedRegion);
        ImageButton.ImageButtonStyle style = button.getStyle();
        style.imageDisabled = region.tint(new Color(0, 0, 0, 0.25f));
        button.setStyle(style);
        button.getImageCell().size(FullEditorInterface.getInterfaceRowSize() * rowSizePct);
        return button;
    }

    /**
     * Create an image text button.
     */
    public static Table createImageTextButton(TextureAtlas atlas, Skin skin, String buttonName, String checkedName, String labelText, String labelStyle, float rowSizePct) {
        TextureRegionDrawable region = new TextureRegionDrawable(atlas.findRegion(buttonName));
        TextureRegionDrawable checkedRegion = region;

        if (checkedName != null) {
            checkedRegion = new TextureRegionDrawable(atlas.findRegion(checkedName));
        }

        ImageButton button = new ImageButton(region, region, checkedRegion);
        ImageButton.ImageButtonStyle style = button.getStyle();
        style.imageDisabled = region.tint(new Color(1, 1, 1, 0.35f));
        button.setStyle(style);

        //float padding = FullEditorInterface.getInterfaiceRowSize() / 8f;
        //float iconSize = FullEditorInterface.getInterfaceRowSize() - padding * 2;

        float regionSize = FullEditorInterface.getInterfaceRowSize() * rowSizePct;

        Table cell = new Table(skin);
        cell.add(button).size(regionSize).expand().fill().top().center().row();
        cell.add(new Label(labelText, skin, labelStyle)).bottom().center();

        return cell;
    }
    
    /**
     * Configures a monospaced slider label.
     */
    public static void configureSliderLabel(final Label label, final Slider slider, final int maxLength, final float value, final NumberFormatter numberFormatter) {
        label.getStyle().font.setFixedWidthGlyphs(" 0123456789.");

        ChangeListener sliderChanged = new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                String valueText = numberFormatter.decimalFormat("#.#", slider.getValue());

                while (valueText.length() < maxLength) {
                    valueText = " " + valueText;
                }

                label.setText(valueText);
            }
        };
        slider.addListener(sliderChanged);
        slider.setValue(value);
        sliderChanged.changed(null, null);
    }
}
