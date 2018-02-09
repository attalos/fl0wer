package org.attalos.owlTest.controll;

import org.attalos.owlTest.controll.SmallestFunctionalModelTree;
import org.attalos.owlTest.subsumption.ApplicableRule;

import java.util.*;

/**
 * Created by attalos on 7/2/17.
 */
public class FunctionalElement {
    private Set<Integer> concepts;
    private boolean directly_blocking;
    private boolean directly_blocked;
    private boolean indirectly_blocked;

    private List<ApplicableRule> hold_back_rules;

    protected FunctionalElement(Collection<Integer> concepts) {
        this.concepts = new HashSet<>(concepts);
        this.directly_blocking = false;
        this.directly_blocked = false;
        this.indirectly_blocked = false;
    }

    /**
     *
     * @param new_concepts
     * @return true, if there are concepts in new_concepts which are not in the current concept set
     */
    protected boolean would_change_elem(Collection<Integer> new_concepts) {
        return !concepts.containsAll(new_concepts);
    }

    protected void add_to_concepts(Collection<Integer> new_concepts) {
        this.concepts.addAll(new_concepts);
    }

    protected void set_directly_blocked(boolean directly_blocked) {
        this.directly_blocked = directly_blocked;
        if (!this.is_blocked()) {
            this.release_rules();
        }
    }

    protected void set_indirectly_blocked(boolean indirectly_blocked) {
        this.indirectly_blocked = indirectly_blocked;
        if (!this.is_blocked()) {
            this.release_rules();
        }
    }

    protected void set_directly_blocking(boolean is_directly_blocking) {
        this.directly_blocking = is_directly_blocking;
    }

    protected Set<Integer> getConcepts() {
        return concepts;
    }

    protected boolean is_blocked() {
        return directly_blocked || indirectly_blocked;
    }

    protected boolean is_directly_blocked() {
        return directly_blocked;
    }

    protected boolean is_indirectly_blocked() {
        return indirectly_blocked;
    }

    protected boolean is_directly_blocking() {
        return directly_blocking;
    }

    protected void insert_rule_to_hold_back(ApplicableRule rule_to_hold_back) {
        if (this.hold_back_rules == null) {
            this.hold_back_rules = new ArrayList<>();
        }

        this.hold_back_rules.add(rule_to_hold_back);
    }

    private void release_rules() {
        if (hold_back_rules != null) {
            FL_0_subsumption.get_instance().reenter_rules_to_queue(hold_back_rules);
            hold_back_rules = null;
        }
    }

    @Override
    public String toString() {
        return concepts.toString();
    }
}
