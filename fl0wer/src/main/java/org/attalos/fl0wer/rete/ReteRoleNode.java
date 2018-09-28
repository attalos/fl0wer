package org.attalos.fl0wer.rete;

import org.attalos.fl0wer.utils.HelperFunctions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by attalos on 6/25/17.
 */
public class ReteRoleNode implements ReteNode {

    private int required_rolename;
    private ArrayList<ReteNode> successors;

    ReteRoleNode(int required_rolename, ReteNode final_node) {
        this.required_rolename = required_rolename;
        this.successors = new ArrayList<>();
        this.successors.add(final_node);
    }

    /*public void add_successor(ReteNode successor) {
        this.successors.add(successor);
    }*/

    @Override
    public boolean represents_concept(long concept) {
        return false;
    }

    @Override
    public void integrate_new_node(int rolename, ArrayList<Integer> needed_concepts, ReteNode final_node) {
        throw new RuntimeException("This function should never get called (ReteRoleNode)");
    }

    @Override
    public void propagate_domain_elem(BigInteger elem_index, int rolename, Collection<Integer> domain_elem, int num_of_roles, WorkingMemory wm) {
        if (rolename == this.required_rolename && !elem_index.equals(BigInteger.ZERO)) {
            for (ReteNode s : successors) {
                s.propagate_domain_elem(
                        HelperFunctions.calculatePartentId(elem_index, rolename, BigInteger.valueOf(num_of_roles)),
                        rolename, domain_elem, num_of_roles, wm);
            }
        } else if (this.required_rolename == -1) {
            for (ReteNode s : successors) {
                s.propagate_domain_elem(elem_index, -1, domain_elem, num_of_roles, wm);
            }
        }
    }

    @Override
    public String to_dot_graph(ArrayList<String> top_level, ArrayList<String> role_level, ArrayList<String> eq_level, ArrayList<String> gci_level, Integer predecessor_identifier) {
        String this_hash = Integer.toString(this.hashCode());
        StringBuilder dot_string = new StringBuilder(this_hash + "[label=\"r" + this.required_rolename + "\"]\n");
        role_level.add(this_hash);
        for (ReteNode successor:successors) {
            dot_string.append(this_hash).append(" -> ").append(successor.hashCode()).append("\n");
            dot_string.append(successor.to_dot_graph(top_level, role_level, eq_level, gci_level, this.required_rolename));

        }
        return dot_string.toString();
    }
}
