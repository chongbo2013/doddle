package com.dodles.gdx.dodleengine.tools.animation.splice;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.EffectChain;
import com.dodles.gdx.dodleengine.animation.EffectTiming;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.animation.UpdateBlockEffectsCommand;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.DensityManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.dodles.gdx.dodleengine.tools.animation.main.TimelineWidget;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import com.dodles.gdx.dodleengine.util.NumberFormatter;

import javax.inject.Inject;

/**
 * Displays the splice length slider.
 */
@PerDodleEngine
public class SpliceLengthFullEditorRow extends AbstractEditorView {
    public static final String STATE_NAME = AnimationTool.TOOL_NAME + ".Splice";
    
    private final AnimationManager animationManager;
    private final AnimationTool animationTool;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final EngineEventManager eventManager;
    private final NumberFormatter numberFormatter;
    private final ObjectManager objectManager;
    private final OkCancelStackManager okCancelStack;
    private final SpliceOptionsFullEditorRow spliceOptions;
    private final TimelineWidget timelineWidget;
    
    private Table rootTable;
    private Slider spliceLengthSlider;
    private UpdateBlockEffectsCommand ubeCommand;
    
    @Inject
    public SpliceLengthFullEditorRow(
        AnimationManager animationManager,
        AnimationTool animationTool,
        CommandFactory commandFactory,
        CommandManager commandManager,
        EngineEventManager eventManager,
        NumberFormatter numberFormatter,
        ObjectManager objectManager,
        OkCancelStackManager okCancelStack,
        SpliceOptionsFullEditorRow spliceOptions,
        TimelineWidget timelineWidget
    ) {
        this.animationManager = animationManager;
        this.animationTool = animationTool;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.eventManager = eventManager;
        this.numberFormatter = numberFormatter;
        this.objectManager = objectManager;
        this.okCancelStack = okCancelStack;
        this.spliceOptions = spliceOptions;
        this.timelineWidget = timelineWidget;
    }
    
    @Override
    public final void activate(Skin skin, String newState) {                    
        if (rootTable == null) {
            rootTable = new Table();
            rootTable.setFillParent(true);
            
            float pad = DensityManager.getScale() * 10;
            Label labelLabel = new Label("Length:", skin, "default-black");
            rootTable.add(labelLabel).align(Align.left).padLeft(pad).padRight(pad);
            
            final Label valueLabel = new Label("", skin, "default-black");
            
            spliceLengthSlider = new Slider(0, 10, 0.1f, false, skin);
            spliceLengthSlider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    updateSplice();
                }
            });
            
            timelineWidget.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    updateSplice();
                    return true;
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    updateSplice();
                }
            });
            
            LmlUtility.configureSliderLabel(valueLabel, spliceLengthSlider, 4, 0, numberFormatter);
            
            rootTable.add(spliceLengthSlider).expand().fill();
            rootTable.add(valueLabel).align(Align.right).padRight(pad);
            
            this.addActor(rootTable);
        }
        
        okCancelStack.push(new Runnable() {
            @Override
            public void run() {
                ubeCommand.finishExecuteLive();
                commandManager.add(ubeCommand);
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, AnimationTool.TOOL_NAME);
            }
        }, new Runnable() {
            @Override
            public void run() {
                ubeCommand.undo();
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, AnimationTool.TOOL_NAME);
            }
        });
        
        spliceLengthSlider.setValue(0);
        ubeCommand = (UpdateBlockEffectsCommand) commandFactory.createCommand(UpdateBlockEffectsCommand.COMMAND_NAME);
        ubeCommand.beginExecuteLive(objectManager.getScene().getName(), animationTool.getActiveBlock().getBlockId());
    }

    @Override
    public final void deactivate() {
        ubeCommand = null;
        timelineWidget.clearSpliceOverlay();
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
    
    private void updateSplice() {
        if (ubeCommand != null) {
            ubeCommand.undo();

            float spliceLength = spliceLengthSlider.getValue();

            for (EffectChain chain : animationTool.getActiveBlock().getEffectChains()) {
                updateSplice(chain, 0, spliceLength);
            }

            timelineWidget.setSpliceTime(spliceLength);
        }
    }
    
    private void updateSplice(EffectChain chain, float time, float removeTimeRemaining) {
        float timelinePosition = animationManager.getBlockTime(animationTool.getActiveBlock().getBlockId());
        float spliceLength = spliceLengthSlider.getValue();
        float effectDelayStart = time;
        EffectTiming timing = chain.getCurrentTiming();
        time += timing.getDelay();
        float roundedTime = Math.round(time * 10) / 10f;

        if (timelinePosition <= roundedTime) {
            if (spliceOptions.isAddTime()) {
                timing.setDelay(timing.getDelay() + spliceLength);
                return;
            } else {
                float delay = timing.getDelay();

                if (delay > 0) {
                    float timeToSubtract = Math.min(removeTimeRemaining, delay);

                    if (timelinePosition > effectDelayStart) {
                        //timeToSubtract = Math.max(timeToSubtract - (timelinePosition - effectDelayStart), 0);
                        timeToSubtract = Math.min(timeToSubtract, roundedTime - timelinePosition);
                    }

                    timing.setDelay(delay - timeToSubtract);
                    removeTimeRemaining -= timeToSubtract;

                    if (removeTimeRemaining <= 0) {
                        return;
                    }
                }
            }
        }

        time += timing.getEffect().getEffectLength();
        
        for (EffectChain depChain : chain.getDependentEffects()) {
            updateSplice(depChain, time, removeTimeRemaining);
        }
    }
}
