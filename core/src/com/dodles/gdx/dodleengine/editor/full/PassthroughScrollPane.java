package com.dodles.gdx.dodleengine.editor.full;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

/**
 * A scrollpane that passes through input events if it doesn't hit a touchable widget.
 */
public class PassthroughScrollPane extends ScrollPane {
    public PassthroughScrollPane(Actor widget) {
        super(widget);
    }
    
    @Override
    public final Actor hit(float x, float y, boolean touchable) {
        Vector2 widgetCoords = getWidget().parentToLocalCoordinates(new Vector2(x, y));
        Actor hitWidget = getWidget().hit(widgetCoords.x, widgetCoords.y, touchable);
        
        if (hitWidget != null) {
            return super.hit(x, y, touchable);
        }
        
        return null;
    }
}
