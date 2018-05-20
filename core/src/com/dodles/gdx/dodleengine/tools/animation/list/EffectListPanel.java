package com.dodles.gdx.dodleengine.tools.animation.list;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.EffectParameterType;
import com.dodles.gdx.dodleengine.animation.EffectTiming;
import com.dodles.gdx.dodleengine.animation.TimelineInfo;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.animation.UpdateBlockEffectsCommand;
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
import com.dodles.gdx.dodleengine.tools.animation.BlockingDraggable;
import com.dodles.gdx.dodleengine.tools.animation.DodlesDragListener;
import com.dodles.gdx.dodleengine.tools.animation.effect.ConfigureEffectOverlay;
import com.dodles.gdx.dodleengine.tools.animation.effect.ConfigureEffectOverlay.EffectConfigurationInterfaceInfo;
import com.dodles.gdx.dodleengine.tools.animation.main.TimelineWidget;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import com.dodles.gdx.dodleengine.util.NumberFormatter;
import com.kotcrab.vis.ui.layout.DragPane;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.Separator;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Panel that lists the effects for the active block.
 */
@PerDodleEngine
public class EffectListPanel implements FullEditorDodleOverlay {
    private static final String VIEW_MORE = "View More";
    private static final String VIEW_LESS = "View Less";
    
    public static final String PANEL_NAME = "EffectList";
    public static final String STATE_NAME = AnimationTool.TOOL_NAME + "." + PANEL_NAME;
    
    private final AnimationManager animationManager;
    private final AnimationTool animationTool;
    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final EngineEventManager eventManager;
    private final NumberFormatter numberFormatter;
    private final ObjectManager objectManager;
    private final OkCancelStackManager okCancelStackManager;
    private final TimelineWidget timelineWidget;
    
    private Table rootTable;
    private Table scrollContent;
    private Skin skin;
    private boolean enabled;
    private TextureAtlas animationIconsAtlas;
    private boolean pushedOkStack = false;
    private boolean showAll = false;
    private EffectConfigurationInterfaceInfo activeInterfaceInfo;
    
    @Inject
    public EffectListPanel(
        AnimationManager animationManager,
        AnimationTool animationTool,
        AssetProvider assetProvider,
        CommandFactory commandFactory,
        CommandManager commandManager,
        EngineEventManager eventManager,
        FullEditorDodleOverlayRegistry fedoRegistry,
        NumberFormatter numberFormatter,
        ObjectManager objectManager,
        OkCancelStackManager okCancelStackManager,
        TimelineWidget timelineWidget
    ) {
        this.animationManager = animationManager;
        this.animationTool = animationTool;
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.eventManager = eventManager;
        this.numberFormatter = numberFormatter;
        this.objectManager = objectManager;
        this.okCancelStackManager = okCancelStackManager;
        this.timelineWidget = timelineWidget;
        
        fedoRegistry.registerOverlay(this);
    }
    
    @Override
    public final String getName() {
        return PANEL_NAME;
    }

