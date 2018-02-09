package org.attalos.fl0wer.subsumption;

/**
 * Created by attalos on 6/27/17.
 */
public class ApplicableRule implements Comparable<ApplicableRule> {
    private long node_id;
    private ConceptHead rule_right_side;


    public ApplicableRule(long node_id, ConceptHead rule_right_side) {
        this.node_id = node_id;
        this.rule_right_side = rule_right_side;
    }

    public long get_node_id() {
        return node_id;
    }

    public ConceptHead get_rule_right_side() {
        return rule_right_side;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof ApplicableRule)) {
            return false;
        }
        ApplicableRule other = (ApplicableRule) o;
        return (this.node_id == other.node_id) && (this.rule_right_side == other.rule_right_side);
    }

    @Override
    public int compareTo(ApplicableRule applicableRule) {
        if (this.node_id != applicableRule.node_id) {
            return Long.compare(this.node_id, applicableRule.node_id);
        } else {
            return rule_right_side.compareTo(applicableRule.rule_right_side);
        }
    }
}
