package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.settings;

import javax.inject.Inject;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

/**
 * For the left panel modify phase settings UI.
 */
@PerDodleEngine
public class ModifyPhaseSettingsView {
    private final AssetProvider assetProvider;
    
    private Table phasesUISettings;
    private Table modifyPhaseRootTable;

    @Inject
    public ModifyPhaseSettingsView(AssetProvider assetProvider) {
        this.assetProvider = assetProvider;
    }

    /**
     * Initializes the view.
     */
    public final void initialize(Table rootTable, Skin skin) {
        LmlParser parser = VisLml.parser()
                .skin(skin)
                .build();

        this.phasesUISettings = rootTable;

        String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_PHASES_SETTING_MODIFYPHASE_VIEW);
        modifyPhaseRootTable = (Table) parser.parseTemplate(template).get(0);

        phasesUISettings.top();
        phasesUISettings.add(modifyPhaseRootTable).expandX().fillX();
        
        TextButton deletePhaseButton = modifyPhaseRootTable.findActor("deletePhase");
        deletePhaseButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                // Access the BasicManagerView and remove the current phase button
                // Note that we will need to pass in some PhaseSchema Button / phaseIndex data on the 2nd click of
                // a PhaseButton.  This data could be stored at the PhasesUISettingsPanel level
            }
        });
    }
    
    /**
     * remove the modify PhaseSchema UI from the Settings Panel.
     */
    public final void resetUI() {
        if (modifyPhaseRootTable != null) {
            phasesUISettings.clearChildren();
            modifyPhaseRootTable = null;
            phasesUISettings.invalidate();
        }
    }
}
