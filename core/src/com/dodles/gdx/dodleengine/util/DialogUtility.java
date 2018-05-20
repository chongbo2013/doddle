package com.dodles.gdx.dodleengine.util;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.SkinAssets;
import com.dodles.gdx.dodleengine.editor.EditorInterfaceManager;
import javax.inject.Inject;

/**
 * Utility to assist with creating simple dialogs.
 */
@PerDodleEngine
public class DialogUtility {
    private final AssetProvider assetProvider;
    private final EditorInterfaceManager editorInterfaceManager;
    
    @Inject
    public DialogUtility(AssetProvider assetProvider, EditorInterfaceManager editorInterfaceManager) {
        this.assetProvider = assetProvider;
        this.editorInterfaceManager = editorInterfaceManager;
    }
    
    /**
     * Creates an "alert" dialog.
     */
    public final void alert(String title, String text) {
        alert(title, text, "OK");
    }
    
    /**
     * Creates an "alert" dialog with custom OK button text.
     */
    public final void alert(String title, String text, String okText) {
        Dialog alertDialog = new Dialog(title, assetProvider.getSkin(SkinAssets.UI_SKIN), "dialog");
        
        alertDialog
            .text(text)
            .button(okText, true)
            .key(Input.Keys.ENTER, true)
            .key(Input.Keys.ESCAPE, false)
            .show(editorInterfaceManager.getStage());
    }
    
    /**
     * Creates a "confirm" dialog.
     */
    public final void confirm(String title, String text, final ParamRunnable<Boolean> resultCallback) {
        confirm(title, text, "OK", "Cancel", resultCallback);
    }
    
    /**
     * Creates a "confirm" dialog with custom ok/cancel button text.
     */
    public final void confirm(String title, String text, String okText, String cancelText, final ParamRunnable<Boolean> resultCallback) {
        Dialog confirmDialog = new Dialog(title, assetProvider.getSkin(SkinAssets.UI_SKIN)) {
            protected void result(Object object) {
                resultCallback.run((Boolean) object);
            }
        };
        
        confirmDialog
            .text(text)
            .button(okText, true)
            .button(cancelText, false)
            .key(Input.Keys.ENTER, true)
            .key(Input.Keys.ESCAPE, false)
            .show(editorInterfaceManager.getStage());
    }
}
