package org.attalos.fl0wer.subsumption;

import org.attalos.fl0wer.normalization.GCI;

/**
 * Created by attalos on 6/8/17.
 */
public class HeadGCI implements Comparable<HeadGCI> {
    private ConceptHead subConceptHead;
    private ConceptHead superConceptHead;

    HeadGCI(ConceptHead subConceptHead, ConceptHead superConceptHead) {
        this.subConceptHead = subConceptHead;
        this.superConceptHead = superConceptHead;
    }

    HeadGCI(GCI gci, int num_of_roles) {
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
