package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.math.Vector2;

/**
 * Class to hold bezier curve values.
 * @author marknickel
 *
 */
public class CubicBezierCurve {
    private Vector2 cp0, cp1, ep0, ep1;
    private boolean isContinuation;

    public CubicBezierCurve(Vector2 cp0, Vector2 cp1, Vector2 ep0, Vector2 ep1, boolean isContinuation) {
        this.cp0 = cp0;
        this.cp1 = cp1;
        this.ep0 = ep0;
        this.ep1 = ep1;
        this.isContinuation = isContinuation;
    }

    /**
     * convenience constructor to create a copy of the incoming BezierCurve.
     * -- modeled after the new Vector2(Vector2) constructor
     * @param c
     */
    public CubicBezierCurve(CubicBezierCurve c) {
        this.cp0 = c.cp0;
        this.cp1 = c.cp1;
        this.ep0 = c.ep0;
        this.ep1 = c.ep1;
        this.isContinuation = c.isContinuation;
    }

    /**
     * toString().
     */
    public String toString() {
        return "cp0: " + cp0 + " cp1: " + cp1 + " ep0: " + ep0 + " ep1: " + ep1 + " isContinuation: " + isContinuation;
    }

    /**
     * getCp0.
     * @return cp0
     */
    public Vector2 getCp0() {
        return cp0;
    }

    /**
     * setCp0.
     * @param cp0
     */
    public void setCp0(Vector2 cp0) {
        this.cp0 = cp0;
    }

    /**
     * getCp1.
     * @return
     */
    public Vector2 getCp1() {
        return cp1;
    }

    /**
     * setCp1.
     * @param cp1
     */
    public void setCp1(Vector2 cp1) {
        this.cp1 = cp1;
    }

    /**
     * getEp0 ep = endpoint.
     * @return ep0
     */
    public Vector2 getEp0() {
        return ep0;
    }

    /**
     * setEp0.
     * @param ep0
     */
    public void setEp0(Vector2 ep0) {
        this.ep0 = ep0;
    }

    /**
     * getEp1.
     * @return
     */
    public Vector2 getEp1() {
        return ep1;
    }

    /**
     * setEp1.
     * @param ep1
     */
    public void setEp1(Vector2 ep1) {
        this.ep1 = ep1;
    }

    /**
     * isContinuation.
     * @return boolean
     */
    public boolean isContinuation() {
        return isContinuation;
    }

    /**
     * setContinuation.
     * @param ic
     */
    public void setContinuation(boolean ic) {
        this.isContinuation = ic;
    }
}
