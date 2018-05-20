package com.dodles.gdx.dodleengine.editor;

import com.badlogic.gdx.Gdx;

/**
 * classify the density of the application.
 * @author pklemstine
 */
public class DensityManager {

    private static DensityType density;
    private static boolean flag = false;

    /**
     * enumerator for all the major Density types -- borrowed from androids naming convensions.
     */
    public enum DensityType {
        LDPI(.75f, "LDPI"),
        MDPI(1f, "MDPI"),  //default
        TVDPI(1.33f, "TVDPI"),
        HDPI(1.5f, "HDPI"),
        XHDPI(2f, "XHDPI"),  //retina
        XXHDPI(3f, "XXLDPI"),
        XXXHDPI(4f, "XXXLDPI");

        private float scale;
        private String name;

        DensityType(float scale, String name) {
            this.scale = scale;
            this.name = name;
        }

        /**
         * return current scale.
         * @return
         */
        public float getScale() {
            return scale;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    protected DensityManager() {
        // prevents calls from subclass.  get around a checkstyle concern.
        throw new UnsupportedOperationException();
    }


    /**
     * Sets the pixel density to use for interface elements.
     */
    public static void setDensity(float densityIn) {
        density = getBestMatchDensity(densityIn);
        DensityManager.flag = true;
    }

    /**
     * Returns the density to use for interface elements.
     */
    public static DensityType getDensity() {
        if (density != null) {
            return density;
        }
        density = getBestMatchDensity(Gdx.graphics.getDensity());
        return density;
    }

    /**
     * Returns the density to use for interface elements.
     */
    public static DensityType getBestMatchDensity(float in) {
        float bestDistance = Float.MAX_VALUE;
        DensityType bestType = null;
        for (DensityType dt : DensityType.values()) {
            float tempDistance = Math.abs(dt.getScale() - in);
            if (tempDistance < bestDistance) {
                bestDistance = tempDistance;
                bestType = dt;
            }
        }
        if (bestType == null) {
            bestType = DensityType.MDPI;
        }
        return bestType;
    }


    /**
     * Returns name of detected densityType.
     */
    public static String getName() {
        if (density != null) {
            return density.toString();
        } else {
            return getDensity().toString();
        }
    }

    /**
     * Returns scale value of detected densityType.
     */
    public static float getScale() {
        if (density != null) {
            return density.getScale();
        } else {
            return getDensity().getScale();
        }

    }

    /**
     * getter for DensityManager flag.
     * @return
     */
    public static boolean isFlag() {
        return flag;
    }

    /**
     * setter for DensityManager flag.
     * @param flag
     */
    public static void setFlag(boolean flag) {
        DensityManager.flag = flag;
    }
}
