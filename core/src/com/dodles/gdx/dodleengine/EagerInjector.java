package com.dodles.gdx.dodleengine;

import com.dodles.gdx.dodleengine.brushes.BrushLoader;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlayLoader;
import com.dodles.gdx.dodleengine.editor.overlays.OverlayLoader;
import com.dodles.gdx.dodleengine.geometry.GeometryLoader;
import com.dodles.gdx.dodleengine.tools.ToolLoader;
import com.dodles.gdx.dodleengine.tools.animation.subtools.AnimationSubtoolLoader;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubToolLoader;

import javax.inject.Inject;

/**
 * The only purpose of this class is to inject classes that will register themselves
 * with other classes to avoid circular dependencies.
 */
@PerDodleEngine
public class EagerInjector {
    @Inject
    public EagerInjector(
        BrushLoader brushLoader,
        FullEditorDodleOverlayLoader fdOverlayLoader,
        GeometryLoader geometryLoader,
        OverlayLoader overlayLoader,
        ToolLoader toolLoader,
        LayerSubToolLoader layerSubToolLoader,
        AnimationSubtoolLoader animationSubtoolLoader
    ) { }
}
