package com.dodles.gdx.dodleengine.tools.play;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.CameraManager;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;
import javax.inject.Inject;

/**
 * Overlay for the play tool in the full editor.
 */
@PerDodleEngine
public class PlayToolFullEditorOverlay extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final CameraManager cameraManager;
    private final ToolRegistry toolRegistry;
    private final OkCancelStackManager okCancelStack;
    private final AnimationManager animationManager;
    private final ObjectManager objectManager;
    private final DodleStageManager stageManager;

    private Table rootTable;

    @Inject
    public PlayToolFullEditorOverlay(
        AssetProvider assetProvider,
        CameraManager cameraManager,
        ToolRegistry toolRegistry,
        OkCancelStackManager okCancelStack,
        AnimationManager animationManager,
        ObjectManager objectManager,
        DodleStageManager stageManager
    ) {
        this.assetProvider = assetProvider;
        this.cameraManager = cameraManager;
        this.toolRegistry = toolRegistry;
        this.okCancelStack = okCancelStack;
        this.animationManager = animationManager;
        this.objectManager = objectManager;
        this.stageManager = stageManager;
    }

    @Override
    public final void activate(Skin skin, String newState) {

        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                    .skin(skin)
                    .build();

            rootTable = (Table) parser.parseTemplate(assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_PLAY_OVERLAY)).get(0);
            rootTable.setBackground(skin.getDrawable(toolRegistry.getActiveTool().getActivatedColor()));

            Button exitButton = rootTable.findActor("exitButton");

            exitButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    animationManager.endAnimation();
                    stageManager.setDisplayMode();
                    toolRegistry.setActiveTool(null);
                }
            });

            this.addActor(rootTable);
        }

        cameraManager.resetGlobalViewport();
        stageManager.setDisplayMode(DodleStageManager.DisplayMode.ANIMATION);
        animationManager.startAnimation();
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
}
