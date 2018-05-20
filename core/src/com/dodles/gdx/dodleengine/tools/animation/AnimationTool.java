package com.dodles.gdx.dodleengine.tools.animation;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.CameraManager;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.ObjectManager.SceneData;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.Block;
import com.dodles.gdx.dodleengine.animation.PlaybackSettings;
import com.dodles.gdx.dodleengine.animation.SceneAnimation;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager.DisplayMode;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.subtools.AbstractAnimationSubtool;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The "Animation" tool handles adding animations to objects on the canvas.
 */
@PerDodleEngine
public class AnimationTool extends AbstractTool implements Tool {

    //region Properties & Variables

    //Constants
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "ANIMATION";
    public static final String ACTIVATED_COLOR = FullEditorViewState.TOOLBAR_MIDDLE_ACTIVATED_COLOR;

    // Subsystem References
    private final AnimationManager animationManager;
    private final AnimationSubtoolRegistry animationSubtoolRegistry;
    private final CameraManager cameraManager;
    private final DodleStageManager dodleStageManager;
    private final EventBus eventBus;
    private final ObjectManager objectManager;
    private final PlaybackSettings playbackSettings;
    private final ToolRegistry toolRegistry;

    // State Variables
    private AnimationSubtoolState currentSubtoolState = AnimationSubtoolState.DISABLED;
    private AbstractAnimationSubtool currentSubtool = null;
    private boolean activatingEffectsMenu = false;

    // Current Animation Data
    private SceneAnimation sceneAnimation;
    private ArrayList<Block> blockChain = new ArrayList<Block>();
    private Block selectedBlock;

    //endregion Properties and Variables