    @Override
    public final void initialize(Stack dodleOverlayStack, Skin pSkin) {
        skin = pSkin;
        
        if (rootTable == null) {
            animationIconsAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_ANIMATIONICONS_ATLAS);
            
            rootTable = FullEditorInterface.getScrollableOverlay(assetProvider, skin, true);
            rootTable.setBackground(FullEditorViewState.TOOLBAR_MIDDLE_ACTIVATED_COLOR);
            rootTable.setVisible(false);
            dodleOverlayStack.add(rootTable);
            
            scrollContent = rootTable.findActor("scrollContent");
            
            eventManager.addListener(new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
                @Override
                public void listen(EngineEventData data) {
                    String state = data.getFirstStringParam();
                    if (state != null && state.indexOf(STATE_NAME) == 0) {
                        if (slidePanel(true)) {
                            final UpdateBlockEffectsCommand ubeCommand = (UpdateBlockEffectsCommand) commandFactory.createCommand(UpdateBlockEffectsCommand.COMMAND_NAME);
                            ubeCommand.beginExecuteLive(
                                    objectManager.getScene().getName(),
                                    animationTool.getActiveBlock().getBlockId());

                            showAll = false;
                            updateList();

                            okCancelStackManager.push(new Runnable() {
                                @Override
                                public void run() {
                                    ubeCommand.finishExecuteLive();
                                    commandManager.add(ubeCommand);
                                    pushedOkStack = false;
                                    timelineWidget.setActiveEffect(null);

                                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, AnimationTool.TOOL_NAME);
                                }
                            }, new Runnable() {
                                @Override
                                public void run() {
                                    ubeCommand.undo();
                                    timelineWidget.setActiveEffect(null);

                                    if (pushedOkStack) {
                                        pushedOkStack = false;
                                        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, AnimationTool.TOOL_NAME);
                                    }
                                }
                            });

                            pushedOkStack = true;
                        }
                    } else {
                        slidePanel(false);
                        
                        if (pushedOkStack) {
                            pushedOkStack = false;
                            okCancelStackManager.popCancel();
                        }
                    }
                }
            });
            
            eventManager.addListener(new EngineEventListener(EngineEventType.ANIMATION_TIMELINE_POSITION_MOVED_BY_USER) {
                @Override
                public void listen(EngineEventData data) {
                    if (enabled && !showAll) {
                        EffectTiming activeEffect = timelineWidget.getActiveEffect();
                        if (activeEffect == null) {
                            updateList();
                        } else {
                            activeInterfaceInfo.getDelaySlider().setValue(activeEffect.getDelay());
                        }
                    }
                }
            });
        }
    }
    
    private void updateList() {
        scrollContent.clear();
        scrollContent.add(new Separator()).height(DensityManager.getScale()).expandX().fillX().row();
        
        TimelineInfo info = new TimelineInfo(animationTool.getScene(), animationTool.getActiveBlock());
        float padSize = DensityManager.getScale() * 10;
        
        Table folderRow = new Table();
        folderRow.add(new Image(new TextureRegionDrawable(animationIconsAtlas.findRegion("folder")), Scaling.fill)).size(FullEditorInterface.getInterfaceRowSize()).padLeft(padSize);
        folderRow.add(new Label("MAIN", skin, "default-black")).expand().padLeft(padSize).align(Align.left);
        scrollContent.add(folderRow).expandX().fillX().height(FullEditorInterface.getInterfaceRowSize()).row();
        
        // Initialize drag pane and make sure that it fills all available horizontal space...
        DragPane dragPane = new DragPane(true);
        dragPane.fillX();
//        dragPane.getVerticalGroup().grow();
        
        final BlockingDraggable draggable = new BlockingDraggable();
        final DodlesDragListener dragListener = new DodlesDragListener();
        draggable.setListener(dragListener);
        dragPane.setDraggable(draggable);
        scrollContent.add(dragPane).expandX().fillX().row();
        
        float curTime = animationManager.getBlockTime(animationTool.getActiveBlock().getBlockId());
        boolean needShowAllButton = false;
        final ArrayList<CollapsibleWidget> allCollapsibles = new ArrayList<CollapsibleWidget>();
        
        for (final EffectTiming timing : animationTool.getActiveBlock().allEffects()) {
            float startTime = timing.calculateStartTime();
            boolean activeEffect = startTime <= curTime && curTime <= startTime + timing.getEffect().getEffectLength();
            startTime += timing.getEffect().getEffectLength();
            needShowAllButton |= !activeEffect;

            if (showAll || activeEffect) {
                final Table effectDraggableRow = new Table();

                effectDraggableRow.add(new Separator()).height(DensityManager.getScale()).expandX().fillX().row();

                Table row = new Table();
                row.setTouchable(Touchable.enabled);

                //if (false) { // drag/drop disabled for now until we have a real use case for it..
                if (showAll) {
                    Image handleImage = new Image(new TextureRegionDrawable(animationIconsAtlas.findRegion("handle")), Scaling.fillY);
                    handleImage.setTouchable(Touchable.enabled);
                    dragListener.addDragHandle(handleImage);
                    row.add(handleImage).size(FullEditorInterface.getInterfaceRowSize()).padLeft(padSize);
                }

                row.add(new Label(timing.getEffect().getEffectDefinition().getDisplayName(), skin, "default-black")).expand().fill().padLeft(padSize);
                row.add(new Label(numberFormatter.decimalFormat("#.#", timing.getEffect().getEffectLength()) + "s", skin, "default-black")).expand().align(Align.right).padRight(padSize);
                effectDraggableRow.add(row).expandX().fillX().height(FullEditorInterface.getInterfaceRowSize()).row();

                float padLeft = padSize * 2 + FullEditorInterface.getInterfaceRowSize();

                Table collapseContent = new Table();
                collapseContent.setFillParent(true);
                final CollapsibleWidget collapseWrapper = new CollapsibleWidget(collapseContent);
                collapseWrapper.setCollapsed(true);
                effectDraggableRow.add(collapseWrapper).expandX().fillX().row();
                final EffectConfigurationInterfaceInfo interfaceInfo = ConfigureEffectOverlay.createEffectConfigurationUi(skin, objectManager, numberFormatter, timing.getEffect().getEffectParameters(), timing.getDelay(), collapseContent, padLeft, padSize);
                allCollapsibles.add(collapseWrapper);

                ImageButton deleteButton = LmlUtility.createButton(animationIconsAtlas, "garbage", 0.8f);
                deleteButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        animationTool.getActiveBlock().removeEffect(timing.getEffect().getEffectID());
                        updateList();
                    }
                });
                collapseContent.add(deleteButton).height(FullEditorInterface.getInterfaceRowSize()).row();

                // Hook to update delay...
                interfaceInfo.getDelaySlider().addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        timing.setDelay(interfaceInfo.getDelaySlider().getValue());
                    }
                });

                // Hooks to update parameters...
                for (final EffectParameterType type : interfaceInfo.getEffectParameterSliders().keySet()) {
                    final Slider curSlider = interfaceInfo.getEffectParameterSliders().get(type);
                    curSlider.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            timing.getEffect().getEffectParameters().put(type, curSlider.getValue());
                        }
                    });
                }

                final Vector2 touchDown = new Vector2(0, 0);

                row.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        touchDown.x = x;
                        touchDown.y = y;

                        return !dragListener.hitDragHandle(effectDraggableRow, x, y);
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        if (touchDown.dst(x, y) < 10) {
                            collapseWrapper.setCollapsed(!collapseWrapper.isCollapsed(), true);

                            if (collapseWrapper.isCollapsed()) {
                                timelineWidget.setActiveEffect(null);
                                activeInterfaceInfo = null;
                            } else {
                                activeInterfaceInfo = interfaceInfo;
                                timelineWidget.setActiveEffect(timing);
                            }

                            for (CollapsibleWidget otherWrapper : allCollapsibles) {
                                if (otherWrapper != collapseWrapper) {
                                    otherWrapper.setCollapsed(true);
                                }
                            }
                        }
                    }
                });

                dragPane.addActor(effectDraggableRow);
            }
        }
        
        scrollContent.add(new Separator()).height(DensityManager.getScale()).expandX().fillX().row();
        
        if (needShowAllButton) {
            final TextButton viewMoreLess = new TextButton(VIEW_MORE, skin);
        
            if (showAll) {
                viewMoreLess.setText(VIEW_LESS);
            }

            viewMoreLess.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showAll = !showAll;
                    updateList();
                }
            });
            scrollContent.add(viewMoreLess).height(FullEditorInterface.getInterfaceRowSize()).row();

            scrollContent.add(new Separator()).height(DensityManager.getScale()).expandX().fillX().row();        
        }
    }
    
    private boolean slidePanel(boolean wantEnabled) {
        if (wantEnabled != enabled) {
            float startY, endY;
        
            if (wantEnabled) {
                startY = -rootTable.getHeight();
                endY = 0;
            } else {
                startY = 0;
                endY = -rootTable.getHeight();
            }

            rootTable.setY(startY);
            rootTable.setVisible(true);
            Action animation = Actions.sequence(Actions.moveTo(rootTable.getX(), endY, 0.25f), Actions.visible(wantEnabled));
            rootTable.addAction(animation);
            enabled = wantEnabled;
            return enabled;
        }
        
        return false;
    }
}
