package org.attalos.fl0wer.rete;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by attalos on 6/25/17.
 */
public class ReteMultipleInput implements  ReteNode {
    private ArrayList<Integer> predecessor_identifier;              //required roles for this element (-1 means no role)
    private ReteFinalNode successor;
    private int node_id;

    /*public ReteMultipleInput(ConceptHead concept_head) {
        predecessor_identifier = concept_head.get_not_null_sucessor_rolenames();
        predecessor_identifier.forEach(predecessor_identifier -> {
            new ReteIntraElemNode(predecessor_identifier, concept_head.get_concept_set_at(predecessor_identifier), this);
        });
    }*/


    ReteMultipleInput(ArrayList<Integer> predecessor_identifier, ReteFinalNode final_node, int node_id) {
        this.predecessor_identifier = predecessor_identifier;
        this.successor = final_node;
        this.node_id = node_id;
    }

    @Override
    public boolean represents_concept(long concept) {
        return false;
    }

    @Override
    public void integrate_new_node(int rolename, ArrayList<Integer> needed_concepts, ReteNode final_node) {
        throw new RuntimeException("This function should never get called (ReteMultipleInput)");
    }

    @Override
    public void propagate_domain_elem(BigInteger elem_index, int rolename, Collection<Integer> domain_elem, int num_of_roles, WorkingMemory wm) {

        int index_of_role = predecessor_identifier.indexOf(rolename);
        if (index_of_role == -1) {
            return;
        }

        //if all incomming pathes are fullfilled propagate to next node
        if (wm.getMultipleInputNodeMemoryAt(this.node_id, this.predecessor_identifier.size()).elem_came_from(elem_index, index_of_role)) {
            successor.propagate_domain_elem(elem_index, -1, null, num_of_roles, wm);
        }
    }

    @Override
    public String to_dot_graph(ArrayList<String> top_level, ArrayList<String> role_level, ArrayList<String> eq_level, ArrayList<String> gci_level, Integer predecessor_identifier) {
        if (predecessor_identifier.equals(this.predecessor_identifier.get(0))) {
            String this_hash = Integer.toString(this.hashCode());
            String dot_string = this_hash + "[label=\"eq\"]\n";
            eq_level.add(this_hash);
            dot_string += this_hash + " -> " + Integer.toString(successor.hashCode()) + "\n";
            dot_string += successor.to_dot_graph(top_level, role_level, eq_level, gci_level, -1);
            return dot_string;
        }
        return "";
    }
}
