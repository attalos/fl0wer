package org.attalos.fl0wer.rete;

import org.attalos.fl0wer.subsumption.ApplicableRule;
import org.attalos.fl0wer.subsumption.ConceptHead;

import java.util.*;

/**
 * Created by attalos on 6/25/17.
 */
public class ReteFinalNode implements ReteNode {
    private ConceptHead fired_result;
    private Set<ApplicableRule> already_fired_rules;


    public ReteFinalNode(ConceptHead fired_result) {
        this.fired_result = fired_result;
        this.already_fired_rules = new HashSet<>();
    }

    @Override
    public boolean represents_concept(long concept) {
        return false;
    }

    @Override
    public void integrate_new_node(int rolename, ArrayList<Integer> needed_concepts, ReteNode final_node) {
        throw new RuntimeException("This function should never get called (ReteFinalNode)");
    }

    @Override
    public void propagate_domain_elem(Long elem_index, int rolename, Collection<Integer> domain_elem, int num_of_roles, WorkingMemory wm) {
        ApplicableRule resulting_applicable_rule = new ApplicableRule(elem_index, fired_result);
        if (already_fired_rules.add(resulting_applicable_rule)) {
            wm.offer_rule(resulting_applicable_rule);
        }
    }

    @Override
    public String to_dot_graph(ArrayList<String> top_level, ArrayList<String> role_level, ArrayList<String> eq_level, ArrayList<String> gci_level, Integer predecessor_identifier) {
        String this_hash = Integer.toString(this.hashCode());
        String dot_string = this_hash + "[label=\"" + fired_result.toString() + "\"]\n";
        dot_string += this_hash + "[shape=box]\n";
        gci_level.add(this_hash);
        return dot_string;
    }
}
