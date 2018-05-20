package com.dodles.gdx.dodleengine.tools.animation.effect;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.EffectDefinition;
import com.dodles.gdx.dodleengine.animation.EffectType;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlay;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlayRegistry;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.dodles.gdx.dodleengine.tools.animation.EffectIconResolver;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import java.util.Map;
import javax.inject.Inject;

/**
 * Quick add effect panel.
 */
@PerDodleEngine
public class QuickAddEffectPanel extends AbstractEffectPanel implements FullEditorDodleOverlay {
    public static final String PANEL_NAME = "QuickAddEffect";
    public static final String STATE_NAME = AnimationTool.TOOL_NAME + "." + PANEL_NAME;
    
    private final AssetProvider assetProvider;
    private final ConfigureEffectOverlay ceOverlay;
    private final EffectIconResolver effectIconResolver;
    private final EngineEventManager eventManager;
    private final FullEditorViewState fullViewState;
    
    private Map<EffectType, Map<String, EffectDefinition>> defaultDefinitions;
    
    @Inject
    public QuickAddEffectPanel(AssetProvider assetProvider, ConfigureEffectOverlay ceOverlay, DodleStageManager stageManager, EffectIconResolver effectIconResolver, EngineEventManager eventManager, FullEditorDodleOverlayRegistry fedoRegistry, FullEditorViewState fullViewState) {
        super(stageManager, eventManager);
        
        this.assetProvider = assetProvider;
        this.ceOverlay = ceOverlay;
        this.effectIconResolver = effectIconResolver;
        this.eventManager = eventManager;
        this.fullViewState = fullViewState;
        
        fedoRegistry.registerOverlay(this);
    }

    @Override
    public final String getName() {
        return PANEL_NAME;
    }

    @Override
    public final void initialize(Stack dodleOverlayStack, Skin skin) {  
        final Table initPanel = baseInitialize(dodleOverlayStack, skin);
        
        if (initPanel != null) {
            defaultDefinitions = EffectDefinition.getDefaultDefinitions(assetProvider);
            
            eventManager.addListener(new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
                @Override
                public void listen(EngineEventData data) {
                    String state = data.getFirstStringParam();
                    
                    if (state.indexOf(STATE_NAME) == 0) {
                        EffectType eventEffectType;
                        
                        try {
                            eventEffectType = EffectType.valueOf(state.replace(STATE_NAME + ".", ""));
                        } catch (IllegalArgumentException iae) {
                            slidePanel(false);
                            return;
                        }
                        
                        slidePanel(rebuildPanel(initPanel, eventEffectType));
                    } else {
                        slidePanel(false);
                    }
                }
            });
            
            for (EffectType type : defaultDefinitions.keySet()) {
                for (String key : defaultDefinitions.get(type).keySet()) {
                    fullViewState.registerOverlayView(QuickAddEffectPanel.STATE_NAME + "." + type.name() + "." + key, ceOverlay);
                }
            }
        }
    }
    
    private boolean rebuildPanel(Table panel, final EffectType effectType) {
        boolean result = false;
        panel.clear();
                        
        for (final String effectName : defaultDefinitions.get(effectType).keySet()) {
            final ImageButton button = LmlUtility.createButton(effectIconResolver.getIcon(effectType, effectName), null, 0.8f);
            
            if (button != null) {
                result = true;
                
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, STATE_NAME + "." + effectType.name() + "." + effectName);
                    }
                });

                panel.add(button).expand().fillX().row();
            }
        }
        
        return result;
    }
}
