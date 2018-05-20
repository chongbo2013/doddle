package com.dodles.gdx.dodleengine.tools.animation.effect;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.BlockStatus;
import com.dodles.gdx.dodleengine.animation.EffectAnimator;
import com.dodles.gdx.dodleengine.animation.EffectDefinition;
import com.dodles.gdx.dodleengine.animation.EffectParameterType;
import com.dodles.gdx.dodleengine.animation.EffectTiming;
import com.dodles.gdx.dodleengine.animation.EffectType;
import com.dodles.gdx.dodleengine.animation.TimelineInfo;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.animation.ModifyEffectCommand;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.DensityManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import com.dodles.gdx.dodleengine.util.NumberFormatter;
import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Overlay that supports effect configuration.
 */
@PerDodleEngine
public class ConfigureEffectOverlay extends AbstractEditorView {
    private final AnimationManager animationManager;
    private final AnimationTool animationTool;
    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final DodleStageManager stageManager;
    private final EngineEventManager eventManager;
    private final FullEditorViewState fullViewState;
    private final NumberFormatter numberFormatter;
    private final ObjectManager objectManager;
    private final OkCancelStackManager okCancelStack;
    
    private EffectConfigurationInterfaceInfo effectInfo;
    private Map<EffectType, Map<String, EffectDefinition>> defaultDefinitions;
    private Table rootTable;
    private Table scrollContent;
    private Skin skin;
    private TextureAtlas animationIconsAtlas;
    private EffectType effectType;
    private String effectName;
    private EffectDefinition effectDefinition;
    private EffectAnimator previewAnimator;
    private TextButton afterLastEffect;
    private float curTime;
    
    @Inject
    public ConfigureEffectOverlay(
        AnimationManager animationManager,
        AnimationTool animationTool,
        AssetProvider assetProvider,
        CommandFactory commandFactory,
        CommandManager commandManager,
        DodleStageManager stageManager,
        EngineEventManager eventManager,
        FullEditorViewState fullViewState,
        NumberFormatter numberFormatter,
        ObjectManager objectManager,
        OkCancelStackManager okCancelStack
    ) {
        this.animationManager = animationManager;
        this.animationTool = animationTool;
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.stageManager = stageManager;
        this.eventManager = eventManager;
        this.fullViewState = fullViewState;
        this.numberFormatter = numberFormatter;
        this.objectManager = objectManager;
        this.okCancelStack = okCancelStack;
    }
    
    @Override
    public final void activate(Skin pSkin, final String newState) {
        if (rootTable == null) {
            skin = pSkin;
            rootTable = FullEditorInterface.getScrollableOverlay(assetProvider, skin, true);
            scrollContent = rootTable.findActor("scrollContent");
            this.addActor(rootTable);
            
            defaultDefinitions = EffectDefinition.getDefaultDefinitions(assetProvider);
            animationIconsAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_ANIMATIONICONS_ATLAS);
        }
        
        updateButtons(newState);
        
