package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolState;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAnimationSubtool {

    // Subsystem References
    protected final AnimationManager animationManager;
    protected final AnimationTool animationTool;
    protected final EventBus eventBus;
    protected final ObjectManager objectManager;
    protected final OkCancelStackManager okCancelStackManager;

    // Local Variables
    protected final ArrayList<InputHandler> inputHandlers = new ArrayList<InputHandler>();

    public AbstractAnimationSubtool(
            AnimationManager animationManager,
            AnimationSubtoolRegistry animationSubtoolRegistry,
            AnimationTool animationTool,
            EventBus eventBus,
            ObjectManager objectManager,
            OkCancelStackManager okCancelStackManager
    ) {
        this.animationManager = animationManager;
        this.animationTool = animationTool;
        this.eventBus = eventBus;
        this.objectManager = objectManager;
        this.okCancelStackManager = okCancelStackManager;

        animationSubtoolRegistry.registerSubtool(getSubtoolState(), this);
    }

    public final List<InputHandler> getInputHandlers() {
        return inputHandlers;
    }

    public abstract AnimationSubtoolState getSubtoolState();
    public void onPreActivation(AnimationSubtoolState currentState, AbstractAnimationSubtool currentSubtool) {}
    public void onActivation(AnimationSubtoolState previousState) {}
    public void onDeactivation(AnimationSubtoolState nextState) {}

}
