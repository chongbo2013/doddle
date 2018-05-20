package com.dodles.gdx.dodleengine.editor.utils;

/**
 * Lightweight class used to define related width and height values.
 */
public class Dimensions {

    // region Properties & Variables

    private float width;
    private float height;

    // endregion Properties & Variables


    // region Constructors

    public Dimensions() {
        this(0, 0);
    }

    public Dimensions(float width, float height) {
        this.width = width;
        this.height = height;
    }

    // endregion Constructors


    // region Property Accessors

    /**
     * Property getter for width property.
     * @return
     */
    public float getWidth() {
        return this.width;
    }

    /**
     * Property setter for width property.
     * @param width
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Property getter for height property.
     * @return
     */
    public float getHeight() {
        return this.height;
    }

    /**
     * Property setter for height property.
     * @param height
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Sets both width and height.
     * @param width
     */
    public void setDimensions(float width, float height) {
        this.width = width;
        this.height = height;
    }

    // endregion Property Accessors

}
