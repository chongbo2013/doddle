package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.dodles.gdx.dodleengine.PerDodleEngine;

import javax.inject.Inject;

/**
 * To avoid circular dependencies between the AnimationSubtoolRegistry, AnimationTool, and animation subtools, we need
 * a third class to eager load the subtools so they can register themselves.
 */
@PerDodleEngine
public class AnimationSubtoolLoader {
    @Inject
    public AnimationSubtoolLoader(
            TimelineSubtool timelineSubtool,
            EffectSelectSubtool effectSelectSubtool,
            MoveEffectDemoSubtool moveEffectDemoSubtool,
            MoveEffectDrawSubtool moveEffectDrawSubtool,
            MoveEffectConfigureSubtool moveEffectConfigureSubtool,
            RotateEffectDemoSubtool rotateEffectDemoSubtool,
            RotateEffectInputSubtool rotateEffectRotateSubtool,
            RotateEffectConfigureSubtool rotateEffectConfigureSubtool,
            RotatePivotPointSubtool rotatePivotPointSubtool,
            ScaleEffectDemoSubtool scaleEffectDemoSubtool,
            ScaleEffectInputSubtool scaleEffectInputSubtool,
            ScaleEffectConfigureSubtool scaleEffectConfigureSubtool
    ) {}
}