        okCancelStack.push(new Runnable() {
            @Override
            public void run() {
                String actorID = null;

                if (objectManager.getSelectedActor() != null) {
                    actorID = objectManager.getSelectedActor().getName();
                }

                ModifyEffectCommand command = (ModifyEffectCommand) commandFactory.createCommand(ModifyEffectCommand.COMMAND_NAME);
                TimelineInfo info = new TimelineInfo(animationTool.getScene(), animationTool.getActiveBlock());
                float delay = effectInfo.delaySlider.getValue();
                String afterEffectID = null;
                
                if (afterLastEffect.isChecked()) {
                    // after last effect for the actor in the block
                    float maxLength = -1;
                    
                    for (EffectTiming timing : animationTool.getActiveBlock().allEffects(actorID)) {
                        float curLength = timing.calculateEndTime();
                        
                        if (curLength > maxLength) {
                            maxLength = curLength;
                            afterEffectID = timing.getEffect().getEffectID();
                        }
                    }
                } else {
                    // at the current position in the timeline
                    BlockStatus status = animationManager.getBlockStatus(animationTool.getActiveBlock().getBlockId());
                    
                    if (status != null) {
                        delay = status.getCurTime();
                    }
                }

                command.init(
                    animationTool.getScene().getSceneID(),
                    UUID.uuid(),
                    animationTool.getActiveBlock().getBlockId(),
                    actorID,
                    effectType,
                    effectName,
                    effectDefinition,
                    createParameters(),
                    afterEffectID,
                    delay
                );

                command.execute();
                commandManager.add(command);
                
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, newState.substring(0, newState.lastIndexOf(".")));
            }
        }, new Runnable() {
            @Override
            public void run() {
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, newState.substring(0, newState.lastIndexOf(".")));
            }
        });
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
    
    private HashMap<EffectParameterType, Object> createParameters() {
        HashMap<EffectParameterType, Object> parameters = new HashMap<EffectParameterType, Object>();

        for (EffectParameterType parameterType : effectInfo.getEffectParameterSliders().keySet()) {
            parameters.put(parameterType, effectInfo.getEffectParameterSliders().get(parameterType).getValue());
        }
        
        return parameters;
    }

    private void updateButtons(String state) {
        previewAnimator = null;
        
        scrollContent.clear();
        
        int keyIndex = state.lastIndexOf(".");
        effectName = state.substring(keyIndex + 1);
        effectType = EffectType.valueOf(state.substring(state.lastIndexOf(".", keyIndex - 1) + 1, keyIndex));
        effectDefinition = defaultDefinitions.get(effectType).get(effectName);
        
        Table topRow = new Table();
        final Button playButton = LmlUtility.createButton(animationIconsAtlas, "play", "pause", 0.8f);
        
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (previewAnimator != null) {
                    resetAnimation();
                }
                
                if (playButton.isChecked()) {
                    previewAnimator = new EffectAnimator("testeffect", animationManager, objectManager, effectType, effectName, effectDefinition, createParameters(), objectManager.getSelectedActor());
                    stageManager.setDisplayMode();
                    curTime = animationManager.getBlockTime(animationTool.getActiveBlock().getBlockId());
                    
                    float delay = 0;
                    
                    if (effectType != EffectType.MOVE) {
                        delay = 1;
                    }
                    
                    animationManager.playSingleEffect(previewAnimator, new Runnable() {
                        @Override
                        public void run() {
                            playButton.setChecked(false);
                            resetAnimation();
                        }
                    }, delay);
                }
            }
        });
        
        topRow.add(playButton).expand().fillY();
        
        Label effectLabel = new Label(effectDefinition.getDisplayName(), skin, "default-black");
        topRow.add(effectLabel).expand().fillY().width(Value.percentWidth(0.9f));
        
        scrollContent.add(topRow).expandX().fillX().height(FullEditorInterface.getInterfaceRowSize()).row();
        
        Table timingRow = new Table();
        float padding = DensityManager.getScale() * 10;
        
        Label timingLabel = new Label("Timing:", skin, "default-black");
        timingRow.add(timingLabel).expand().fillY().align(Align.left).padLeft(padding);
        
        boolean afterLastEffectEnabled = state.startsWith(SelectEffectTypePanel.STATE_NAME);
        afterLastEffect = new TextButton("After Last Effect", skin, "medium-toggle");
        afterLastEffect.setChecked(afterLastEffectEnabled);
        timingRow.add(afterLastEffect).expand().fillY().align(Align.right).padLeft(padding);
        
        TextButton timelinePosition = new TextButton("Timeline Position", skin, "medium-toggle");
        timelinePosition.setChecked(!afterLastEffectEnabled);
        timingRow.add(timelinePosition).expand().fillY().align(Align.left).padRight(padding);
        
        ButtonGroup timingBg = new ButtonGroup(afterLastEffect, timelinePosition);
        scrollContent.add(timingRow).expandX().fillX().height(FullEditorInterface.getInterfaceRowSize()).row();
                
        HashMap<EffectParameterType, Object> parameterValues = new HashMap<EffectParameterType, Object>();
        
        for (EffectParameterType parameterType : effectDefinition.getDefaultParameters().keySet()) {
            parameterValues.put(parameterType, effectDefinition.getDefaultParameters().get(parameterType));
        }
        
        effectInfo = createEffectConfigurationUi(skin, objectManager, numberFormatter, parameterValues, 0, scrollContent, padding, padding);
    }
    
    /**
     * Creates the effect configuration widgets and adds them to the passed in table.
     */
    public static EffectConfigurationInterfaceInfo createEffectConfigurationUi(
        Skin skin,
        ObjectManager objectManager,
        final NumberFormatter numberFormatter,
        Map<EffectParameterType, Object> parameters,
        float delay,
        Table target,
        float padLeft,
        float padRight
    ) {
        HashMap<EffectParameterType, Slider> paramSliders = new HashMap<EffectParameterType, Slider>();
        
        Table delayRow = new Table();
        Label delayLabel = new Label("Delay", skin, "default-black");
        delayRow.add(delayLabel).padRight(DensityManager.getDensity().getScale() * 5).padLeft(padLeft);
        Slider delaySlider = createSlider(skin, numberFormatter, delayRow, delay, 0, 25, 0.1f);
        target.add(delayRow).expandX().fillX().height(FullEditorInterface.getInterfaceRowSize()).padRight(padRight).row();
        
        for (EffectParameterType parameterType : parameters.keySet()) {
            if (!parameterType.isConfigurable()) {
                continue;
            }
            
            Table parameterRow = new Table();
            
            Label descLabel = new Label(parameterType.name().toLowerCase(), skin, "default-black");
            parameterRow.add(descLabel).padRight(DensityManager.getDensity().getScale() * 5).padLeft(padLeft);
            
            float minValue = 0;
            float maxValue = 1000;
            float stepSize = 1;
                    
            // CHECKSTYLE.OFF: MissingSwitchDefault - some parameters could use the defaults...
            switch (parameterType) {
                case DEGREES:
                    minValue = -360;
                    maxValue = 360;
                    break;
                    
                case TRANSLATE_X:
                case TRANSLATE_Y:
                    minValue = -1000;
                    break;
                    
                case SCENE_NUM:
                    minValue = 1;
                    maxValue = objectManager.allSceneData().size();
                    break;
                    
                case PHASE_NUM:
                    DodlesActor selectedActor = objectManager.getSelectedActor();
                    minValue = 1;
                    maxValue = 1;
                    
                    if (selectedActor instanceof DodlesGroup) {
                        maxValue = ((DodlesGroup) selectedActor).getPhases().size();
                    }
                    break;
                
                case LENGTH:
                case SCALE:
                    maxValue = 25;
                    stepSize = 0.1f;
            }
            // CHECKSTYLE.ON: MissingSwitchDefault
            
            Slider curSlider = createSlider(skin, numberFormatter, parameterRow, (Float) parameters.get(parameterType), minValue, maxValue, stepSize);
            target.add(parameterRow).expandX().fillX().height(FullEditorInterface.getInterfaceRowSize()).padRight(padRight).row();
            paramSliders.put(parameterType, curSlider);
        }
        
        return new EffectConfigurationInterfaceInfo(delaySlider, paramSliders);
    }
    
    private static Slider createSlider(Skin skin, final NumberFormatter numberFormatter, Table row, float value, float minValue, float maxValue, float stepSize) {
        final Label valueLabel = new Label(value + "", skin, "default-black");
        final Slider curSlider = new Slider(minValue, maxValue, stepSize, false, skin);
        LmlUtility.configureSliderLabel(valueLabel, curSlider, 7, value, numberFormatter);

        curSlider.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // prevents scrollpane and slider from interfering with each other...
                event.stop();
                return false;
            }
        });
        
        row.add(curSlider).fill().expand().maxHeight(FullEditorInterface.getInterfaceRowSize());
        row.add(valueLabel).padLeft(DensityManager.getDensity().getScale() * 5).row();
        
        return curSlider;
    }
    
    private void resetAnimation() {
        animationManager.endAnimation();
        animationTool.getActiveBlock().resetToBaseTransform();
        animationManager.startAnimation(
                animationTool.getScene().getSceneID(),
                animationTool.getActiveBlock().getBlockId(),
                curTime);
        animationManager.stopAnimation();
        stageManager.setDisplayMode(AnimationTool.getDisplayModeForState(fullViewState));
    }
    
    /**
     * Encapsulates UI elements for configuring effects.
     */
    public static class EffectConfigurationInterfaceInfo {
        private Slider delaySlider;
        private HashMap<EffectParameterType, Slider> effectParameterSliders;
        
        public EffectConfigurationInterfaceInfo(Slider delaySlider, HashMap<EffectParameterType, Slider> effectParameterSliders) {
            this.delaySlider = delaySlider;
            this.effectParameterSliders = effectParameterSliders;
        }
        
        /**
         * Returns the slider that effects the delay for the effect.
         */
        public final Slider getDelaySlider() {
            return delaySlider;
        }
        
        /**
         * Returns all parameter sliders for the effect.
         */
        public final HashMap<EffectParameterType, Slider> getEffectParameterSliders() {
            return effectParameterSliders;
        }
    }
}
