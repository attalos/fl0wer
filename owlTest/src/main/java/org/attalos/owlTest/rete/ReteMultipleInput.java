package org.attalos.owlTest.rete;

import org.attalos.owlTest.subsumption.ConceptHead;

import java.util.*;

/**
 * Created by attalos on 6/25/17.
 */
public class ReteMultipleInput implements  ReteNode {
    private ArrayList<Integer> predecessor_identifier;              //required roles for this element (-1 means no role)
    private ReteFinalNode successor;
    private Map<Long, List<Boolean>> current_elements;

    /*public ReteMultipleInput(ConceptHead concept_head) {
        predecessor_identifier = concept_head.get_not_null_sucessor_rolenames();
        predecessor_identifier.forEach(predecessor_identifier -> {
            new ReteIntraElemNode(predecessor_identifier, concept_head.get_concept_set_at(predecessor_identifier), this);
        });
    }*/


    public ReteMultipleInput(ArrayList<Integer> predecessor_identifier, ReteFinalNode final_node) {
        this.predecessor_identifier = predecessor_identifier;
        this.successor = final_node;
        this.current_elements = new HashMap<>();
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
    public void propagate_domain_elem(Long elem_index, int rolename, Collection<Integer> domain_elem, int num_of_roles) {

        int index_of_role = predecessor_identifier.indexOf(rolename);
        if (index_of_role == -1) {
            return;
        }

        List<Boolean> current_element;

        if (!current_elements.containsKey(elem_index)) {
            current_element = new ArrayList<>(Collections.nCopies(predecessor_identifier.size(), false));
            current_elements.put(elem_index, current_element);
        } else {
            current_element = current_elements.get(elem_index);
        }

        current_element.set(index_of_role, true);

        // all path are full filled
        if (current_element.stream().allMatch(Boolean::booleanValue)) {
            successor.propagate_domain_elem(elem_index, -1, null, num_of_roles);
            this.current_elements.remove(elem_index);
        }
    }

    @Override
    public String to_dot_graph(ArrayList<String> top_level, ArrayList<String> role_level, ArrayList<String> eq_level, ArrayList<String> gci_level, Integer predecessor_identifier) {
        if (predecessor_identifier == this.predecessor_identifier.get(0)) {
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
