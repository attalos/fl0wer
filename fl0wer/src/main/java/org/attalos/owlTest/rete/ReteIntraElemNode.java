package org.attalos.owlTest.rete;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by attalos on 6/25/17.
 */
public class ReteIntraElemNode implements ReteNode{

    int needed_concept;
    ArrayList<ReteNode> successors;

    public ReteIntraElemNode(int rolename, ArrayList<Integer> needed_concepts, ReteNode final_node) {
        this.needed_concept = needed_concepts.remove(0);

        this.successors = new ArrayList<>();

        if (needed_concepts.size() >= 1) {
            this.successors.add(new ReteIntraElemNode(rolename, needed_concepts, final_node));
        } else if (rolename != -1) {
            this.successors.add(new ReteRoleNode(rolename, final_node));
        } else {
            this.successors.add(final_node);
        }
    }

    @Override
    public boolean represents_concept(long concept) {
        return this.needed_concept == concept;
    }

    @Override
    public void integrate_new_node(int rolename, ArrayList<Integer> needed_concepts, ReteNode final_node) {
        needed_concepts.remove(0);

        if (needed_concepts.size() >= 1) {
            for (ReteNode successor : successors) {
                if (successor.represents_concept(needed_concepts.get(0))) {
                    successor.integrate_new_node(rolename, needed_concepts, final_node);
                    return;
                }
            }
/*            successors.forEach(successor -> {
                if (successor.represents_concept(needed_concepts.get(0))) {
                    successor.integrate_new_node(rolename, needed_concepts, final_node);
                    return;
                }
            });*/
            this.successors.add(new ReteIntraElemNode(rolename, needed_concepts, final_node));
        } else if (rolename != -1) {
            this.successors.add(new ReteRoleNode(rolename, final_node));
        } else {
            this.successors.add(final_node);
        }
    }

    @Override
    public void propagate_domain_elem(Long elem_index, int rolename, Collection<Integer> domain_elem, int num_of_roles) {
        if (domain_elem.contains(needed_concept)) {
            //successors.forEach(successor -> successor.propagate_domain_elem(elem_index, rolename, domain_elem, num_of_roles));
            for (ReteNode successor : successors) {
                successor.propagate_domain_elem(elem_index, rolename, domain_elem, num_of_roles);
            }
        }
    }

    @Override
    public String to_dot_graph(ArrayList<String> top_level, ArrayList<String> role_level, ArrayList<String> eq_level, ArrayList<String> gci_level, Integer predecessor_identifier) {
        String this_hash = Integer.toString(this.hashCode());
        String dot_string = this_hash + "[label=A" + this.needed_concept + "]\n";
        for (ReteNode successor:successors) {
            dot_string += this_hash + " -> " + Integer.toString(successor.hashCode()) + "\n";
            dot_string += successor.to_dot_graph(top_level, role_level, eq_level, gci_level, -1);

        }
        return dot_string;
    }
}
