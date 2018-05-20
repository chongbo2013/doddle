package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackFrame;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.overlays.SelectedActorOverlay;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolState;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.dodles.gdx.dodleengine.tools.nullTool.NullTool;

import javax.inject.Inject;

@PerDodleEngine
public class EffectSelectSubtool extends AbstractAnimationSubtool {

    //region Properties & Variables

    // Subsystem References
    private final DodleStageManager dodleStageManager;
    private final SelectedActorOverlay selectedActorOverlay;
    private final ToolRegistry toolRegistry;

    // Internal Variables
    private String initialOkCancelLayer;

    //endregion Properties & Variables

    //region Constructor
    @Inject
    public EffectSelectSubtool(
            AnimationManager animationManager,
            AnimationSubtoolRegistry animationSubtoolRegistry,
            AnimationTool animationTool,
            DodleStageManager dodleStageManager,
            EventBus eventBus,
            ObjectManager objectManager,
            OkCancelStackManager okCancelStackManager,
            SelectedActorOverlay selectedActorOverlay,
            ToolRegistry toolRegistry
    ) {
        // Super
        super(
                animationManager,
                animationSubtoolRegistry,
                animationTool,
                eventBus,
                objectManager,
                okCancelStackManager
        );

        // Subsystem References
        this.dodleStageManager = dodleStageManager;
        this.selectedActorOverlay = selectedActorOverlay;
        this.toolRegistry = toolRegistry;
    }
    //endregion Constructor

    //region AbstractAnimationSubtool Implementation

    @Override
    public AnimationSubtoolState getSubtoolState() {
        return AnimationSubtoolState.EFFECT_SELECT;
    }

    @Override
    public void onActivation(final AnimationSubtoolState previousState) {
        System.out.println("EffectSelectSubtool(AnimationTool)::onActivation <- " + previousState);

        final DodlesActor originallySelectedActor = objectManager.getSelectedActor();
        final boolean animationToolWasDisabled = (previousState == AnimationSubtoolState.DISABLED);

        if (!animationToolWasDisabled) {
            objectManager.selectActor(originallySelectedActor);
        }

        // Manage OK Stack
        initialOkCancelLayer = okCancelStackManager.getLayerID();
        okCancelStackManager.push(new OkCancelStackFrame("Effects", true, false) {
            @Override
            public void execute() {
                if (animationToolWasDisabled) {
                    toolRegistry.setActiveTool(NullTool.TOOL_NAME);
                    objectManager.selectActor(originallySelectedActor);
                    dodleStageManager.setDisplayMode(DodleStageManager.DisplayMode.SHOW_OBJECT_MANIPULATION_OVERLAY);
                } else {
                    eventBus.publish(EventTopic.EDITOR, EventType.SET_ANIMATION_TOOL_STATE, previousState.toString());
                }
                // TODO: discuss with Jordan what this does - CAD 2017.10.17
                selectedActorOverlay.toggleEffectsOff();
            }
        });
    }

    @Override
    public void onDeactivation(AnimationSubtoolState nextState) {
        System.out.println("EffectSelectSubtool(AnimationTool)::onDeactivation -> " + nextState);

        // Manage OK Stack
        okCancelStackManager.reset();
    }

    //endregion AbstractAnimationSubtool Implementation
}
