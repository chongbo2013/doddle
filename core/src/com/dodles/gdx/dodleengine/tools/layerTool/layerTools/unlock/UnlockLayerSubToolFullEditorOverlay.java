package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.unlock;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.LockCommand;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Overlay that manages stroke size.
 */
public class UnlockLayerSubToolFullEditorOverlay extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final OkCancelStackManager okCancelStack;
    private final EngineEventManager eventManager;
    private final ObjectManager objectManager;
    private final CommandManager commandManager;
    private final CommandFactory commandFactory;
    private final DodleStageManager stageManager;

    @Inject
    public UnlockLayerSubToolFullEditorOverlay(AssetProvider assetProvider, OkCancelStackManager okCancelStack, EngineEventManager eventManager, ObjectManager objectManager, CommandManager commandManager, CommandFactory commandFactory, DodleStageManager stageManager) {
        this.assetProvider = assetProvider;
        this.okCancelStack = okCancelStack;
        this.eventManager = eventManager;
        this.objectManager = objectManager;
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
        this.stageManager = stageManager;
    }

    private Table rootTable;
    private Skin skin;

    @Override
    public final void activate(Skin pSkin, String newState) {
        skin = pSkin;
        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                    .skin(skin)
                    .build();

            rootTable = (Table) parser.parseTemplate(assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_EMPTY_OVERLAY)).get(0);

            int buttonSize = FullEditorInterface.getInterfaceRowSize();

            Texture icons = assetProvider.getTexture(TextureAssets.EDITOR_LAYER_TOOL_TOOLBARICONS);
            TextureRegion icon = new TextureRegion(icons, 192, 51, 44, 44);

            Button button = new Button(new TextureRegionDrawable(icon));
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ArrayList<String> lockedIDs = objectManager.getLockedActorIDs();

                    if (lockedIDs.size() > 0) {

                        LockCommand command = (LockCommand) commandFactory.createCommand(LockCommand.COMMAND_NAME);

                        command.init(lockedIDs, false);
                        command.execute();

                        commandManager.add(command);

                        objectManager.clearSelectedActors();

                        stageManager.updateStateUi();
                    }

                    okCancelStack.pop(true);

                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, LayerTool.TOOL_NAME);
                }
            });

            rootTable.add(button).expandX().size(buttonSize);

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
