package com.dodles.gdx.dodleengine.brushes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.DrawEraserCommand;
import com.dodles.gdx.dodleengine.commands.DrawStrokeCommand;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.Shape.RenderMode;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;

import javax.inject.Inject;

/**
 * Eraser brush.
 */
@PerDodleEngine
public class EraserBrush extends AbstractLineBrush {
    public static final String BRUSH_NAME = "erasebrush";
    private final CommandFactory commandFactory;
    private EditorState editorState;

    @Inject
    public EraserBrush(AssetProvider assetProvider, EditorState editorState, BrushRegistry brushRegistry, CommandFactory commandFactory) {
        super(assetProvider, commandFactory);
        this.commandFactory = commandFactory;
        this.editorState = editorState;
        brushRegistry.registerBrush(this);
    }

    @Override
    public final String getName() {
        return BRUSH_NAME;
    }

    @Override
    public final int getOrder() {
        return 8;
    }

    @Override
    public final void init(Shape shape, RulerMode newRulerMode) {
        shape.setRenderMode(RenderMode.DIRECT);
        super.init(shape, newRulerMode);
    }

    @Override
    public final TextureRegion getActiveIcon() {
        return new TextureRegion(getBrushIconsTexture(), 418, 2, 103, 102);
    }

    @Override
    public final TextureRegion getInactiveIcon() {
        return new TextureRegion(getBrushIconsTexture(), 524, 1, 101, 103);
    }

    @Override
    public final DrawStrokeCommand createCommand() {
        return (DrawEraserCommand) commandFactory.createCommand(DrawEraserCommand.COMMAND_NAME);
    }

    @Override
    protected final StrokeConfig getDefaultStrokeConfig() {
        editorState.copyCurrentStrokeConfig();
        StrokeConfig dsg = new StrokeConfig();
        dsg.setColor(new Color(1f, 1f, 1f, 1f));
        dsg.setSize(15);
        return dsg;
    }
}

