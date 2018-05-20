package com.dodles.gdx.dodleengine.brushes;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.commands.DrawStrokeCommand;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;

/**
 * Functionality common between all Brushes.
 */
public interface Brush extends Comparable<Brush> {
    /**
     * Begins a new stroke.
     */
    void beginStroke(Shape shape);
    
    /**
     * Returns the name of the brush.
     */
    String getName();
    
    /**
     * Returns the order of the brush to show in the UI.
     */
    int getOrder();
    
    /**
     * Creates the command to use for live drawing.
     */
    DrawStrokeCommand createCommand();
    
     /**
     * Returns the active icon to use for the brush.
     */
    TextureRegion getActiveIcon();
    
    /**
     * Returns the inactive icon to use for the brush.
     */
    TextureRegion getInactiveIcon();
    
    /**
     * Returns the current stroke configuration for the brush.
     */
    StrokeConfig getBrushStrokeConfig();
    
    /**
     * Sets a new stroke configuration for the brush.
     */
    void setBrushStrokeConfig(StrokeConfig newStrokeConfig);
    
    /**
     * Returns the RulerMode for the brush.
     */
    RulerMode getRulerMode();
    
    /**
     * Sets the RulerMode for the brush.
     */
    void setRulerMode(RulerMode newRulerMode);
    
    /**
     * Handles a movement of the mouse to the new point.
     */
    void mouseMove(Vector2 point);
    
    /**
     * Initializes a brush stroke for a shape.
     */
    void init(Shape shape, RulerMode newRulerMode);
}
