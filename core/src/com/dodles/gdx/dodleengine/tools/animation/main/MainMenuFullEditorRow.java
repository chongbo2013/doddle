package com.dodles.gdx.dodleengine.tools.animation.main;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.PlaybackSettings;
import com.dodles.gdx.dodleengine.animation.TimelineInfo;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.dodles.gdx.dodleengine.tools.animation.effect.SelectBlockPanel;
import com.dodles.gdx.dodleengine.tools.animation.list.EffectListPanel;
import com.dodles.gdx.dodleengine.tools.animation.speed.PlaySpeedButton;
import com.dodles.gdx.dodleengine.tools.animation.splice.SpliceLengthFullEditorRow;
import com.dodles.gdx.dodleengine.util.LmlUtility;

import javax.inject.Inject;

/**
 * Editor row that that contains the main animation menu.
 */
public class MainMenuFullEditorRow extends AbstractEditorView {
    private final AnimationManager animationManager;
    private final AnimationTool animationTool;
    private final AssetProvider assetProvider;
    private final DodleStageManager stageManager;
    private final EngineEventManager eventManager;
    private final FullEditorViewState fullViewState;
    private final ObjectManager objectManager;
    private final PlaybackSettings playbackSettings;

    private Table rootTable;
    private Skin skin;
    private TextureAtlas animationIconsAtlas;
    private Button playButton;
    
    @Inject
    public MainMenuFullEditorRow(
            AnimationManager animationManager,
            AnimationTool animationTool,
            AssetProvider assetProvider,
            DodleStageManager stageManager,
            EngineEventManager eventManager,
            FullEditorViewState fullViewState,
            ObjectManager objectManager,
            PlaybackSettings playbackSettings
    ) {
        this.animationManager = animationManager;
        this.animationTool = animationTool;
        this.assetProvider = assetProvider;
        this.eventManager = eventManager;
        this.fullViewState = fullViewState;
        this.stageManager = stageManager;
        this.objectManager = objectManager;
        this.playbackSettings = playbackSettings;
    }
    
    @Override
    public final void activate(Skin newSkin, String newState) {
        skin = newSkin;
        
        if (rootTable == null) {
            rootTable = new Table();
            rootTable.setFillParent(true);
            
            animationIconsAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_ANIMATIONICONS_ATLAS);
            
            playButton = LmlUtility.createButton(animationIconsAtlas, "play", "pause", 0.8f);
            playButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {                    
                    if (playButton.isChecked()) {
                        float time = animationManager.getBlockTime(animationTool.getActiveBlock().getBlockId());
                        TimelineInfo info = new TimelineInfo(animationTool.getScene(), animationTool.getActiveBlock());
                        
                        if (time >= info.getSceneLength()) {
                            time = 0;
                        }
                        
                        animationManager.endAnimation();
                        animationTool.getActiveBlock().resetToBaseTransform();
                        animationManager.startAnimation(
                                animationTool.getScene().getSceneID(),
                                animationTool.getActiveBlock().getBlockId(),
                                time);
                        stageManager.setDisplayMode();
                    } else {
                        animationManager.stopAnimation();
                        stageManager.setDisplayMode(AnimationTool.getDisplayModeForState(fullViewState));
                    }
                }
            });
            rootTable.add(playButton).expand().fillY();
            
            final Button resetButton = LmlUtility.createButton(animationIconsAtlas, "reset", 0.8f);
            resetButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    animationManager.endAnimation();
                    animationTool.getActiveBlock().resetToBaseTransform();
                    playButton.setChecked(false);
                    stageManager.setDisplayMode(AnimationTool.getDisplayModeForState(fullViewState));
                }
            });
            rootTable.add(resetButton).expand().fillY();
            
            final Button playSpeedButton = new PlaySpeedButton(playbackSettings, animationIconsAtlas, skin);
            rootTable.add(playSpeedButton).expand().fillY();
            
            final Button timeSliceButton = LmlUtility.createButton(animationIconsAtlas, "timesplice", 0.8f);
            timeSliceButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, SpliceLengthFullEditorRow.STATE_NAME);
                    stageManager.setDisplayMode(AnimationTool.getDisplayModeForState(fullViewState));
                }
            });
            rootTable.add(timeSliceButton).expand().fillY();
            
            final Button listButton = LmlUtility.createButton(animationIconsAtlas, "list", 0.8f);
            listButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, EffectListPanel.STATE_NAME);
                    stageManager.setDisplayMode(AnimationTool.getDisplayModeForState(fullViewState));
                }
            });
            rootTable.add(listButton).expand().fillY();
            
            final Button blocksButton = LmlUtility.createButton(animationIconsAtlas, "blocks", 0.8f);
            blocksButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    if (blocksButton.isChecked()) {
                        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, SelectBlockPanel.STATE_NAME);
                    } else {
                        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, AnimationTool.TOOL_NAME);
                    }
                    
                    stageManager.setDisplayMode(AnimationTool.getDisplayModeForState(fullViewState));
                }
            });
            rootTable.add(blocksButton).expand().fillY();
            
            eventManager.addListener(new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
                @Override
                public void listen(EngineEventData data) {
                    String state = data.getFirstStringParam();
                    boolean isBaseAnimationState = state.equals(AnimationTool.TOOL_NAME);
                    
                    playButton.setDisabled(!isBaseAnimationState);
                    resetButton.setDisabled(!isBaseAnimationState);
                    playSpeedButton.setDisabled(!isBaseAnimationState);
                    timeSliceButton.setDisabled(!isBaseAnimationState && !state.equals(SpliceLengthFullEditorRow.STATE_NAME));
                    listButton.setDisabled(!isBaseAnimationState && !state.equals(EffectListPanel.STATE_NAME));
                    blocksButton.setDisabled(!isBaseAnimationState && !state.equals(SelectBlockPanel.STATE_NAME));
                }
            });
            
            this.addActor(rootTable);
        }
    }

    @Override
    public final void deactivate() {
    }
    
    @Override
    public final void act(float delta) {
        if (playButton.isChecked() && !animationManager.getBlockStatus(animationTool.getActiveBlock().getBlockId()).isPlaying()) {
            playButton.setChecked(false);
        }
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
}
