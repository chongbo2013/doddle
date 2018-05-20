package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.spine;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.tools.layerTool.AbstractLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The "Trash" tool handles removing objects.
 */
@PerDodleEngine
public class SpineLayerSubTool extends AbstractLayerSubTool implements LayerSubTool {
    public static final String TOOL_NAME = LayerTool.TOOL_NAME + ".SPINE";
    public static final String ACTIVATED_COLOR = "tray-background";

    private final ArrayList<InputHandler> inputHandlers = new ArrayList<InputHandler>();
    private final SpineToolFullEditorOverlay overlay;

    @Inject
    public SpineLayerSubTool(
            AssetProvider assetProvider,
            FullEditorViewState fullViewState,
            PanRotateZoomActorInputHandler przaih,
            final SpineToolFullEditorOverlay overlay,
            LayerSubToolRegistry layerSubToolRegistry
    ) {
        super(assetProvider);
        this.overlay = overlay;

        layerSubToolRegistry.registerTool(this);
        fullViewState.registerOverlayView(TOOL_NAME, overlay);

        przaih.initialize(new PanRotateZoomActorInputHandler.ActorProvider() {
            @Override
            public DodlesActor getActor() {
                return overlay.getActiveSpine();
            }
        });
        inputHandlers.add(przaih);
    }

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
        return 1;
    }

    @Override
    public final int getOrder() {
        return 5;
    }

    @Override
    public final TextureRegion getIcon() {
        //return new TextureRegion(getToolBarIconsTexture(), 241, 95, 77, 79);
        return null;
    }

    @Override
    public final String getButtonStyleName() {
        return null;
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        //return getToolBarIconsTextureAtlas().findRegion("save",i);
        return null;
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return inputHandlers;
    }

    @Override
    public final void onActivation() {
        Phase phase = overlay.getActivePhase();

        if (phase != null) {
            if (phase.getSpine() != null) {
                phase.setDisplayMode(Phase.DisplayMode.SPINE_OUTLINE);
            } else {
                phase.setDisplayMode(Phase.DisplayMode.SOURCE);
            }
        }
    }

    @Override
    public final void onDeactivation() {
        Phase phase = overlay.getActivePhase();
        if (phase != null) {
            if (phase.getSpine() != null) {
                phase.setDisplayMode(Phase.DisplayMode.SPINE_FINAL);
            } else {
                phase.setDisplayMode(Phase.DisplayMode.SOURCE);
            }
        }
    }
}
