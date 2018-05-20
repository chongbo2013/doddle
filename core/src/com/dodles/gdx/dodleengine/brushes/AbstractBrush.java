package com.dodles.gdx.dodleengine.brushes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.DrawStrokeCommand;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import java.util.ArrayList;

/**
 * Base brush functionality.
 */
public abstract class AbstractBrush implements Brush {
    private final AssetProvider assetProvider;
    
    private ArrayList<Vector2> points = new ArrayList<Vector2>();
    private RulerMode rulerMode = RulerMode.NONE;
    private StrokeConfig strokeConfig = null;
    private Shape strokeShape;
    
    private CommandFactory commandFactory;
    
    public AbstractBrush(AssetProvider assetProvider, CommandFactory commandFactory) {
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
    }
    
    @Override
    public final void beginStroke(Shape shape) {
        points.clear();
        strokeShape = shape;
    }
    
    @Override
    public abstract String getName();
    
    @Override
    public final StrokeConfig getBrushStrokeConfig() {
        strokeConfig = getDefaultStrokeConfig();
        return strokeConfig;
    }
    
    @Override
    public final void setBrushStrokeConfig(StrokeConfig newStrokeConfig) {
        strokeConfig = newStrokeConfig;
    }
    
    @Override
    public final RulerMode getRulerMode() {
        return rulerMode;
    }
    
    @Override
    public final void setRulerMode(RulerMode newRulerMode) {
        rulerMode = newRulerMode;
    }
    
    @Override
    public final void mouseMove(Vector2 point) {
        if (!keepDuplicatePoints() && points.size() > 0) {
            if (point.equals(points.get(points.size() - 1))) {
                return;
            }
        }
        
        points.add(point);
        onMouseMove(strokeShape);
    }
    
    /**
     * Implements Comparable<Brush>.
     */
    @Override
    public final int compareTo(Brush otherBrush) {        
        Integer thisOrder = new Integer(getOrder());
        Integer thatOrder = new Integer(otherBrush.getOrder());
        
        return thisOrder.compareTo(thatOrder);
    }
    
    /**
     * Creates the necessary command for the brush.
     */
    // CHECKSTYLE.OFF: DesignForExtension - methods that are non-final on purpose
    public DrawStrokeCommand createCommand() {
        return (DrawStrokeCommand) commandFactory.createCommand(DrawStrokeCommand.COMMAND_NAME);
    }
    
    @Override
    public void init(Shape shape, RulerMode newRulerMode) {
        setRulerMode(newRulerMode);
        beginStroke(shape);   
    }
    // CHECKSTYLE.ON: DesignForExtension
    
    /**
     * Returns the active asset provider.
     */
    protected final AssetProvider getAssetProvider() {
        return assetProvider;
    }
    
    /**
     * Returns the brush icons texture.
     */
    protected final Texture getBrushIconsTexture() {
        return assetProvider.getTexture(TextureAssets.EDITOR_TOOL_BRUSHICONS);
    }
    
    /**
     * Called for inherited classes on mouse movement.
     */
    protected abstract void onMouseMove(Shape shape);
    
    /**
     * Returns the default stroke configuration for the brush.
     */
    protected abstract StrokeConfig getDefaultStrokeConfig();
    
    /**
     * Returns true if duplicate points should be saved in the brush stroke.
     */
    protected abstract boolean keepDuplicatePoints();
    
    /**
     * Returns the distance to the previous point.
     */
    protected final float getDistanceToPrevious() {
        int size = points.size();
        
        if (size < 2) {
            return -1f;
        }
        
        return points.get(size - 2).dst(points.get(size - 1));
    }
    
    /**
     * Returns point i in the point list.
     */
    protected final Vector2 getPoint(int i) {
        if (points.size() < i) {
            return null;
        }
        
        return points.get(i);
    }
    
    /**
     * Returns the current point.
     */
    protected final Vector2 getCurPoint() {
        if (points.size() < 1) {
            return null;
        }
        
        return points.get(points.size() - 1);
    }
    
