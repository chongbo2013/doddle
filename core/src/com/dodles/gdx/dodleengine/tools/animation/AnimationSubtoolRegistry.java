package com.dodles.gdx.dodleengine.tools.animation;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.tools.animation.subtools.AbstractAnimationSubtool;

import javax.inject.Inject;
import java.util.HashMap;

@PerDodleEngine
public class AnimationSubtoolRegistry {

    //region Properties & Variables
    private final HashMap<AnimationSubtoolState, AbstractAnimationSubtool> subtoolMap;
    //endregion Properties & Variables


    //region Constructor
    @Inject
    public AnimationSubtoolRegistry() {
        subtoolMap = new HashMap<AnimationSubtoolState, AbstractAnimationSubtool>();
    }
    //endregion Constructor


    //region Public API

    public void registerSubtool(AnimationSubtoolState state, AbstractAnimationSubtool subtool) {
        subtoolMap.put(state, subtool);
    }

    public AbstractAnimationSubtool getSubtool(AnimationSubtoolState subtoolState) {
        return subtoolMap.get(subtoolState);
    }

    //endregion Public API


}
