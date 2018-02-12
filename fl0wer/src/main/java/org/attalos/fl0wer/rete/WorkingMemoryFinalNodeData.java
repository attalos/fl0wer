package org.attalos.fl0wer.rete;

import org.attalos.fl0wer.subsumption.ApplicableRule;

import java.util.HashSet;
import java.util.Set;

public class WorkingMemoryFinalNodeData {
    private Set<ApplicableRule> already_fired_rules;

    protected WorkingMemoryFinalNodeData() {
        already_fired_rules = new HashSet<>();
    }

    /**
     *
     * @param rule which got fired
     * @return true, if this rule is new (returnvalue of java.util.HashSet.add( ... ))
     */
    protected boolean add_fired_ApplicableRule(ApplicableRule rule) {
        return already_fired_rules.add(rule);
    }
}
