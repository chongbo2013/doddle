package com.dodles.gdx.dodleengine.tools.animation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.ui.widget.Draggable;

/**
 * Extends draggable to block touchdown events from bubbling, prevents bad interactions between draggables and scrollpanes.
 */
public class BlockingDraggable extends Draggable {
    private Vector2 startPoint;
    
    @Override
    public final boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
        startPoint = new Vector2(x, y);
        
        if (super.touchDown(event, x, y, pointer, button)) {
            event.stop();
            return true;
        }
        
        return false;
    }
}
