package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase;
/**
 * Instead of polluting the EngineEventType class with all sorts of Dimension-specific states, collect
 * them into another spot and introspect the EngineEventData in the listen() callback.
 * 
 * @author marknickel
 *
 */
public enum PhaseUIStates {
    SHOW_MANAGER_TOGGLE("show_manager_toggle"),
    SWITCH_ADVANCED_MANAGER("switch_advanced_manager"),
    SWITCH_BASIC_MANAGER("switch_basic_manager"),
    SWITCH_NEW_PHASE("switch_new_phase"),
    SWITCH_CONFIG_PHASE("switch_config_phase"),
    SWITCH_MODIFY_PHASE("switch_modify_phase"), 
    SWITCH_SETTINGS_VIEW("swich_settings_view"),
    EASEOUT_SETTINGS_PANEL("easeout_settings_panel"),
    EASEIN_SETTINGS_PANEL("easein_settings_panel"),
    EASEOUT_MANAGER_PANEL("easeout_manager_panel"),
    EASEIN_MANAGER_PANEL("easein_manager_panel"),
    SWITCH_CONFIG_MANAGER("switch_config_manager"),
    SWITCH_PHASE_STEP_FALL_BACK_MANAGER("switch_phase_step_fall_back_manager"),
    SWITCH_PROPERTIES_VIEW("switch_properties_view"),
    SWITCH_PHASE_STEP_SELECTED("switch_phase_step_selected"),
    RELOAD_PROPERTIES_VIEW("reload_properties_view"),
    UPDATE_PHASEBUTTON_TEXT("update_phasebutton_text"),
    RELOAD_ADVANCED_FOLDER_VIEW("reload_advanced_folder_view"),
    RELOAD_BASIC_PHASEBUTTONS("reload_basic_phasebuttons"),
    MOVE_PHASE_ON("move_phase_on"),
    MOVE_PHASE_OFF("move_phase_off"),
    REFRESH_ASSIGNED_SLOT_NAME("refresh_assigned_slot_name");



    @SuppressWarnings("unused")
    private String value;
    
    private PhaseUIStates(String value) {
        this.value = value;
    }

}
