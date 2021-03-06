package org.attalos.fl0wer.subsumption;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Created by attalos on 6/27/17.
 */
public class ApplicableRule implements Comparable<ApplicableRule> {
    private BigInteger node_id;
    private ConceptHead rule_right_side;


    public ApplicableRule(BigInteger node_id, ConceptHead rule_right_side) {
        this.node_id = node_id;
        this.rule_right_side = rule_right_side;
    }

    public BigInteger get_node_id() {
        return node_id;
    }

    public ConceptHead get_rule_right_side() {
        return rule_right_side;
    }

    @Override
    public int compareTo(ApplicableRule applicableRule) {
        return this.node_id.compareTo(applicableRule.node_id);
        //return Long.compare(this.node_id, applicableRule.node_id);
        /*if (this.node_id != applicableRule.node_id) {
            return Long.compare(this.node_id, applicableRule.node_id);
        } else {
            return rule_right_side.compareTo(applicableRule.rule_right_side);
        }*/
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicableRule that = (ApplicableRule) o;
        return node_id.equals(that.node_id) &&
                Objects.equals(rule_right_side, that.rule_right_side);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node_id, rule_right_side);
    }
}
