package com.dodles.gdx.dodleengine.editor;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import javax.inject.Inject;

/**
 * Stores editor state configuration.
 */
@PerDodleEngine
public class EditorState {
    //Stroke Config used for all tasks except for Shape Tool
    private StrokeConfig strokeConfig = new StrokeConfig();
    //Stroke Config used for Shape Tool
    private StrokeConfig strokeConfigCopied = new StrokeConfig();

    @Inject
    public EditorState() {
    }

    public final StrokeConfig getStrokeConfig() {
        return strokeConfig;
    }

    public final void copyCurrentStrokeConfig() {strokeConfigCopied = strokeConfig;}

    public final void revertToPreviousStrokeConfig() {strokeConfig = strokeConfigCopied;}

    public final void setStrokeConfig(StrokeConfig newStrokeConfig) {
        strokeConfig = newStrokeConfig;
    }

}
