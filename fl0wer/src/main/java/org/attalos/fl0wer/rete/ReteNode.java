package org.attalos.fl0wer.rete;


import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by attalos on 6/25/17.
 */
public interface ReteNode {
    boolean represents_concept(long concept);
    void integrate_new_node(int rolename, ArrayList<Integer> needed_concepts, ReteNode final_node);
    void propagate_domain_elem(Long elem_index, int rolename, Collection<Integer> domain_elem, int num_of_roles, WorkingMemory wm);
    String to_dot_graph(ArrayList<String> top_level, ArrayList<String> role_level, ArrayList<String> eq_level, ArrayList<String> gci_level, Integer predecessor_identifier);
}
