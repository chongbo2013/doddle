package com.dodles.gdx.dodleengine.tools.animation.effect;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.EffectType;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlay;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlayRegistry;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import javax.inject.Inject;

/**
 * Panel to select from the main effect classes.
 */
@PerDodleEngine
public class SelectEffectTypePanel extends AbstractEffectPanel implements FullEditorDodleOverlay {
    public static final String PANEL_NAME = "SelectEffect";
    public static final String STATE_NAME = AnimationTool.TOOL_NAME + "." + PANEL_NAME;
    
    private final AssetProvider assetProvider;
    private final EngineEventManager eventManager;
    private final OkCancelStackManager okCancelStack;
    
    private TextureAtlas animationIconsAtlas;
    
    @Inject
    public SelectEffectTypePanel(AssetProvider assetProvider, DodleStageManager stageManager, EngineEventManager eventManager, FullEditorDodleOverlayRegistry fedoRegistry, FullEditorViewState fullViewState, OkCancelStackManager okCancelStack, SelectEffectOverlay seOverlay) {
        super(stageManager, eventManager);
        
        this.assetProvider = assetProvider;
        this.eventManager = eventManager;
        this.okCancelStack = okCancelStack;
        
        fedoRegistry.registerOverlay(this);
        
        for (EffectType effectType : EffectType.values()) {
            fullViewState.registerOverlayView(SelectEffectTypePanel.STATE_NAME + "." + effectType.name(), seOverlay);
        }
    }
    
    @Override
    public final String getName() {
        return PANEL_NAME;
    }

    @Override
    public final void initialize(Stack dodleOverlayStack, Skin skin) {
        Table initPanel = baseInitialize(dodleOverlayStack, skin);
        
        if (initPanel != null) {
            eventManager.addListener(new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
                @Override
                public void listen(EngineEventData data) {
                    String state = data.getFirstStringParam();
                    slidePanel(state != null && state.indexOf(STATE_NAME) == 0);
                }
            });
            
            
            animationIconsAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_ANIMATIONICONS_ATLAS);
            
            for (final EffectType effectType : EffectType.values()) {
                final ImageButton button = LmlUtility.createButton(animationIconsAtlas, effectType.name(), 0.8f);
                final String targetState = STATE_NAME + "." + effectType.name();
                
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, targetState);
                        
                        okCancelStack.push(new Runnable() {
                            @Override
                            public void run() {
                                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, STATE_NAME);
                            }
                        });
                    }
                });
                
                eventManager.addListener(new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
                    @Override
                    public void listen(EngineEventData data) {
                        String state = data.getFirstStringParam();
                        button.setDisabled(!state.equals(STATE_NAME) && state.indexOf(targetState) < 0);
                        slidePanel(state != null && state.indexOf(STATE_NAME) == 0);
                    }
                });
                
                initPanel.add(button).expand().fillX().row();
            }
        }
    }
}
