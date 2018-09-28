package org.attalos.fl0wer.rete;

import org.attalos.fl0wer.subsumption.ApplicableRule;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class WorkingMemoryFinalNodeData {
    //private Set<ApplicableRule> already_fired_rules;
    private Set<BigInteger> already_fired_rules;

    protected WorkingMemoryFinalNodeData() {
        already_fired_rules = new HashSet<>();
    }

    /**
     *
     * @param elemIdx at which the rule got fired
     * @return true, if this rule is new (returnvalue of java.util.HashSet.add( ... ))
     */
    protected boolean ruleFirstTimeFired(BigInteger elemIdx) {
        return already_fired_rules.add(elemIdx);
    }
}
