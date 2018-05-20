package com.dodles.gdx.dodleengine.brushes;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import javax.inject.Inject;

/**
 * Forces eager loading and registration of brushes.
 */
@PerDodleEngine
public class BrushLoader {
    @Inject
    public BrushLoader(
        ChalkBrush chalk,
        CrayonBrush crayon,
        DryBrush dry,
        EraserBrush eraser,
        FountainPenBrush fountainPen,
        MarkerBrush markerBrush,
        PaintBrush paintBrush,
        PencilBrush pencilBrush
    ) {
    }
}
