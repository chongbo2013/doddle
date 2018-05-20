package com.dodles.gdx.dodleengine.brushes;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.GraphicsGenerator;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.shaperenderer.ShapeRendererQuadraticCurveGraphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Common Line Brush functionality.
 */
public abstract class AbstractLineBrush extends AbstractBrush {
    
    public AbstractLineBrush(AssetProvider assetProvider, CommandFactory commandFactory) {
        super(assetProvider, commandFactory);
    }
    
    @Override
    protected final boolean keepDuplicatePoints() {
        return false;
    }
    
    @Override
    protected final void onMouseMove(Shape strokeShape) {        
        Vector2 prevMid = getPrevMid();
        
        if (prevMid != null) {
            if (getRulerMode() != RulerMode.NONE) {
                createGenerator(strokeShape, getPoint(0), getArcPoint(), getCurPoint());
            } else {
                createGenerator(strokeShape, prevMid, getPrevPoint(), getMidPoint());
            }
        }
    }
    
    private void createGenerator(Shape strokeShape, final Vector2 p0, final Vector2 cp, final Vector2 p1) {
        strokeShape.addGenerator(new GraphicsGenerator() {
            @Override
            public List<Graphics> generateGraphics(Shape shape) {
                ArrayList<Graphics> result = new ArrayList<Graphics>();
                
                StrokeConfig sc = shape.getStrokeConfig();
                result.add(new ShapeRendererQuadraticCurveGraphics(p0, cp, p1, sc.getSize(), sc.getColor()));

                return result;
            }
        });
    }
}