    //region Constructor
    @Inject
    public AnimationTool(
        // Subsystems
        final AnimationManager animationManager,
        final AnimationSubtoolRegistry animationSubtoolRegistry,
        final AssetProvider assetProvider,
        final CameraManager cameraManager,
        final DodleStageManager dodleStageManager,
        final EventBus eventBus,
        final ObjectManager objectManager,
        final PlaybackSettings playbackSettings,
        final ToolRegistry toolRegistry
    ) {
        // Super
        super(assetProvider);

        // Sub-system References
        this.animationManager = animationManager;
        this.animationSubtoolRegistry = animationSubtoolRegistry;
        this.cameraManager = cameraManager;
        this.dodleStageManager = dodleStageManager;
        this.eventBus = eventBus;
        this.objectManager = objectManager;
        this.playbackSettings = playbackSettings;
        this.toolRegistry = toolRegistry;

        // Event Listeners
        this.eventBus.addSubscriber(new EventSubscriber(EventTopic.EDITOR) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                System.out.println("AnimationTool::eventType: " + eventType + ", data: " + data.getFirstStringParam());
                switch (eventType) {
                    case ANIMATION_START:
                        startAnimation();
                        break;
                    case ANIMATION_STOP:
                        stopAnimation();
                        break;
                    case SELECTED_ACTOR_EFFECTS_BUTTON_PRESSED:
                        activatingEffectsMenu = true;
                        if (isActive()) {
                            setToolState(AnimationSubtoolState.EFFECT_SELECT);
                        } else {
                            toolRegistry.setActiveTool(TOOL_NAME);
                        }
                        activatingEffectsMenu = false;
                        break;
                    case SET_ANIMATION_TOOL_STATE:
                        String subtoolStateStr = data.getFirstStringParam();
                        AnimationSubtoolState newState = AnimationSubtoolState.valueOf(subtoolStateStr);
                        setToolState(newState);
                        break;
                    case ANIM_CHANGE_SELECTED_BLOCK:
                        setSelectedBlock((data == null) ? null : data.getFirstStringParam());
                        break;
                    case ANIM_PLAYHEAD_POSITION_CHANGED:
                        SetPlayheadPosition(Float.parseFloat(data.getFirstStringParam()));
                        break;
                }
            }
        });

        // Register Tool
        toolRegistry.registerTool(this);
    }

    //endregion Constructor

    //region Tool UI Related Functions - TODO: refactor Tool class & remove! - CAD 2017.09.14

    @Override
    public final String getName() {
        return TOOL_NAME;
    }

    @Override
    public final String getActivatedColor() {
        return ACTIVATED_COLOR;
    }

    @Override
    public final int getRow() {
        return 2;
    }

    @Override
    public final int getOrder() {
        return 3;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 430, 3, 69, 69);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        return getToolBarIconsTextureAtlas().findRegion("animation_" + i);
    }

    @Override
    public final String getButtonStyleName() {
        return "animation";
    }

    //endregion Tool UI Related Functions

    //region Public API

    @Override
    public final void onActivation() {
        System.out.println("AnimationTool::onActivation");

        // Reset Internal State Variables
        sceneAnimation = animationManager.getSceneAnimation(objectManager.getScene().getName());
        blockChain.clear();
        blockChain.add(sceneAnimation.getRootBlock());

        // Set Subtool depending on activation state
        if (activatingEffectsMenu) {
            setToolState(AnimationSubtoolState.EFFECT_SELECT);
        } else {
            setToolState(AnimationSubtoolState.TIMELINE);
        }

        // If an object is selected, and appears selected, then keep it selected. Otherwise, clear it
        if (dodleStageManager.getDisplayMode() != DisplayMode.SHOW_OBJECT_MANIPULATION_OVERLAY) {
            objectManager.clearSelectedActors();
            dodleStageManager.setDisplayMode(DisplayMode.SHOW_OBJECT_MANIPULATION_OVERLAY);
        }
    }

    @Override
    public final void onDeactivation() {
        System.out.println("AnimationTool::onDeactivation");

        // Deactivate current subtool
        setToolState(AnimationSubtoolState.DISABLED);

        // Reset all scenes to base transform...
        animationManager.endAnimation();
        for (SceneData data : objectManager.allSceneData()) {
            SceneAnimation sa = animationManager.getSceneAnimation(data.getScene().getName());
            sa.getRootBlock().resetToBaseTransform();
        }

        dodleStageManager.setDisplayMode();
        playbackSettings.setPlaySpeed(1);
        activatingEffectsMenu = false;
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        List<InputHandler> inputHandlers = null;
        if (currentSubtool != null) {
            inputHandlers = currentSubtool.getInputHandlers();
        }
        return inputHandlers;
    }

    public final boolean isActive() {
        boolean _isActive = toolRegistry.getActiveTool().getName().equals(this.TOOL_NAME);
        System.out.println("AnimationTool::isActive = " + _isActive);
        return _isActive;
    }

    /**
     * Returns the active scene animation.
     */
    public final SceneAnimation getScene() {
        System.out.println("AnimationTool::getScene");
        return sceneAnimation;
    }

    /**
     * Returns the active block chain
     */
    @Deprecated
    public final List<Block> getActiveBlockChain() {
        System.out.println("AnimationTool::getActiveBlockChain has been deprecated");
        return Collections.unmodifiableList(blockChain);
    }

    /**
     * Returns the active block.
     */
    public final Block getActiveBlock() {
        System.out.println("AnimationTool::getActiveBlock");
        return blockChain.get(blockChain.size() - 1);
    }

    /**
     * Sets the active block being modified in the animation tool.
     */
    public final void setSelectedBlock(String blockID) {
        Block block = sceneAnimation.getRootBlock().findBlock(blockID);
        if (block == null && blockID != null) {
            System.out.println("AnimationTool::setSelectedBlock - could not find block '" + blockID + "'");
        }
        setSelectedBlock(block);
    }

    public final void setSelectedBlock(Block block) {
        Block mainBlock = block;
        System.out.println("AnimationTool::setActiveBlock " + (block != null ? block.getBlockId() : "null"));
        blockChain.clear();
        blockChain.add(block);

        while (block.getParentBlock() != null) {
            block = block.getParentBlock();
            blockChain.add(0, block);
        }

        if (selectedBlock != mainBlock) {
            selectedBlock = mainBlock;
            if (selectedBlock != null) {
                eventBus.publish(EventTopic.DEFAULT, EventType.ANIM_SELECTED_BLOCK_CHANGED, selectedBlock.getBlockId());
            } else {
                eventBus.publish(EventTopic.DEFAULT, EventType.ANIM_SELECTED_BLOCK_CHANGED);
            }
        }
    }

    public AnimationSubtoolState getToolState() {
        return currentSubtoolState;
    }

    /**
     * Returns the appropriate display mode to use for the current state inside of the animation tool.
     */
    public static DisplayMode getDisplayModeForState(FullEditorViewState state) {
        System.out.println("AnimationTool::getDisplayModeForState");
        if (state.getState().equals(TOOL_NAME)) {
            return DisplayMode.SHOW_OBJECT_MANIPULATION_OVERLAY;
        }

        return DisplayMode.SHOW_OBJECT_OUTLINE;
    }

    //endregion Public API


    //region Private Helper Functions

    private void setToolState(AnimationSubtoolState newSubtoolState) {
        if (this.isActive()) {
            AbstractAnimationSubtool newSubtool = animationSubtoolRegistry.getSubtool(newSubtoolState);
            if (newSubtool != currentSubtool) {
                // Pre-Activate New Subtool
                if (newSubtool != null) {
                    newSubtool.onPreActivation(currentSubtoolState, currentSubtool);
                }
                // Deactivate Previous Subtool
                if (currentSubtool != null) {
                    currentSubtool.onDeactivation(newSubtoolState);
                }
                // Switch Subtool
                AnimationSubtoolState previousState = currentSubtoolState;
                currentSubtoolState = newSubtoolState;
                currentSubtool = newSubtool;
                // Activate New Subtool
                if (currentSubtool != null) {
                    currentSubtool.onActivation(previousState);
                }
                // Send Event
                eventBus.publish(
                        EventTopic.DEFAULT,
                        EventType.ANIMATION_TOOL_STATE_CHANGED,
                        currentSubtoolState.toString());
            } else {
                System.err.println("AnimationTool::setToolState - attempting to set state to '" + newSubtoolState + "' while state is already '" + newSubtoolState + "'");
            }
        } else {
            System.err.println("AnimationTool::setToolState - attempting to set state to '" + newSubtoolState + "' while animation tool is not active.");
        }
    }

    private void startAnimation() {
        cameraManager.resetGlobalViewport();
        dodleStageManager.setDisplayMode(DodleStageManager.DisplayMode.ANIMATION);
        animationManager.startAnimation(getScene().getSceneID(), getActiveBlock().getBlockId());
    }

    private void stopAnimation() {
        animationManager.stopAnimation();
        dodleStageManager.setDisplayMode();
    }

    private void SetPlayheadPosition(float time) {
        animationManager.endAnimation();
        getActiveBlock().resetToBaseTransform();
        animationManager.startAnimation(
                getScene().getSceneID(),
                getActiveBlock().getBlockId(),
                time);
        animationManager.stopAnimation();
        dodleStageManager.updateStateUi();
    }

    //endregion Private Helper Functions
}
