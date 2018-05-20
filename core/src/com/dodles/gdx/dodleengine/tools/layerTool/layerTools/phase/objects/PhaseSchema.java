package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

/**
 * Base phase object.
 */
public class PhaseSchema {

    private PhaseType phaseType;
    private ArrayList<PhaseAttribute> attributeList = new ArrayList<PhaseAttribute>();;

    public PhaseSchema() {

    }

    public PhaseSchema(JsonValue json) {
        loadConfig(json);
    }

    public PhaseSchema(PhaseType type) {
        this.phaseType = type;
    }

    /**
     * Get the PhaseType of this PhaseSchema.
     */
    public final PhaseType getPhaseType() {
        return phaseType;
    }

    /**
     * Get the PhaseAttributes of this PhaseSchema.
     */
    public final ArrayList<PhaseAttribute> getAttributeList() {
        return attributeList;
    }

    /**
     * Set the PhaseAttribute list.
     */
    public final void setAttributeList(ArrayList<PhaseAttribute> attributeList) {
        this.attributeList = attributeList;
    }

    /**
     * Search through the attributes and return a specific Attribute, ie:  "Emotion".
     */
    public final PhaseAttribute getAttributeByType(String attributeType) {
        PhaseAttribute attr = null;
        for (PhaseAttribute attribute: attributeList) {
            if (attribute.getPhaseAttributeType().name().equals(attributeType)) {
                attr = attribute;
                break;
            }
        }
        return attr;
    }

    /**
     * Write this object to json.
     */
    public final void writeConfig(Json json) {
        json.writeValue("phaseType", phaseType.name());
        json.writeArrayStart("attributeList");
        for (PhaseAttribute attr : attributeList) {
            json.writeObjectStart();
            attr.writeConfig(json);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
    }

    /**
     * Load this object from json.
     */
    private void loadConfig(JsonValue json) {
        phaseType = PhaseType.valueOf(json.getString("phaseType"));
        JsonValue jsonAttrList = json.get("attributeList");
        for (JsonValue attr: jsonAttrList.iterator()) {
            PhaseAttribute pa = new PhaseAttribute(attr);
            //pa.loadConfig(attr);
            attributeList.add(pa);
        }
    }

    /**
     * deep clone the PhaseSchema object.
     * @return
     */
    public final PhaseSchema dodleClone() {
        PhaseSchema clone = new PhaseSchema();
        clone.phaseType = PhaseType.valueOf(this.phaseType.name());

        for (PhaseAttribute attr: this.attributeList) {
            clone.getAttributeList().add(attr.dodleClone());
        }
        return clone;
    }
}
