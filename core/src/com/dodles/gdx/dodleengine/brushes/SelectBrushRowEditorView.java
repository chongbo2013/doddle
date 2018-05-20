package com.dodles.gdx.dodleengine.brushes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.EditorStateManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Full editor UI Row that displays all the available brushes for selection.
 */
@PerDodleEngine
public class SelectBrushRowEditorView extends AbstractEditorView {
    private final BrushRegistry brushRegistry;
    private final EditorStateManager editorStateManager;
    private final ArrayList<Button> allButtons = new ArrayList<Button>();
    private boolean togglingButtons = false;
    private Table rootTable;
    
    @Inject
    public SelectBrushRowEditorView(BrushRegistry brushRegistry, EditorStateManager editorStateManager) {
        this.brushRegistry = brushRegistry;
        this.editorStateManager = editorStateManager;
    }
    
    @Override
    public final void activate(Skin skin, String newState) {
        if (rootTable == null) {
            rootTable = new Table();
            rootTable.setFillParent(true);
            for (Brush brush : brushRegistry.getBrushes()) {
                allButtons.add(configureBrushButton(brush));
            }
            this.addActor(rootTable);
        }
    }


    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }


    public final void resetAllBrushStates()
    {   rootTable.clear();
        for (Brush brush : brushRegistry.getBrushes()) {
            allButtons.add(configureBrushButton(brush));
        }
    }

    private Button configureBrushButton(final Brush brush) {
        TextureRegionDrawable activeImage = new TextureRegionDrawable(brush.getActiveIcon());
        TextureRegionDrawable inactiveImage = new TextureRegionDrawable(brush.getInactiveIcon());
        final Button button = new Button(inactiveImage, activeImage, activeImage);
        
        if (brushRegistry.getActiveBrush() == brush) {
            button.setChecked(true);
        }

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (!togglingButtons) {
                    togglingButtons = true;
                    
                    for (Button curButton : allButtons) {
                        if (curButton != button) {
                            curButton.setChecked(false);
                        }
                    }

                    editorStateManager.onBrushChange(brush.getName());

                    togglingButtons = false;
                }
            }
        });

        rootTable.add(button).size(FullEditorInterface.getInterfaceRowSize(), FullEditorInterface.getInterfaceRowSize()).expandX();
        return button;
    }
}
