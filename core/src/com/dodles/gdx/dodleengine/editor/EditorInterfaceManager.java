package com.dodles.gdx.dodleengine.editor;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.DodleEngineConfig;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.StageRenderer;
import com.dodles.gdx.dodleengine.editor.inline.InlineEditorInterface;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Manages communication between editor interfaces and the dodle engine.
 */
@PerDodleEngine
public class EditorInterfaceManager implements StageRenderer {
    private EditorInterface activeInterface = null;
    private DodleEngineConfig engineConfig;
    private Stage stage;
    
    // CHECKSTYLE.OFF: VisibilityModifier - can't be private for injection
    @Inject Lazy<FullEditorInterface> fullEditor;
    @Inject Lazy<InlineEditorInterface> inlineEditor;
    // CHECKSTYLE.ON: VisibilityModifier
    
    @Inject
    public EditorInterfaceManager(DodleEngineConfig engineConfig, EngineEventManager eventManager) {
        this.engineConfig = engineConfig;
        
        eventManager.addListener(new EngineEventListener(EngineEventType.ENGINE_CONFIG_CHANGED) {
            @Override
            public void listen(EngineEventData eventData) {
                updateActiveEditorInterface();
            }
        });
    }
    
    /**
     * Returns the padding taken up by the editor interface on screen.
     */
    public final EditorInterface.Padding getInterfacePadding() {
        if (activeInterface == null) {
            return new EditorInterface.Padding(0, 0, 0, 0);
        }
        
        return activeInterface.getInterfacePadding();
    }
    
    /**
     * Resets the active interface.
     */
    public final void reset() {
        if (activeInterface != null) {
            activeInterface.reset();
        }
    }

    @Override
    public final void initStage(Batch batch, int width, int height) {
        OrthographicCamera camera = new OrthographicCamera();
        
        stage = new Stage(new ScreenViewport(camera), batch);
        stage.setDebugAll(false);
        this.resize(width, height);
    }

    @Override
    public final Stage getStage() {
        return stage;
    }
    
    @Override
    public final boolean actWhenOverloaded() {
        return true;
    }
    
    @Override
    public final void act(float deltaTime) {
        stage.act(deltaTime);
    }

    @Override
    public final void draw() {
        stage.draw();
    }

    @Override
    public final void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);

            // propagate the resize event
            if (activeInterface != null) {
                activeInterface.resize(width, height);
            }
        }
    }
    
    private void updateActiveEditorInterface() {
        if (activeInterface != null) {
            activeInterface.deactivate();
        }

        if (engineConfig.hasOption(DodleEngineConfig.Options.FULL_EDITOR)) {
            activeInterface = fullEditor.get();
            activeInterface.activate(stage);
        } else if (engineConfig.hasOption(DodleEngineConfig.Options.INLINE_EDITOR)) {
            activeInterface = inlineEditor.get();
            activeInterface.activate(stage);
        }
    }
}
