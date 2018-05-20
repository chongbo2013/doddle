package com.dodles.gdx.dodleengine;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Defines something that manages a stage that needs to be rendered to the screen.
 */
public interface StageRenderer {
    /**
     * Initializes the stage.
     */
    void initStage(Batch batch, int width, int height);
    
    /**
     * Returns the stage.
     */
    Stage getStage();

    /**
     * Resizes the stage.
     */
    void resize(int width, int height);
    
    /**
     * Updates the actors by delta time.
     */
    void act(float deltaTime);
    
    /**
     * Draws the stage.
     */
    void draw();
    
    /**
     * Returns a value indicating whether this renderer should act when overloaded.
     */
    boolean actWhenOverloaded();
}
