package com.dodles.gdx.dodleengine.tools.animation.main;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.CameraManager;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.TimelineEffectGroup;
import com.dodles.gdx.dodleengine.animation.TimelineInfo;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.dodles.gdx.dodleengine.tools.animation.effect.QuickAddEffectPanel;
import com.dodles.gdx.dodleengine.util.LmlUtility;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Editor row that contains the animation timeline.
 */
public class TimelineFullEditorRow extends AbstractEditorView {
    private final AnimationManager animationManager;
    private final AnimationTool animationTool;
    private final AssetProvider assetProvider;
    private final CameraManager cameraManager;
    private final EngineEventManager eventManager;
    private final ObjectManager objectManager;
    private final TimelineWidget timelineWidget;

    private Table rootTable;
    private TextureAtlas animationIconsAtlas;
    private String activeEffectGroupKey;
    private Button effectsButton;
    
    @Inject
    public TimelineFullEditorRow(
            AnimationManager animationManager,
            AnimationTool animationTool,
            AssetProvider assetProvider,
            CameraManager cameraManager,
            EngineEventManager eventManager,
            ObjectManager objectManager,
            TimelineWidget timelineWidget
    ) {
        this.animationManager = animationManager;
        this.animationTool = animationTool;
        this.assetProvider = assetProvider;
        this.cameraManager = cameraManager;
        this.eventManager = eventManager;
        this.objectManager = objectManager;
        this.timelineWidget = timelineWidget;
    }
    
    @Override
    public final void activate(Skin newSkin, String newState) {        
        if (rootTable == null) {
            rootTable = new Table();
            rootTable.setFillParent(true);
            
            animationIconsAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_ANIMATIONICONS_ATLAS);
            
            effectsButton = LmlUtility.createButton(animationIconsAtlas, "effects", 0.8f);
            
            effectsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!effectsButton.isDisabled()) {
                        updateSelectedEffectGroup(false);
                    }
                }
            });
            
            eventManager.addListener(new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
                @Override
                public void listen(EngineEventData data) {
                    boolean disabled = true;
                    String param = data.getFirstStringParam();
                    
                    if (param != null) {
                        if (param.equals(AnimationTool.TOOL_NAME)) {
                            disabled = false;
                        } else if (param.startsWith(QuickAddEffectPanel.STATE_NAME)) {
                            disabled = false;
                        }
                    }
                    
                    effectsButton.setDisabled(disabled);
                    
                    if (disabled) {
                        updateSelectedEffectGroup(true);
                    }
                }
            });
                    
            rootTable.add(effectsButton).expandY().fillY().width(Value.percentWidth(0.1666666666666f, rootTable));
            rootTable.add(this.timelineWidget).expand().fill();
            
            this.addActor(rootTable);
        }
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
    
    private void updateSelectedEffectGroup(boolean reset) {
        TimelineInfo info = new TimelineInfo(animationTool.getScene(), animationTool.getActiveBlock());
        Collection<TimelineEffectGroup> groups = info.getAllEffectGroups();
        TimelineEffectGroup teg = null;
        boolean useNextGroup = false;
        
        if (!reset) {
            for (TimelineEffectGroup group : groups) {
                if (activeEffectGroupKey == null || useNextGroup) {
                    teg = group;
                    break;
                }

                if (activeEffectGroupKey.equals(group.getKey())) {
                    useNextGroup = true;
                }
            }
        }

        timelineWidget.setActiveEffectGroup(teg);
        activeEffectGroupKey = null;

        if (teg != null) {
            activeEffectGroupKey = teg.getKey();
            eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, QuickAddEffectPanel.STATE_NAME + "." + teg.getEffectType().name());
        } else if (!reset) {
            eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, AnimationTool.TOOL_NAME);
        }

        // Show correct icon in menu
        AtlasRegion finalRegion = animationIconsAtlas.findRegion("effects");

        if (teg != null) {
            finalRegion = animationIconsAtlas.findRegion(teg.getEffectType().name());
        }

        TextureRegionDrawable region = new TextureRegionDrawable(finalRegion);
        ImageButtonStyle ibs = (ImageButtonStyle) effectsButton.getStyle();
        ibs.imageUp = region;
        ibs.imageDown = region;
        ibs.imageChecked = region;
        
        if (!reset) {
            objectManager.clearSelectedActors();
            
            // Focus on active actor if possible
            if (teg == null || !teg.getEffectType().isActorSpecific()) {    
                cameraManager.resetGlobalViewport(.25f);
            } else {
                DodlesActor actor = objectManager.getActor(teg.getActorID());

                if (actor != null) {
                    objectManager.selectActor(actor);
                    cameraManager.focus(actor);
                }
            }
        }
    }
}
