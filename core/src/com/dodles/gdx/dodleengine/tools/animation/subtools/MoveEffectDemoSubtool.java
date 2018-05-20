package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.*;
import com.dodles.gdx.dodleengine.assets.DemoAssets;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.*;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolState;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class MoveEffectDemoSubtool extends AbstractAnimationSubtool {

    //region Properties & Variables

    // Subtool-specific Subsystem References
    private final DemoAssets demoAssets;
    private final DodleStageManager dodleStageManager;

    // Internal Variables
    private Vector2 startCharacter;
    private Vector2 startTouchPoint;
    private Vector2 movepathSize;

    //endregion Properties & Variables

    //region Constructor
    @Inject
    public MoveEffectDemoSubtool(
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
        return AnimationSubtoolState.MOVE_EFFECT_DEMO;
    }

    @Override
    public void onActivation(AnimationSubtoolState previousState) {
        System.out.println("MoveEffectDemoSubtool::onActivation <- " + previousState);
        dodleStageManager.setDisplayMode(DodleStageManager.DisplayMode.NORMAL);

        initializeAssets();
        demoAssets.show(AnimationSubtoolState.MOVE_EFFECT_DEMO);
    }

    @Override
    public void onDeactivation(AnimationSubtoolState nextState) {
        System.out.println("MoveEffectDemoSubtool::onDeactivation -> " + nextState);

        demoAssets.reset(AnimationSubtoolState.MOVE_EFFECT_DEMO);
    }

    //endregion AbstractAnimationSubtool Implementation

    //region Private Helper Functions

    private void initializeAssets() {
        demoAssets.initializeAssets();
        Image movepath = demoAssets.getMovepath();
        Image character = demoAssets.getCharacter();
        Image touchpoint = demoAssets.getTouchpoint();

        // Set up position
        Vector2 backgroundSize = demoAssets.getBackgroundSize();
        Vector2 characterSize = demoAssets.getCharacterSize();
        Vector2 touchpointSize = demoAssets.getTouchpointSize();
        movepathSize = demoAssets.getMovepathSize();

        float offToLeft = backgroundSize.x * 0.25f;

        startCharacter = new Vector2((backgroundSize.x - characterSize.x) / 2f - offToLeft, (backgroundSize.y - characterSize.y) / 2f - backgroundSize.y*0.15f);
        startTouchPoint = new Vector2((backgroundSize.x - touchpointSize.x) / 2f - offToLeft, (backgroundSize.y - touchpointSize.y) / 2f - 5f - backgroundSize.y*0.15f);

        character.setPosition(startCharacter.x, startCharacter.y);
        touchpoint.setPosition(startTouchPoint.x, startTouchPoint.y);
        movepath.setPosition(backgroundSize.x / 2f - offToLeft, backgroundSize.y / 2f - backgroundSize.y*0.15f);

        // Draw Demo Assets
        character.addAction(Actions.forever(getSequenceAction(getDistance(), getTiming(), true)));
        touchpoint.addAction(Actions.forever(getSequenceAction(getDistance(), getTiming(), false)));
    }

    private SequenceAction getSequenceAction(List<Vector2> distances, List<Float> timing, boolean isCharacter){
        SequenceAction sequenceAction = new SequenceAction();
        for(int i = 0; i < distances.size(); i++)
            sequenceAction.addAction(Actions.moveBy(distances.get(i).x, distances.get(i).y, timing.get(i)));

        if(isCharacter)
            sequenceAction.addAction(Actions.moveTo(startCharacter.x, startCharacter.y));
        else
            sequenceAction.addAction(Actions.moveTo(startTouchPoint.x, startTouchPoint.y));

        return sequenceAction;
    }

    private List<Vector2> getDistance(){
        return Arrays.asList(
            new Vector2(movepathSize.x*1f/3f, -15f),
            new Vector2(movepathSize.x*1f/3f, 15f),
            new Vector2(-movepathSize.x*1f/4f, movepathSize.y*1f/3f),
            new Vector2(-movepathSize.x*3f/10f, movepathSize.y*1f/6f),
            new Vector2(movepathSize.x*3f/10f,movepathSize.y*1f/3f),
            new Vector2(movepathSize.x*0.5f, -15)
        );
    }

    private List<Float> getTiming(){
        List<Float> timing = new ArrayList<Float>();
        for(int i = 0; i < 6; i++){
            timing.add(0.5f);
        }
        return timing;
    }

    //endregion Private Helper Functions
}