    /**
     * Returns the previous point.
     */
    protected final Vector2 getPrevPoint() {
        if (points.size() < 2) {
            return null;
        }
        
        return points.get(points.size() - 2);
    }
    
    /**
     * Returns the current midpoint between this and the previous point.
     */
    protected final Vector2 getMidPoint() {
        Vector2 curPoint = getCurPoint();
        Vector2 prevPoint = getPrevPoint();
        
        if (prevPoint == null) {
            return null;
        }
        
        return new Vector2(
            prevPoint.x + ((curPoint.x - prevPoint.x) / 2f),
            prevPoint.y + ((curPoint.y - prevPoint.y) / 2f)
        );
    }
    
    /**
     * Returns the midpoint between the n - 1 and n - 2 points.
     */
    protected final Vector2 getPrevMid() {
        if (points.size() < 3) {
            return null;
        }
        
        Vector2 prevPoint = getPrevPoint();
        Vector2 thirdPrev = points.get(points.size() - 3);
        
        return new Vector2(
            thirdPrev.x + ((prevPoint.x - thirdPrev.x) / 2f),
            thirdPrev.y + ((prevPoint.y - thirdPrev.y) / 2f)
        );
    }
    
    /**
     * Returns new points on a smoothed curve between the current point and the previous point.
     */
    protected final ArrayList<Vector2> getNewSmoothedPoints() {
        ArrayList<Vector2> result = new ArrayList<Vector2>();
        Vector2 prevMid = getPrevMid();
        
        if (prevMid == null) {
            return result;
        }
        
        Bezier<Vector2> curve;
                
        if (getRulerMode() != RulerMode.NONE) {
            curve = new Bezier<Vector2>(points.get(0), getArcPoint(), getCurPoint());
        } else {
            curve = new Bezier<Vector2>(getPrevMid(), getPrevPoint(), getMidPoint());
        }
        
        Vector2 tempPoint = new Vector2();
        
        curve.valueAt(tempPoint, 0);
        result.add(tempPoint.cpy());
        curve.valueAt(tempPoint, 1);
        
        bezierBisect(result, curve, result.get(0), 0, tempPoint, 1);
        
        result.add(tempPoint);
        
        return result;
    }
    
    private void bezierBisect(ArrayList<Vector2> result, Bezier<Vector2> curve, Vector2 p0, float t0, Vector2 p1, float t1) {
        if (p0.dst(p1) < 1) {
            return;
        }
        
        Vector2 pMid = new Vector2();
        float tMid = (t0 + t1) / 2;
        curve.valueAt(pMid, tMid);
        
        bezierBisect(result, curve, p0, t0, pMid, tMid);
        
        result.add(pMid);
        
        bezierBisect(result, curve, pMid, tMid, p1, t1);
    }
    
    /**
     * Returns the point that a line should arc at for the ruler.
     */
    protected final Vector2 getArcPoint() {
        Vector2 arcPoint = new Vector2();
        Vector2 prev = getPrevPoint();
                
        Bezier<Vector2> bezier = new Bezier<Vector2>(getPoint(0), prev, getCurPoint());
        bezier.valueAt(arcPoint, 0.5f);
        
        float distance = prev.dst(arcPoint);
        float angle = (float) Math.atan2(prev.x - arcPoint.x, prev.y - arcPoint.y);
        
        // For some reason, the commented out code below gives a different answer than the math
        // above, I'm not sure why, and I'm sick of trying to figure out why. :(
        // I'm sure it's obvious to you.
        /*Vector2 diff = new Vector2(prev.x - arcPoint.x, prev.y - arcPoint.y);
        float angle = diff.angle() * MathUtils.degreesToRadians;*/
        
        return new Vector2(prev.x + ((distance * (float) Math.sin(angle)) * 2), prev.y + ((distance * (float) Math.cos(angle)) * 2));
    }
}
