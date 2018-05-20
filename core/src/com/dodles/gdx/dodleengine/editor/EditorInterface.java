package com.dodles.gdx.dodleengine.editor;

import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * An editing interface to the dodle engine.
 */
public interface EditorInterface {
    /**
     * Activates the editor interface, drawing on the given stage.
     */
    void activate(Stage stage);
    
    /**
     * Deactivates the editor interface.
     */
    void deactivate();
    
    /**
     * Returns the padding taken up by the editor interface on screen.
     */
    Padding getInterfacePadding();
    
    /**
     * resize() event Handler.
     */
    void resize(int width, int height);
    
    /**
     * Resets the interface.
     */
    void reset();
    
    /**
     * Padding information.
     */
    public class Padding {
        private float left;
        private float right;
        private float top;
        private float bottom;
        
        public Padding(float left, float right, float top, float bottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }
        
        /**
         * Returns the left padding.
         */
        public final float getLeft() {
            return left;
        }

        /**
         * Returns the right padding.
         */
        public final float getRight() {
            return right;
        }
        
        /**
         * Returns the top padding.
         */
        public final float getTop() {
            return top;
        }
        
        /**
         * Returns the bottom padding.
         */
        public final float getBottom() {
            return bottom;
        }
    }
}
