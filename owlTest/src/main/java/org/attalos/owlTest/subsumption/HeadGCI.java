package org.attalos.owlTest.subsumption;

import org.attalos.owlTest.normalization.Conjunction;
import org.attalos.owlTest.normalization.GCI;

/**
 * Created by attalos on 6/8/17.
 */
public class HeadGCI implements Comparable<HeadGCI> {
    ConceptHead subConceptHead;
    ConceptHead superConceptHead;

    public HeadGCI(ConceptHead subConceptHead, ConceptHead superConceptHead) {
        this.subConceptHead = subConceptHead;
        this.superConceptHead = superConceptHead;
    }

    public HeadGCI(GCI gci, int num_of_roles) {
        this.subConceptHead = new ConceptHead(gci.getSubClass(), num_of_roles);
        this.superConceptHead = new ConceptHead(gci.getSuperClass(), num_of_roles);
    }

    public ConceptHead get_subConceptHead() {
        return subConceptHead;
    }

    public ConceptHead get_superConceptHead() {
        return superConceptHead;
    }

    @Override
    public String toString() {
        return subConceptHead + " \u2291 " + superConceptHead;
    }

    @Override
    public int compareTo(HeadGCI other) {
        return this.subConceptHead.compareTo(other.get_subConceptHead());
    }
}
