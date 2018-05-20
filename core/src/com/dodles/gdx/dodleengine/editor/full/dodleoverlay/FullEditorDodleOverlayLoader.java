package com.dodles.gdx.dodleengine.editor.full.dodleoverlay;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.tools.animation.effect.QuickAddEffectPanel;
import com.dodles.gdx.dodleengine.tools.animation.effect.SelectBlockPanel;
import com.dodles.gdx.dodleengine.tools.animation.effect.SelectEffectTypePanel;
import com.dodles.gdx.dodleengine.tools.animation.list.EffectListPanel;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.managers.PhasesUIRightPanel;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.settings.PhasesUILeftPanel;
import com.dodles.gdx.dodleengine.tools.scene.SceneUIListPanel;

import javax.inject.Inject;

/**
 * To avoid circular dependencies, we need a third class to eager 
 * load the overlays so they can register themselves.
 */
@PerDodleEngine
public class FullEditorDodleOverlayLoader {
    @Inject
    public FullEditorDodleOverlayLoader(
        PeelDodleOverlay pdo,
        TransformIndicatorDodleOverlay tido,
        PhasesUIRightPanel phasesUIManagerPanel,
        PhasesUILeftPanel phasesUISettingsPanel,
        QuickAddEffectPanel quickAddEffectPanel,
        SelectEffectTypePanel selectEffectTypePanel,
        EffectListPanel effectListPanel,
        SceneUIListPanel sceneUIListPanel,
        SelectBlockPanel selectBlockPanel
    ) {
    }
}
