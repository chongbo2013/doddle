package com.dodles.gdx.dodleengine.tools.scene;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;

/**
 * Row that allows management of the scenes in the dodle.
 */
public class SceneManagementFullEditorRowTwo extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final EngineEventManager eventManager;
    private final ObjectManager objectManager;
    private final SceneUIListPanel sceneUIListPanel;

    private Table rootTable;
    private Skin skin;

    @Inject
    public SceneManagementFullEditorRowTwo(AssetProvider assetProvider, CommandFactory commandFactory, CommandManager commandManager, EngineEventManager eventManager, ObjectManager objectManager, SceneUIListPanel sceneUIListPanel) {
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.eventManager = eventManager;
        this.objectManager = objectManager;
        this.sceneUIListPanel = sceneUIListPanel;
    }

    @Override
    public final void activate(Skin newSkin, String newState) {
        this.skin = newSkin;
        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                    .skin(skin)
                    .build();

            String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_SCENE_MANAGEMENT_ROW2);
            rootTable = (Table) parser.parseTemplate(template).get(0);


            Button listButton = rootTable.findActor("listScenes");
            listButton.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!sceneUIListPanel.isPushedOkStack()) {
                        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, SceneUIListPanel.STATE_NAME, "SceneList");
                    }
                }
            });

            Button layersButton = rootTable.findActor("listLayers");
            layersButton.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!sceneUIListPanel.isPushedOkStack()) {
                        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, SceneUIListPanel.STATE_NAME, "LayerList");
                    }
                }
            });


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
}
