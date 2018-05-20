package com.dodles.gdx.dodleengine.tools.animation.speed;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.animation.PlaybackSettings;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;

/**
 * Button that shows the play speed overlay.
 */
public class PlaySpeedButton extends ImageButton {
    private final PlaybackSettings playbackSettings;
    private final Skin skin;
    
    private ShapeRenderer shapeRenderer;
    private Slider slider;
    private Table sliderTable;
    
    public PlaySpeedButton(PlaybackSettings playbackSettings, TextureAtlas atlas, Skin skin) {
        super(new TextureRegionDrawable(atlas.findRegion("playspeed")));
        
        this.playbackSettings = playbackSettings;
        this.skin = skin;
        
        ImageButton.ImageButtonStyle style = getStyle();
        style.imageDisabled = ((TextureRegionDrawable) style.imageUp).tint(new Color(0, 0, 0, 0.25f));
        setStyle(style);
        getImageCell().size(FullEditorInterface.getInterfaceRowSize() * 0.8f);
        
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleSlider();
            }
        });
    }
    
    private void toggleSlider() {
        Stage stage = getStage();
        
        if (sliderTable == null) {
            Vector2 stageCoords = getParent().localToStageCoordinates(new Vector2(getX(), getY()));
            
            sliderTable = new Table(skin);
            sliderTable.setBackground(FullEditorViewState.TOOLBAR_MIDDLE_ACTIVATED_COLOR);
            sliderTable.setHeight(FullEditorInterface.getInterfaceRowSize() * 4);
            sliderTable.setWidth(FullEditorInterface.getInterfaceRowSize());
            sliderTable.setX(stageCoords.x);
            sliderTable.setY(stageCoords.y + getHeight());
            
            slider = new Slider(0, 4, 1, true, skin);
            slider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    playbackSettings.setPlaySpeed(sliderToPlaySpeed(slider.getValue()));
                }
            });
            
            sliderTable.add(slider).expand().fill();
            
            stage.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Vector2 stageVector = new Vector2(x, y);
                    Vector2 tableCoords = sliderTable.stageToLocalCoordinates(stageVector.cpy());
                    Vector2 thisCoords = stageToLocalCoordinates(stageVector);
                    
                    if (sliderTable.hit(tableCoords.x, tableCoords.y, false) == null && hit(thisCoords.x, thisCoords.y, false) == null) {
                        sliderTable.remove();
                    }
                    return false;
                }
            });
        }
        
        if (stage.getActors().contains(sliderTable, true)) {
            sliderTable.remove();
        } else {
            stage.addActor(sliderTable);
        }
        
        slider.setValue(playSpeedToSlider(playbackSettings.getPlaySpeed()));
    }
    
    private float playSpeedToSlider(float playSpeed) {
        return (float) (Math.log(playSpeed * 4) / Math.log(2));
    }
    
    private float sliderToPlaySpeed(float sliderValue) {
        return 0.25f * (float) Math.pow(2f, sliderValue);
    }
}
