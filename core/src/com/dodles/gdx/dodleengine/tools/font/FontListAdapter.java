package com.dodles.gdx.dodleengine.tools.font;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.BitmapFontAssets;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;

/**
 * List adapter for rendering font lists.
 */
public class FontListAdapter extends ArrayAdapter<BitmapFontAssets, Table> {
    private final Drawable bg = VisUI.getSkin().getDrawable(FullEditorViewState.TOOLBAR_TOP_ACTIVATED_COLOR);
    private final Drawable selection = VisUI.getSkin().getDrawable("list-selection");
    
    private AssetProvider assetProvider;
    private EditorState editorState;
    
    public FontListAdapter(AssetProvider assetProvider, EditorState editorState) {
        super(new Array<BitmapFontAssets>(BitmapFontAssets.values()));
        
        this.assetProvider = assetProvider;
        this.editorState = editorState;
        setSelectionMode(SelectionMode.SINGLE);
    }
    
    @Override
    protected final Table createView(BitmapFontAssets bmpFont) {
        return new FontDemoTable(assetProvider, bmpFont, editorState.getStrokeConfig().getColor().cpy());
    }
    
    @Override
    protected final void selectView(Table view) {
        view.setBackground(selection);
    }

    @Override
    protected final void deselectView(Table view) {
        view.setBackground(bg);
    }
}
