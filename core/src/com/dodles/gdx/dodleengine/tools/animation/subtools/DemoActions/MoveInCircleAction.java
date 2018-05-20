package com.dodles.gdx.dodleengine.tools.animation.subtools.DemoActions;

import com.badlogic.gdx.scenes.scene2d.actions.RelativeTemporalAction;

public class MoveInCircleAction extends RelativeTemporalAction {
    // Angle in degree
    private float radius, angleDegree, curAngle;
    private boolean clockwise;

    protected void updateRelative (float percentDelta) {
        float deltaAngle = percentDelta * (float)(angleDegree/180 * Math.PI);
        curAngle += deltaAngle;
        float amountX = radius * (float) Math.sin(curAngle) * deltaAngle;
        float amountY = -radius * (float) Math.cos(curAngle) * deltaAngle;

        if(!clockwise){
            amountY = -amountY;
        }

        target.moveBy(amountX, amountY);
    }

    public MoveInCircleAction(float radius, float angle, boolean clockwise, float duration) {
        this.radius = radius;
        this.angleDegree = angle;
        this.clockwise = clockwise;
        this.curAngle = 0;
        this.setDuration(duration);
        this.setInterpolation(null);
    }
}
