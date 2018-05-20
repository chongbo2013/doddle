package com.dodles.gdx.dodleengine.tools;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.dodles.gdx.dodleengine.tools.chest.ChestTool;
import com.dodles.gdx.dodleengine.tools.crop.CropTool;
import com.dodles.gdx.dodleengine.tools.draw.DrawTool;
import com.dodles.gdx.dodleengine.tools.file.FileTool;
import com.dodles.gdx.dodleengine.tools.font.FontTool;
import com.dodles.gdx.dodleengine.tools.importTool.ImportTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;
import com.dodles.gdx.dodleengine.tools.nullTool.NullTool;
import com.dodles.gdx.dodleengine.tools.play.PlayTool;
import com.dodles.gdx.dodleengine.tools.redo.RedoTool;
import com.dodles.gdx.dodleengine.tools.save.SaveTool;
import com.dodles.gdx.dodleengine.tools.scene.SceneTool;
import com.dodles.gdx.dodleengine.tools.geometry.GeometryTool;
import com.dodles.gdx.dodleengine.tools.share.ShareTool;
import com.dodles.gdx.dodleengine.tools.trash.TrashTool;
import com.dodles.gdx.dodleengine.tools.undo.UndoTool;

import javax.inject.Inject;

/**
 * To avoid circular dependencies between the ToolRegistry and tools, we need
 * a third class to eager load the tools so they can register themselves.
 */
@PerDodleEngine
public class ToolLoader {
    @Inject
    public ToolLoader(
        DrawTool drawTool,
        NullTool nullTool,
        SaveTool saveTool,
        AnimationTool animationTool,
        ChestTool chestTool,
        FileTool fileTool,
        FontTool fontTool,
        ImportTool importTool,
        LayerTool layerTool,
        PlayTool playTool,
        RedoTool redoTool,
        SceneTool sceneTool,
        GeometryTool shapeTool,
        ShareTool shareTool,
        TrashTool trashTool,
        UndoTool undoTool,
        CropTool cropTool
    ) {
    }
}
