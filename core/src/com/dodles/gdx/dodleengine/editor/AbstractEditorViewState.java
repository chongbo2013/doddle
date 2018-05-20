package com.dodles.gdx.dodleengine.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import java.util.HashMap;

/**
 * Common functionality for editor view states.
 */
public abstract class AbstractEditorViewState {
    /**
     * Returns the view from the map for the given state.
     */
    protected final AbstractEditorView getView(HashMap<String, AbstractEditorView> map, String state) {
        if (map.containsKey(state)) {
            return map.get(state);
        }
        
        int period = state.length();
        
        while ((period = state.lastIndexOf(".", period - 1)) >= 0) {
            String subState = state.substring(0, period);
            
            if (map.containsKey(subState)) {
                return map.get(subState);
            }
        }
        
        return map.get("");
    }
    
    /**
     * Injects the view for the given state into the proper host.
     */
    protected final void injectDynamicView(String newState, AbstractEditorView newView, String hostID, WidgetGroup rootTable, Skin skin) {
        Container host = ((Container) rootTable.findActor(hostID));
        AbstractEditorView oldView = (AbstractEditorView) host.getActor();
        
        if (oldView == newView && !(newView instanceof ForceReactivationEditorView)) {
            return;
        }

        if (oldView != null) {
            oldView.deactivate();
            host.removeActor(oldView);
        }

        if (newView != null) {
            newView.activate(skin, newState);
            host.setActor(newView);
        }
    }
}
