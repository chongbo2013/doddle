package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.input.InteractionData;
import com.dodles.gdx.dodleengine.input.TouchInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolState;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;

import javax.inject.Inject;

@PerDodleEngine
public class TimelineSubtool extends AbstractAnimationSubtool implements TouchInputHandler {

    //region Properties & Variables

    // Subsystem References
    private final DodleStageManager dodleStageManager;

    //endregion Properties & Variables

    //region Constructor
    @Inject
    public TimelineSubtool(
            AnimationManager animationManager,
            AnimationSubtoolRegistry animationSubtoolRegistry,
            AnimationTool animationTool,
            DodleStageManager dodleStageManager,
            ObjectManager objectManager,
            EventBus eventBus,
            OkCancelStackManager okCancelStackManager

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

        // Input Listeners
        inputHandlers.add(this);
    }
    //endregion Constructor

    //region AbstractAnimationSubtool Implementation

    @Override
    public AnimationSubtoolState getSubtoolState() {
        return AnimationSubtoolState.TIMELINE;
    }

    @Override
    public void onActivation(AnimationSubtoolState previousState) {
        System.out.println("TimelineSubtool(AnimationTool)::onActivation <-" + previousState);
        dodleStageManager.setDisplayMode(DodleStageManager.DisplayMode.SHOW_OBJECT_MANIPULATION_OVERLAY);
    }

    @Override
    public void onDeactivation(AnimationSubtoolState nextState) {
        System.out.println("TimelineSubtool(AnimationTool)::onDeactivation ->" + nextState);
    }

    //endregion AbstractAnimationSubtool Implementation

    //region TouchInputHandler Implementation

    @Override
    public final void handleTouchStart(InteractionData startData, int pointer) { }

    @Override
    public final void handleTouchMove(InteractionData moveData, int pointer) { }

    @Override
    public final void handleTouchEnd(InteractionData endData, int pointer) {
        objectManager.select(endData.getDodlePoint());
        dodleStageManager.updateStateUi();
    }

    @Override
    public void handleTouchCancel() { }

    //endregion TouchInputHandler Implementation
}
