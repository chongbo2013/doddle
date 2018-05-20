package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.assets.DemoAssets;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.tools.animation.*;
import com.dodles.gdx.dodleengine.tools.animation.subtools.DemoActions.MoveInCircleAction;

import javax.inject.Inject;

public class RotateEffectDemoSubtool extends AbstractAnimationSubtool {

    //region Properties & Variables

    // Subtool-specific Subsystem References
    private final DemoAssets demoAssets;
    private final DodleStageManager dodleStageManager;

    //endregion Properties & Variables

    //region Constructor
    @Inject
    public RotateEffectDemoSubtool(
            AnimationManager animationManager,
            AnimationSubtoolRegistry animationSubtoolRegistry,
            AnimationTool animationTool,
            DemoAssets demoAssets,
            DodleStageManager dodleStageManager,
            final ObjectManager objectManager,
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
        this.demoAssets = demoAssets;
        this.dodleStageManager = dodleStageManager;
    }
    //endregion Constructor

    //region AbstractAnimationSubtool Implementation

    @Override
    public AnimationSubtoolState getSubtoolState() {
        return AnimationSubtoolState.ROTATE_EFFECT_DEMO;
    }

    @Override
    public void onActivation(AnimationSubtoolState previousState) {
        System.out.println("RotateEffectDemoSubtool::onActivation <- " + previousState);
        dodleStageManager.setDisplayMode(DodleStageManager.DisplayMode.NORMAL);

        initializeAssets();
        demoAssets.show(AnimationSubtoolState.ROTATE_EFFECT_DEMO);
    }

    @Override
    public void onDeactivation(AnimationSubtoolState nextState) {
        System.out.println("RotateEffectDemoSubtool::onDeactivation -> " + nextState);

        demoAssets.reset(AnimationSubtoolState.ROTATE_EFFECT_DEMO);
    }

    //endregion AbstractAnimationSubtool Implementation

    //region Private Helper Functions

    private void initializeAssets() {
        demoAssets.initializeAssets();
        Image rotatepath1 = demoAssets.getRotatepath().getFirst();
        Image rotatepath2 = demoAssets.getRotatepath().getSecond();
        Image character = demoAssets.getCharacter();
        Image touchpoint = demoAssets.getTouchpoint();

        // Set up position
        Vector2 backgroundSize = demoAssets.getBackgroundSize();
        Vector2 characterSize = demoAssets.getCharacterSize();
        Vector2 touchpointSize = demoAssets.getTouchpointSize();
        Vector2 rotatepathSize = demoAssets.getRotatepathSize();

        Vector2 startCharacter = new Vector2((backgroundSize.x - characterSize.x) / 2f, (backgroundSize.y - characterSize.y) / 2f);
        Vector2 startTouchPoint = new Vector2((backgroundSize.x - rotatepathSize.x *7f/8f - touchpointSize.x) / 2f, (backgroundSize.y - touchpointSize.y) / 2f);

        character.setPosition(startCharacter.x, startCharacter.y);
        rotatepath1.setPosition((backgroundSize.x - rotatepathSize.x) / 2f, (backgroundSize.y - rotatepathSize.y) / 2f);
        rotatepath2.setPosition((backgroundSize.x - rotatepathSize.x) / 2f, (backgroundSize.y - rotatepathSize.y) / 2f);
        touchpoint.setPosition(startTouchPoint.x, startTouchPoint.y);

        // Animate the demo assets
        float duration = 4f;

        character.setOrigin(characterSize.x/2f, characterSize.y/2f);
        character.addAction(Actions.forever(Actions.sequence(
                Actions.repeat(2, Actions.rotateBy(360f, duration)),
                Actions.repeat(2, Actions.rotateBy(-360f, duration)))));
        touchpoint.addAction(Actions.forever(Actions.sequence(
                Actions.repeat(2, new MoveInCircleAction((rotatepathSize.x*7f/8f) /2, 360f, true, duration)),
                Actions.repeat(2,new MoveInCircleAction((rotatepathSize.x*7f/8f) /2, 360f, false, duration)))));
        rotatepath1.addAction(Actions.forever(Actions.sequence(Actions.delay(2f*duration), Actions.hide(), Actions.delay(2f*duration), Actions.show())));
        rotatepath2.addAction(Actions.forever(Actions.sequence(Actions.hide(), Actions.delay(2f*duration), Actions.show(), Actions.delay(2f*duration))));
    }

    //endregion Private Helper Functions
}