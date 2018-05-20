package com.dodles.gdx.dodleengine.tools;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.input.InputHandler;
import java.util.List;

/**
 * A "Tool" is an interface for interacting with the editor.
 * @author mike.rosack
 */
public interface Tool extends Comparable<Tool> {
    String ICONUP = "1";
    String ICONDOWN = "2";

    /**
     * Returns the name of the tool.
     */
    String getName();

    /**
     * Returns the activated color for lookup in the Skin.
     * @return
     */
    String getActivatedColor();
    
    /**
     * Returns the default row of the tool in the tool interface.
     */
    int getRow();
    
    /**
     * Returns the order of the tool in it's row.
     */
    int getOrder();
    
    /**
     * Returns the icon to use for the tool.
     */
    TextureRegion getIcon();

    /**
     * Returns the tool icon.
     * @param icon - name of the texture in the atlas.
     */
    TextureAtlas.AtlasRegion getIcon(String icon);

    /**
     * Returns the name of the skin style for the corresponding editor button.
     * @return
     */
    String getButtonStyleName();

    /**
     * Returns all of the input handlers for the tool.
     */
    List<InputHandler> getInputHandlers();

    /**
     * Called when the tool is activated.
     */
    void onActivation();
    
    /**
     * Called when the tool is deactivated.
     */
    void onDeactivation();
}
