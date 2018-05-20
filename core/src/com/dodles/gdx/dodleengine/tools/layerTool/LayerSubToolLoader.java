package com.dodles.gdx.dodleengine.tools.layerTool;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.chest.ChestLayerToolSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.copy.CopyLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.fillcolor.FillColorLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.fliphorizontally.FlipHorizontallyLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.flipvertically.FlipVerticallyLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.lock.LockLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.merge.MergeLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.opacity.OpacityLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.recenter.ReCenterLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.spine.SpineLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.strokecolor.StrokeColorLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.strokesize.StrokeSizeLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.trash.TrashLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.unlock.UnlockLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.unmerge.UnmergeLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.zindex.ZIndexDownLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.zindex.ZIndexUpLayerSubTool;
import javax.inject.Inject;

/**
 * To avoid circular dependencies between the LayerToolRegistry and layerTools, we need
 * a third class to eager load the tools so they can register themselves.
 */
@PerDodleEngine
public class LayerSubToolLoader {
    @Inject
    public LayerSubToolLoader(
            ChestLayerToolSubTool chestLayerTool,
            CopyLayerSubTool copyLayerTool,
            //DensityFactorLayerSubTool densityFactorLayerTool,
            FillColorLayerSubTool fillColorLayerTool,
            FlipHorizontallyLayerSubTool flipHorizontallyLayerTool,
            FlipVerticallyLayerSubTool flipVerticallyLayerTool,
            LockLayerSubTool lockLayerTool,
            MergeLayerSubTool mergeLayerTool,
            OpacityLayerSubTool opacityLayerTool,
            PhaseLayerSubTool phaseTool,
            SpineLayerSubTool spineTool,
            //SizeFactorLayerSubTool sizeFactorLayerTool,
            //StreakFactorLayerSubTool streakFactorLayerTool,
            StrokeColorLayerSubTool strokeColorLayerTool,
            StrokeSizeLayerSubTool strokeSizeLayerTool,
            TrashLayerSubTool trashLayerTool,
            UnlockLayerSubTool unlockLayerTool,
            UnmergeLayerSubTool unMergeLayerTool,
            ZIndexUpLayerSubTool zIndexUpTool,
            ZIndexDownLayerSubTool zIndexDownTool,
            ReCenterLayerSubTool reCenterLayerSubTool
    ) {
    }
}
