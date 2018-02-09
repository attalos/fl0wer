package org.attalos.fl0wer.normalization;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by attalos on 4/25/17.
 */
public class GCI {
    private ConceptDescription subClass;
    private ConceptDescription superClass;

    public GCI(ConceptDescription subClass, ConceptDescription superClass) {

        this.subClass = subClass;
        this.superClass = superClass;
    }

    public ConceptDescription getSubClass() {
        return subClass;
    }

    public ConceptDescription getSuperClass() {
        return superClass;
    }

    @Override
    public String toString() {
        return subClass + " \u2291 " + superClass;
    }

    public void setSubClass(ConceptDescription subClass) {
        this.subClass = subClass;
    }

    public void normalize (Ontology ontology) {
        normalize_nf11_to_nf13();
        normalize_nf14();
        normalize_nf21(ontology);
        normalize_nf22(ontology);
    }

    private void normalize_nf11_to_nf13() {
        subClass.normalize_nf11_to_nf13();
        superClass.normalize_nf11_to_nf13();
    }

    private void normalize_nf14() {
        boolean something_changed = true;

        while (something_changed) {
            Return_Node_Con return_value = superClass.normalize_one_step_nf14();
            superClass = return_value.getConjunction();
            something_changed = return_value.isSomething_Changed();
        }

        something_changed = true;

        while (something_changed) {
            Return_Node_Con return_value = subClass.normalize_one_step_nf14();
            subClass = return_value.getConjunction();
            something_changed = return_value.isSomething_Changed();
        }
    }

    /*
    private Node_Con normalize_nf14(ConceptDescription concept) {
        Node_Con normalized_concept;

        if (not (concept instanceof Node_Con)) {
            if (concept instanceof ValueRestriction) {

            } else {
                throw new RuntimeException("ValueRestriction should be the only ConceptDescription that is not a Node_con");
            }
        }
        return normalized_concept;
    }
    */

    private void normalize_nf21(Ontology o) {
        if(! (subClass instanceof NamedConcept || subClass instanceof  Top) ) {
            normalize_nf2((Conjunction) this.subClass, o, true);
        }
    }

    private void normalize_nf22(Ontology o) {
        if(! (superClass instanceof NamedConcept || superClass instanceof  Top) ) {
            normalize_nf2((Conjunction) this.superClass, o, false);
        }
    }

    private void normalize_nf2(Conjunction conjunction, Ontology ontology, boolean is_nf21) {
        Concept_Factory factory = Concept_Factory.getInstance();

        Iterator<Node_Res> iterator = conjunction.getConjuncts().iterator();
        Conjunction new_conjuncts = new Conjunction();

        while( iterator.hasNext() ) {
            Node_Res conjunct = iterator.next();

            if( conjunct instanceof ValueRestriction ) {
                ValueRestriction value_restriction = (ValueRestriction) conjunct;
                LinkedList<Role> role_word = value_restriction.getRole_word();

                if ( role_word.size() > 1 ) {
                    if( !(value_restriction.getConcept() instanceof NamedConcept)) {
                        throw new RuntimeException("After normalisation phase 1 the should be only named concepts in ValueRestrictions");
                    }
                    //NamedConcept orig_concept = (NamedConcept) value_restriction.getConcept();
                    //NamedConcept new_concept1 = factory.get_new_concept();
                    NamedConcept new_concept1 = (NamedConcept) value_restriction.getConcept();
                    NamedConcept new_concept2;

                    //conjunction.remove_Conjunct(conjunct);
                    //conjunction.appendConjunct(new ValueRestriction(role_word.pop(), new_concept1));

                    for (int i = role_word.size(); i > 1; i--) {
                        new_concept2 = new_concept1;
                        new_concept1 = factory.get_new_concept();
                        Role role = role_word.remove(role_word.size() - 1);
                        ConceptDescription val_res_concept = new ValueRestriction(role, new_concept2);

                        if(is_nf21) {
                            ontology.add_gci(new GCI(val_res_concept, new_concept1));
                        } else {
                            ontology.add_gci(new GCI(new_concept1, val_res_concept));
                        }
                    }

                    iterator.remove();
                    new_conjuncts.appendConjunct(new ValueRestriction(role_word.pop(), new_concept1));
                    int i = 1;
                }
            }
        }

        conjunction.join_Node_Con(new_conjuncts);
    }
}
