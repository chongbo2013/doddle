package com.dodles.gdx.dodleengine.tools;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * A tool that can be clicked on.
 */
public interface ClickableTool {
    /**
     * Returns a Clicklistener.
     */
    ClickListener onClick();
}
