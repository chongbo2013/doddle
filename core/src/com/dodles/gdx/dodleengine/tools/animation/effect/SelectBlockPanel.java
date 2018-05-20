package com.dodles.gdx.dodleengine.tools.animation.effect;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.Block;
import com.dodles.gdx.dodleengine.animation.TouchBlockTrigger;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.animation.AddBlockCommand;
import com.dodles.gdx.dodleengine.editor.DensityManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlay;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlayRegistry;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.dodles.gdx.dodleengine.util.DialogUtility;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import com.kotcrab.vis.ui.widget.Separator;

import javax.inject.Inject;

/**
 * Panel for selecting blocks to work with.
 */
@PerDodleEngine
public class SelectBlockPanel implements FullEditorDodleOverlay {
    public static final String PANEL_NAME = "SelectBlock";
    public static final String STATE_NAME = AnimationTool.TOOL_NAME + "." + PANEL_NAME;

    private final AnimationManager animationManager;
    private final AnimationTool animationTool;
    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final DialogUtility dialogUtility;
    private final EngineEventManager eventManager;
    private final ObjectManager objectManager;
    private final OkCancelStackManager okCancelStack;
    
    private Table panel;
    private Skin skin;
    private Stack dodleOverlayStack;
    private boolean enabled;
    private TextureAtlas animationAtlas;
    private int blockCount = 1; // TODO: this will definitely need to be smarter
    
    @Inject
    public SelectBlockPanel(
        AnimationManager animationManager,
        AnimationTool animationTool,
        AssetProvider assetProvider,
        CommandFactory commandFactory,
        CommandManager commandManager,
        DialogUtility dialogUtility,
        EngineEventManager eventManager,
        FullEditorDodleOverlayRegistry fedoRegistry,
        ObjectManager objectManager,
        OkCancelStackManager okCancelStack
    ) {
        this.animationManager = animationManager;
        this.animationTool = animationTool;
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.dialogUtility = dialogUtility;
        this.eventManager = eventManager;
        this.objectManager = objectManager;
        this.okCancelStack = okCancelStack;
        
        fedoRegistry.registerOverlay(this);
    }
    
    @Override
    public final String getName() {
        return PANEL_NAME;
    }

    @Override
    public final void initialize(Stack pDodleOverlayStack, Skin pSkin) {
        skin = pSkin;
        
        if (panel == null) {
            animationAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_ANIMATIONICONS_ATLAS);
            
            dodleOverlayStack = pDodleOverlayStack;
            
            Table panelHost = new Table();
            panelHost.setFillParent(true);
            dodleOverlayStack.add(panelHost);
            
            panel = new Table(skin);
            panel.setBackground(FullEditorViewState.TOOLBAR_MIDDLE_ACTIVATED_COLOR);
            panel.setVisible(false);
            panelHost.add(panel).expand().fillY().width(FullEditorInterface.getInterfaceRowSize() * 3).align(Align.right);
            
            eventManager.addListener(new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
                @Override
                public void listen(EngineEventData data) {
                    generateBlockList();
                    
                    String state = data.getFirstStringParam();
                    slidePanel(state != null && state.indexOf(STATE_NAME) == 0);
                }
            });
        }
        
        generateBlockList();
    }
    
    private void generateBlockList() {
        panel.clear();

        if (!animationTool.isActive()) {
            return;
        }
        
        for (final Block block : animationTool.getActiveBlockChain()) {
            createFolderButton(block);
        }
        
        panel.add(new Separator()).height(DensityManager.getScale() * 3).expandX().fillX().pad(DensityManager.getScale() * 5, 0, DensityManager.getScale() * 5, 0).row();
        
        for (final Block block : animationTool.getActiveBlock().getChildBlocks()) {
            createFolderButton(block);
        }
        
        TextButton newBlock = new TextButton("New", skin);
        
        newBlock.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AddBlockCommand command = (AddBlockCommand) commandFactory.createCommand(AddBlockCommand.COMMAND_NAME);
                command.init(animationTool.getScene(), animationTool.getActiveBlock(), blockCount++ + "");
                command.execute();
                commandManager.add(command);
                generateBlockList();
            }
        });
        
        panel.add(newBlock).row();
        
        panel.add(new Table()).expandY().fillY().row();
    }
    
    private void createFolderButton(final Block block) {
        String displayName = block.getDisplayName();
        
        if (block.getTrigger() != null) {
            displayName += " *";
        }
        
        Table icon = LmlUtility.createImageTextButton(animationAtlas, skin, "folder", "folder", displayName, "medium", 1f);
        panel.add(icon).row();

        icon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBlockClick(block);
            }
        });
    }
    
    private void handleBlockClick(final Block block) {
        System.err.println("SelectBlockPanel::handleBlockClick - not implemented correctly. Depends on old style of animation tool object selection");
        if (animationTool.getActiveBlock() == block && animationTool.getScene().getRootBlock() != block) {
            dialogUtility.alert("Select Trigger Object", "Pick the object that you want to trigger this\nblock and then click the ok button.");
            //animationTool.setObjectSelectionAllowed(true);
            
            okCancelStack.push(new Runnable() {
                @Override
                public void run() {
                    //animationTool.setObjectSelectionAllowed(false);
                    
                    if (objectManager.getSelectedActor() != null) {
                        block.setTrigger(new TouchBlockTrigger(objectManager.getSelectedActor()));
                    }
                    
                    generateBlockList();
                }
            }, new Runnable() {
                @Override
                public void run() {
                    //animationTool.setObjectSelectionAllowed(false);
                    generateBlockList();
                }
            });
        } else {
            animationTool.setSelectedBlock(block);
            generateBlockList();
        }
    }
    
    /**
     * Slides the panel in or out.
     */
    private void slidePanel(boolean wantEnabled) {
        if (wantEnabled != enabled) {
            float startX, endX;
        
            if (wantEnabled) {
                startX = dodleOverlayStack.getWidth();
                endX = dodleOverlayStack.getWidth() - panel.getWidth();
            } else {
                startX = dodleOverlayStack.getWidth() - panel.getWidth();
                endX = dodleOverlayStack.getWidth();
            }

            panel.setX(startX);
            panel.setVisible(true);
            Action animation = Actions.sequence(Actions.moveTo(endX, panel.getY(), 0.25f), Actions.visible(wantEnabled));
            panel.addAction(animation);
            enabled = wantEnabled;
        }
    }
}
