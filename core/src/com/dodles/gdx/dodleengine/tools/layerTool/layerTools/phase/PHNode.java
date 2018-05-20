package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseAttributeType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStepType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseType;

import java.util.ArrayList;

/**
 * PhaseSchema Hierarchy Node - shortended to PHNode
 * Object represents a node within the entire object structure of the PhaseSchema Hierarchy
 *
 * it is built from the PhaseSchema schema.
 */
public class PHNode {

    private PhaseType type;
    private PhaseAttributeType attributeType;
    private PhaseStepType stepType;
    private String phaseId = null;

    private Integer depth;
    private Integer step;

    private ArrayList<PHNode> children = new ArrayList<PHNode>();

    public PHNode() {

    }

    public PHNode(JsonValue json) {
        loadConfig(json);
    }

    /**
     * Returns the attribute type.
     */
    public final PhaseAttributeType getAttributeType() {
        return attributeType;
    }

    /**
     * Sets the attribute type.
     */
    public final void setAttributeType(PhaseAttributeType attributeType) {
        this.attributeType = attributeType;
    }

    /**
     * Gets the PhaseType.
     */
    public final PhaseType getType() {
        return type;
    }

    /**
     * Sets the PhaseType.
     */
    public final void setType(PhaseType type) {
        this.type = type;
    }

    /**
     * Get the PhaseStepType.
     */
    public final PhaseStepType getStepType() {
        return stepType;
    }

    /**
     * Set the PhaseStepType.
     */
    public final void setStepType(PhaseStepType stepType) {
        this.stepType = stepType;
    }

    /**
     * Returns the list of PHNode children.
     */
    public final ArrayList<PHNode> getChildren() {
        return children;
    }

    /**
     * Sets the list of PHNode children.
     */
    public final void setChildren(ArrayList<PHNode> children) {
        this.children = children;
    }

    /**
     * Returns the phaseId.
     */
    public final String getPhaseId() {
        return phaseId;
    }

    /**
     * Sets the phaseId.
     */
    public final void setPhaseId(String phaseId) {
        this.phaseId = phaseId;
    }

    /**
     * Get the depth.
     */
    public final Integer getDepth() {
        return depth;
    }

    /**
     * Set the depth.
     */
    public final void setDepth(Integer depth) {
        this.depth = depth;
    }

    /**
     * Get the step.
     */
    public final Integer getStep() {
        return step;
    }

    /**
     * Set the step.
     */
    public final void setStep(Integer step) {
        this.step = step;
    }

    /**
     * Specialized method to write the json confg.
     */
    public final void writeConfig(Json json) {
        if (phaseId != null) {
            json.writeValue("phaseId", phaseId);
        }
        if (type != null) {
            json.writeValue("type", type.name());
        }
        if (attributeType != null) {
            json.writeValue("attributeType", attributeType.name());
        }
        if (stepType != null) {
            json.writeValue("stepType", stepType.name());
        }
        if (children.size() > 0) {
            json.writeArrayStart("children");
            for (PHNode node : children) {
                json.writeObjectStart();
                node.writeConfig(json);
                json.writeObjectEnd();
            }
            json.writeArrayEnd();
        }

        if (depth != null) {
            json.writeValue("depth", depth);
        }
        if (step != null) {
            json.writeValue("step", step);
        }
    }

    private void loadConfig(JsonValue json) {
        depth = json.getInt("depth");
        if (json.has("step")) {
            step = json.getInt("step");
        }

        if (json.has("stepType")) {
            stepType = PhaseStepType.valueOf(json.getString("stepType"));
        }

        if (json.has("attributeType")) {
            attributeType = PhaseAttributeType.valueOf(json.getString("attributeType"));
        }
    }

    /**
     * perform a deep clone of the object.
     * @return
     */
    public final PHNode dodleClone() {
        PHNode node = new PHNode();
        node.setType(type);
        node.setAttributeType(attributeType);
        node.setStepType(stepType);
        node.setPhaseId(phaseId);
        node.setDepth(depth);
        node.setStep(step);
        if (children.size() > 0) {
            for (PHNode child : children) {
                node.getChildren().add(child.dodleClone());
            }
        }
        return node;
    }

    /**
     * convenient toString method override.
     * @return
     */
    public final String toString() {
        String locattribute = "";
        String locstep = "";

        if (attributeType != null) {
            locattribute = attributeType.name();
        }
        if (stepType != null) {
            locstep = stepType.name();
        }
        return "PHNode -- depth: " + depth + " == step: " + step + "   =-=-=  Attribute: " + locattribute + "  Step: " + locstep;
    }
}
