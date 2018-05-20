package com.dodles.gdx.dodleengine.commands;

/**
 * Genericize the getObjectID method to reduce code in the file import overlay.
 */
public interface Importable {

    /**
     * Return the root object id for selection.
     */
    String getObjectID();
}
