package com.dodles.gdx.dodleengine.tools.animation.splice;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.tools.animation.main.TimelineWidget;
import javax.inject.Inject;

/**
 * Displays the splice options.
 */
@PerDodleEngine
public class SpliceOptionsFullEditorRow extends AbstractEditorView {
    private final TimelineWidget timelineWidget;
    
    private Table rootTable;
    private TextButton addTime;
    private ClickListener addTimeClickListener;
    
    @Inject
    public SpliceOptionsFullEditorRow(TimelineWidget timelineWidget) {
        this.timelineWidget = timelineWidget;
    }
    
    @Override
    public final void activate(Skin skin, String newState) {
        if (rootTable == null) {
            rootTable = new Table();
            rootTable.setFillParent(true);
            
            addTime = new TextButton("Add Time", skin, "toggle");
            addTimeClickListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    timelineWidget.setSpliceMode(addTime.isChecked());
                }
            };
            addTime.addListener(addTimeClickListener);
            
            TextButton subtractTime = new TextButton("Subtract Time", skin, "toggle");
            subtractTime.addListener(addTimeClickListener);
            
            rootTable.add(addTime).expand();
            rootTable.add(subtractTime).expand();
            
            ButtonGroup bg = new ButtonGroup(addTime, subtractTime);
            
            this.addActor(rootTable);
        }
        
        addTime.setChecked(true);
        addTimeClickListener.clicked(null, 0, 0);
    }

    @Override
    public final void deactivate() {
    }
    
    /**
     * Returns whether we're in add time or remove time mode.
     */
    public final boolean isAddTime() {
        return addTime.isChecked();
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
}
