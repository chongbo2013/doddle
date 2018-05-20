package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.DodleEngine;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PHNode;

import java.util.Stack;

/**
 * A PhaseHierchy is basically a directory structure of all folders that you have traverse as you
 * decent "down" into other folders.  Able to use this for the hierarchy as you browse through the Phases
 * as well as for recording where in the hierarchy you have placed a Phase.
 */
public class PhaseHierarchy {
    private final Stack<PHNode> hierarchyStack = new Stack<PHNode>();

    public PhaseHierarchy() {
    }

    public PhaseHierarchy(JsonValue json) {
        loadConfig(json);
    }

    /**
     * get the hierarchyStack.
     * @return
     */
    public final Stack<PHNode> getHierarchyStack() {
        return hierarchyStack;
    }
    
    /**
     * Returns the node for the given attribute type.
     */
    public final PHNode getNodeForAttribute(PhaseAttribute attribute) {
        for (PHNode node : hierarchyStack) {
            if (node.getAttributeType() == attribute.getPhaseAttributeType()) {
                return node;
            }
        }
        
        return null;
    }

    /**
     * serialize object to Json.
     * @param json
     */
    public final void writeConfig(Json json) {
        json.writeArrayStart("hierarchyStack");
        for (PHNode node : hierarchyStack) {
            json.writeObjectStart();
            node.writeConfig(json);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
    }

    /**
     * deserialize object from json.
     * @param json
     */
    private void loadConfig(JsonValue json) {
        JsonValue jsonHierarchy = json.get("hierarchyStack");
        for (int i = 0; i < jsonHierarchy.size; i++) {
            PHNode n = new PHNode(jsonHierarchy.get(i));
            hierarchyStack.push(n);
        }
    }

    /**
     * execute a deep clone of the stack.
     * @return
     */
    public final PhaseHierarchy dodleClone() {
        PhaseHierarchy ph = new PhaseHierarchy();
        for (PHNode node : hierarchyStack) {
            PHNode nodeClone = node.dodleClone();
            ph.getHierarchyStack().push(nodeClone);
        }
        return ph;
    }

    /**
     * specialized toString to dump out the entire stack.
     * @return
     */
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        for (PHNode node : hierarchyStack) {
            sb.append(node.toString() + "\n");
        }
        return sb.toString();
    }


    /**
     * show two stacks.
     * @param h1
     * @param h2
     */
    public static final void showTwoHierarchies(PhaseHierarchy h1, PhaseHierarchy h2) {
        if (h1 != null) {
            DodleEngine.getLogger().log("PhaseHierarchy", "==Hierarchy Stack==");
            DodleEngine.getLogger().log("PhaseHierarchy", h1.toString());
            if (h2 != null) {
                DodleEngine.getLogger().log("PhaseHierarchy", "==Phase Stack==");
                DodleEngine.getLogger().log("PhaseHierarchy", h2.toString());
            }
        }
    }

}
