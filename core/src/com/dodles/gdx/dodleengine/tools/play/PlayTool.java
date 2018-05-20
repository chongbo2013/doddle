package com.dodles.gdx.dodleengine.tools.play;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The "Play" tool handles changing the state of the dodleEngine to animate.
 */
@PerDodleEngine
public class PlayTool extends AbstractTool implements Tool {
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "PLAY";
    public static final String ACTIVATED_COLOR = FullEditorViewState.TOOLBAR_BOTTOM_ACTIVATED_COLOR;
    
    @Inject
    public PlayTool(AssetProvider assetProvider, FullEditorViewState fullViewState, ToolRegistry toolRegistry, PlayToolFullEditorOverlay overlay) {
        super(assetProvider);
        
        toolRegistry.registerTool(this);
        fullViewState.registerOverlayView(TOOL_NAME, overlay);
    }
    
    @Override
    public final String getName() {
        return TOOL_NAME;
    }

    @Override
    public final String getActivatedColor() {
        return ACTIVATED_COLOR;
    };

    @Override
    public final int getRow() {
        return 3;
    }

    @Override
    public final int getOrder() {
        return 4;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 357, 75, 69, 69);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        return getToolBarIconsTextureAtlas().findRegion("play_" + i);
    }

    @Override
    public final String getButtonStyleName() {
        return "play";
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return new ArrayList<InputHandler>();
    }
    
    @Override
    public void onActivation() {
    }

    @Override
    public void onDeactivation() {
    }
}
