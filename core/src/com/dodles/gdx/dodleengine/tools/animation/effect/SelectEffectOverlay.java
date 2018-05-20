package com.dodles.gdx.dodleengine.tools.animation.effect;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.EffectDefinition;
import com.dodles.gdx.dodleengine.animation.EffectType;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.ForceReactivationEditorView;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.tools.animation.EffectIconResolver;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

/**
 * Overlay that allows selecting a new effect.
 */
@PerDodleEngine
public class SelectEffectOverlay extends AbstractEditorView implements ForceReactivationEditorView {    
    private final AssetProvider assetProvider;
    private final ConfigureEffectOverlay ceOverlay;
    private final EffectIconResolver effectIconResolver;
    private final EngineEventManager eventManager;
    private final FullEditorViewState fullViewState;
    private final OkCancelStackManager okCancelStack;
    
    private Map<EffectType, Map<String, EffectDefinition>> defaultDefinitions;
    private Table rootTable;
    private Table[] rows;
    private Skin skin;
    
    @Inject
    public SelectEffectOverlay(AssetProvider assetProvider, ConfigureEffectOverlay ceOverlay, EffectIconResolver effectIconResolver, EngineEventManager eventManager, FullEditorViewState fullViewState, OkCancelStackManager okCancelStack) {
        this.assetProvider = assetProvider;
        this.ceOverlay = ceOverlay;
        this.effectIconResolver = effectIconResolver;
        this.eventManager = eventManager;
        this.fullViewState = fullViewState;
        this.okCancelStack = okCancelStack;
    }
    
    @Override
    public final void activate(Skin pSkin, String newState) {
        if (rootTable == null) {
            skin = pSkin;
            rootTable = FullEditorInterface.getThreeRowOverlay(assetProvider, skin);
            this.addActor(rootTable);
            
            rows = new Table[] {
                rootTable.findActor("row1"),
                rootTable.findActor("row2"),
                rootTable.findActor("row3")
            };
            
            defaultDefinitions = EffectDefinition.getDefaultDefinitions(assetProvider);
        
            for (EffectType type : defaultDefinitions.keySet()) {
                for (String key : defaultDefinitions.get(type).keySet()) {
                    fullViewState.registerOverlayView(SelectEffectTypePanel.STATE_NAME + "." + type.name() + "." + key, ceOverlay);
                }
            }
        }
        
        updateButtons(newState);
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
    
    private void updateButtons(final String state) {        
        for (Table row : rows) {
            row.clear();
        }
        
        EffectType effectType = EffectType.valueOf(state.substring(state.lastIndexOf(".") + 1));
        Map<String, EffectDefinition> definitions = defaultDefinitions.get(effectType);
        
        if (definitions != null) {
            Set<String> keys = definitions.keySet();
            int buttonsPerRow = (keys.size() / rows.length) + 1;
            int curRow = 0;
            int buttonsOnCurrentRow = 0;
            
            for (final String key : keys) {
                AtlasRegion region = effectIconResolver.getIcon(effectType, key);
                Button button;
                
                if (region != null) {
                    button = LmlUtility.createButton(region, null, 1f);
                } else {
                    button = new TextButton(definitions.get(key).getDisplayName(), skin);
                }
                
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, state + "." + key);
                    }
                });
                
                rows[curRow].add(button).maxHeight(FullEditorInterface.getInterfaceRowSize()).expandX().expandY().fillY();
                buttonsOnCurrentRow++;
                
                if (buttonsOnCurrentRow == buttonsPerRow) {
                    rows[curRow].row();
                    curRow++;
                    buttonsOnCurrentRow = 0;
                }
            }
        }
    }
}
