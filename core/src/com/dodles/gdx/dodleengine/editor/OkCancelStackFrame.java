package com.dodles.gdx.dodleengine.editor;

import com.badlogic.gdx.Gdx;

/**
 * A single frame in the ok/cancel stack.
 */
public class OkCancelStackFrame {

    //region Properties & Variables
    private Runnable ok;
    private Runnable cancel;
    private String type;
    private boolean forceOk = false; // a bit of hack, needed while transitioning from Runnable to Command approach
    private boolean forceCancel = false; // a bit of hack, needed while transitioning from Runnable to Command approach
    //endregion Properties & Variables


    //region Constructors

    public OkCancelStackFrame() {
        this(null, null, null);
    }

    public OkCancelStackFrame(String type, boolean hasOk, boolean hasCancel) {
        this(null, null, type);
        forceOk = hasOk;
        forceCancel = hasCancel;
    }

    public OkCancelStackFrame(Runnable ok, Runnable cancel, String type) {
        this.ok = ok;
        this.cancel = cancel;
        this.type = type;
    }

    //endregion Constructors


    //region Public API

    /**
     * Returns true if there is an okay action in this frame.
     */
    public boolean hasOk() {
        return (forceOk || ok != null);
    }

    /**
     * Executes the okay action, running it on the core libGDX application.
     */
    public void execute() {
        if (ok != null) {
            Gdx.app.postRunnable(ok);
        }
    }

    /**
     * Returns true if there is a cancel action in this frame.
     */
    public boolean hasCancel() {
        return (forceCancel || cancel != null);
    }

    /**
     * Executes the okay action, running it on the core libGDX application.
     */
    public void cancel() {
        if (cancel != null) {
            Gdx.app.postRunnable(cancel);
        }
    }

    /**
     * Returns the type
     */
    public String getType() {
        return type;
    }

    //endregion  Public API
}
