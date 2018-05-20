package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase;

import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.managers.BasePanelView;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseHierarchy;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseSchema;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStep;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseType;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to help manage state throughout the Phase UI usage.
 */
@PerDodleEngine
public class PhaseStateManager {

    private ObjectManager objectManager;

    private final PhaseHierarchy hierarchyStack = new PhaseHierarchy();
    private PhaseSchema selectedPhaseSchema;

    // holds all available phase schemas that have been predefined.
    private ArrayList<PhaseSchema> phaseSchemas = new ArrayList<PhaseSchema>();
    private String selectedPhaseType = PhaseType.EMPTY.name();

    // indicate which view is currently active. easy way to tell which panel is active.
    private BasePanelView currView;

    // flag if in editing mode.
    private boolean inEditing;

    // flag to indicate that UI is in phase move mode.
    private boolean inMove;

    // selected leafnode.
    private PhaseStep assignedPhaseStep;

    // state of the second-level slide out for more config options
    private boolean toggleExpand = false;


    @Inject
    public PhaseStateManager(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }


    /**
     * Get the full PhaseHierarchy object.
     */
    public final PhaseHierarchy getPhaseHierarchy() {
        return hierarchyStack;
    }

    /**
     * set selected phase schema.
     * @param selectedPhaseSchema
     */
    public final void setSelectedPhaseSchema(PhaseSchema selectedPhaseSchema) {
        this.selectedPhaseSchema = selectedPhaseSchema;
    }

    /**
     * get the PhaseSchema from the currently selected object.
     * @return
     */
    public final PhaseSchema getSelectedPhaseSchema() {
        DodlesActor activePhaseGroup = objectManager.getActiveLayer();
        if (activePhaseGroup != null && activePhaseGroup instanceof DodlesGroup) {
            final DodlesGroup pg = ((DodlesGroup) activePhaseGroup);
            selectedPhaseSchema = pg.getPhaseSchema();
            return selectedPhaseSchema;
        }
        return null;
    }

    /**
     * pull a Phase from the Active DodlesGroup by Name.
     * @param bdvg
     * @return
     */
    public final Phase getSelectedPhaseFromActiveGroup(BaseDodlesViewGroup bdvg) {
        DodlesGroup group = (DodlesGroup) bdvg;
        Phase p = null;
        List<Phase> phases = group.getPhases();
        for (int i = 0; i < phases.size(); i++) {
            p = phases.get(i);
            if (p.getName().equals(group.getVisiblePhaseID())) {
                break;
            }
        }
        return p;
    }

    /**
     * Get the phaseSchemas in memory.
     */
    public final ArrayList<PhaseSchema> getPhaseSchemas() {
        if (phaseSchemas.size() == 0) {
            phaseSchemas = new ArrayList<PhaseSchema>(PhaseConstants.PHASES);
        }

        return phaseSchemas;
    }

    /**
     * set the available Phases Schemas to pick from.
     * @param phaseSchemas
     */
    public final void setPhaseSchemas(ArrayList<PhaseSchema> phaseSchemas) {
        this.phaseSchemas = phaseSchemas;
    }

    /**
     * get selected phase type.
     * @return
     */
    public final String getSelectedPhaseType() {
        return selectedPhaseType;
    }

    /**
     * set selected phase type.
     * @param selectedPhaseType
     */
    public final void setSelectedPhaseType(String selectedPhaseType) {
        this.selectedPhaseType = selectedPhaseType;
    }

    /**
     * get a reference to the Current View loaded.
     * @return
     */
    public final BasePanelView getCurrView() {
        return currView;
    }

    /**
     * set currView for which panel is active.
     * @param currView
     */
    public final void setCurrView(BasePanelView currView) {
        this.currView = currView;
    }

    /**
     * are you editing something.
     * @return
     */
    // CHECKSTYLE.OFF: DesignForExtension
    public final boolean isInEditing() {
        return inEditing;
    }
    // CHECKSTYLE.ON: DesignForExtension

    /**
     * set isEditing mode flag.
     * @param inEditing
     */
    public final void setInEditing(boolean inEditing) {
        this.inEditing = inEditing;
    }

    /**
     * is in move mode?
     * @return
     */
    public final boolean isInMove() {
        return inMove;
    }

    /**
     * set move mode flag.
     * @param inMove
     */
    public final void setInMove(boolean inMove) {
        this.inMove = inMove;
    }

    /**
     * get the assigned step when leafnode is selected from hierarchy.
     * @return
     */
    public final PhaseStep getAssignedPhaseStep() {
        return assignedPhaseStep;
    }

    /**
     * set the assigned step when selected from the hierarchy.
     * @param assignedPhaseStep
     */
    public final void setAssignedPhaseStep(PhaseStep assignedPhaseStep) {
        this.assignedPhaseStep = assignedPhaseStep;
    }

    /**
     * is the second screen expanded.
     * @return
     */
    public final boolean isToggleExpand() {
        return toggleExpand;
    }

    /**
     * set the secondary expand.
     * @param toggleExpand
     */
    public final void setToggleExpand(boolean toggleExpand) {
        this.toggleExpand = toggleExpand;
    }
}
