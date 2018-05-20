package com.dodles.gdx.dodleengine.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * A view that's used in the editor.
 */
public abstract class AbstractEditorView extends WidgetGroup {
    /**
     * Activates the view with the given skin.
     */
    public abstract void activate(Skin skin, String newState);
    
    /**
     * Deactivates the view.
     */
    public abstract void deactivate();
    
    /**
     * Returns the widget container for this view.
     */
    protected abstract WidgetGroup getRootWidget();
    
    @Override
    public final float getPrefWidth() {
        return getRootWidget().getPrefWidth();
    }

    @Override
    public final float getPrefHeight() {
        return getRootWidget().getPrefHeight();
    }

    @Override
    public final float getMinWidth() {
        return getRootWidget().getMinWidth();
    }

    @Override
    public final float getMinHeight() {
        return getRootWidget().getMinHeight();
    }
    
    @Override
    public final float getMaxWidth() {
        return getRootWidget().getMaxWidth();
    }

    @Override
    public final float getMaxHeight() {
        return getRootWidget().getMaxHeight();
    }
}
