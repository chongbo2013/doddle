package com.dodles.gdx.dodleengine.tools.animation.effect;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;

/**
 * Core functionality for effect panels.
 */
public abstract class AbstractEffectPanel {
    private final DodleStageManager stageManager;
    private final EngineEventManager eventManager;
    
    private Table panel;
    private boolean enabled;
    
    public AbstractEffectPanel(DodleStageManager stageManager, EngineEventManager eventManager) {
        this.stageManager = stageManager;
        this.eventManager = eventManager;
    }
    
    /**
     * Base initialization for the panel table, returns the table if this is the first time it has been initialized.
     */
    protected final Table baseInitialize(Stack dodleOverlayStack, Skin skin) {        
        if (panel == null) {
            Table panelHost = new Table();
            panelHost.setFillParent(true);
            dodleOverlayStack.add(panelHost);
            
            panel = new Table(skin);
            panel.setBackground(FullEditorViewState.TOOLBAR_MIDDLE_ACTIVATED_COLOR);
            panel.setVisible(false);
            panelHost.add(panel).expand().fillY().width(FullEditorInterface.getInterfaceRowSize() * 1).align(Align.left);
            
            return panel;
        }
        
        return null;
    }
    
    /**
     * Slides the panel in or out.
     */
    protected final void slidePanel(boolean wantEnabled) {
        if (wantEnabled != enabled) {
            float startX, endX;
        
            if (wantEnabled) {
                startX = -panel.getWidth();
                endX = 0;
                stageManager.setDisplayMode(DodleStageManager.DisplayMode.SHOW_OBJECT_OUTLINE);
            } else {
                startX = 0;
                endX = -panel.getWidth();
            }

            panel.setX(startX);
            panel.setVisible(true);
            Action animation = Actions.sequence(Actions.moveTo(endX, panel.getY(), 0.25f), Actions.visible(wantEnabled));
            panel.addAction(animation);
            enabled = wantEnabled;
        }
    }
}
