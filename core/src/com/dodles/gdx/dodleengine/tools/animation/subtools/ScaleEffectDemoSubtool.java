package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.assets.DemoAssets;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolState;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;

import javax.inject.Inject;

public class ScaleEffectDemoSubtool extends AbstractAnimationSubtool{

    //region Properties & Variables

    // Subtool-specific Subsystem References
    private final DemoAssets demoAssets;
    private final DodleStageManager dodleStageManager;

    // Internal Variables
    private final float duration = 2f;

    //endregion Properties & Variables

    //region Constructor
    @Inject
    public ScaleEffectDemoSubtool(
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
        return AnimationSubtoolState.SCALE_EFFECT_DEMO;
    }

    @Override
    public void onActivation(AnimationSubtoolState previousState) {
        System.out.println("MoveEffectDemoSubtool::onActivation <- " + previousState);
        dodleStageManager.setDisplayMode(DodleStageManager.DisplayMode.NORMAL);

        initializeAssets();
        demoAssets.show(AnimationSubtoolState.SCALE_EFFECT_DEMO);
    }

    @Override
    public void onDeactivation(AnimationSubtoolState nextState) {
        System.out.println("MoveEffectDemoSubtool::onDeactivation -> " + nextState);

        demoAssets.reset(AnimationSubtoolState.SCALE_EFFECT_DEMO);
    }

    //endregion AbstractAnimationSubtool Implementation

    //region Private Helper Functions

    private void initializeAssets() {
        demoAssets.initializeAssets();
        Image scalepath1 = demoAssets.getScalepath().getFirst();
        Image scalepath2 = demoAssets.getScalepath().getSecond();
        Image character = demoAssets.getCharacter();
        Image touchpoint1 = demoAssets.getTouchpoint();
        Image touchpoint2 = demoAssets.getSecondTouchPoint();

        // Set up position
        Vector2 backgroundSize = demoAssets.getBackgroundSize();
        Vector2 characterSize = demoAssets.getCharacterSize();
        Vector2 scalepathSize = demoAssets.getScalepathSize();

        Vector2 startCharacter = new Vector2((backgroundSize.x - characterSize.x) / 2f, (backgroundSize.y - characterSize.y) / 2f);
        Vector2 startTouchPoint = new Vector2((backgroundSize.x - scalepathSize.x *7f/8f) / 2f, (backgroundSize.y - scalepathSize.y *7f/8f) / 2f);
        Vector2 scalepath1Pos = new Vector2((backgroundSize.x - scalepathSize.x*2f) / 2f, (backgroundSize.y - scalepathSize.y*2.2f) / 2f + scalepathSize.y);
        Vector2 scalepath2Pos = new Vector2((backgroundSize.x - scalepathSize.x*2f) / 2f + scalepathSize.x, (backgroundSize.y - scalepathSize.y*2.2f) / 2f);

        character.setPosition(startCharacter.x, startCharacter.y);
        scalepath1.setOrigin(scalepathSize.x / 2f, scalepathSize.y / 2f);
        scalepath2.setOrigin(scalepathSize.x / 2f, scalepathSize.y / 2f);
        scalepath1.rotateBy(180f);
        scalepath1.setPosition(scalepath1Pos.x , scalepath1Pos.y);
        scalepath2.setPosition(scalepath2Pos.x, scalepath2Pos.y);
        touchpoint1.setPosition(startTouchPoint.x, startTouchPoint.y);
        touchpoint2.setPosition(startTouchPoint.x, startTouchPoint.y);

        // Animation
        character.setOrigin(characterSize.x/2f, characterSize.y/2f);

        character.addAction(Actions.forever(getCharacterActions()));
        touchpoint1.addAction(Actions.forever(getTouchPointActions(true, scalepathSize.x *6.5f/8f, scalepathSize.y)));
        touchpoint2.addAction(Actions.forever(getTouchPointActions(false, scalepathSize.x *6.5f/8f, scalepathSize.y)));;
        scalepath1.addAction(Actions.forever(getScalePathActions()));
        scalepath2.addAction(Actions.forever(getScalePathActions()));
    }

    private SequenceAction getCharacterActions(){
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(Actions.scaleTo(1.5f, 1.5f, duration));
        sequenceAction.addAction(fadeAsset("character"));
        sequenceAction.addAction(Actions.scaleTo(0.7f, 0.7f, duration));
        sequenceAction.addAction(fadeAsset("character"));

        return sequenceAction;
    }

    private SequenceAction getTouchPointActions(boolean isFirst, float amountX, float amountY){
        SequenceAction sequenceAction = new SequenceAction();
        if(!isFirst) {
            amountX = -amountX;
            amountY = -amountY;
        }
        sequenceAction.addAction(Actions.moveBy(-amountX, amountY * 8f/9f, duration));
        sequenceAction.addAction(fadeAsset("character"));
        sequenceAction.addAction(Actions.moveBy(amountX, -amountY * 8f/9f, duration));
        sequenceAction.addAction(fadeAsset("character"));

        return sequenceAction;
    }

    private SequenceAction getScalePathActions(){
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(Actions.delay(duration));
        sequenceAction.addAction(fadeAsset("scalepath"));
        sequenceAction.addAction(Actions.delay(duration));
        sequenceAction.addAction(fadeAsset("scalepath"));

        return sequenceAction;
    }

    private SequenceAction fadeAsset(String name){
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(Actions.hide());

        if(name.equals("character"))
            sequenceAction.addAction(Actions.scaleTo(1f, 1f));
        else if(name.equals("scalepath"))
            sequenceAction.addAction(Actions.rotateBy(180f));

        sequenceAction.addAction(Actions.delay(0.5f));
        sequenceAction.addAction(Actions.show());

        return sequenceAction;
    }



    //endregion Private Helper Functions
}
