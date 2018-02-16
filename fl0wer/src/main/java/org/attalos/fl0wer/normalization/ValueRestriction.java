package org.attalos.fl0wer.normalization;

import org.attalos.fl0wer.utils.HelperFunctions;
import org.attalos.fl0wer.utils.OwlToInternalTranslator;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;

import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by attalos on 4/24/17.
 */
public class ValueRestriction implements  Node_Res{
    private LinkedList<Role> role_word;
    private Node_Con concept;

    public ValueRestriction(Role role, Node_Con concept) {
        role_word = new LinkedList<>();
        role_word.add(role);
        this.concept = concept;
    }

    public ValueRestriction(OWLObjectAllValuesFrom owl_valueRes, OwlToInternalTranslator o2iTranslator) {
        role_word = new LinkedList<>();
        //role_word.add(new Role(owl_valueRes.getProperty()));
        role_word.add(o2iTranslator.translate(owl_valueRes.getProperty()));

        ConceptDescription concept_description = Ontology.owlClass_to_conceptDescription(owl_valueRes.getFiller(), o2iTranslator);
        if (concept_description instanceof ValueRestriction) {
            role_word.addAll(((ValueRestriction) concept_description).getRole_word());
            concept = ((ValueRestriction) concept_description).getConcept();
        } else if(concept_description instanceof Node_Con) {
            concept = (Node_Con) concept_description;
        } else {
            //System.out.println("problem while value restriction");
            System.out.println(concept);
            System.out.println(owl_valueRes);
            System.out.println(HelperFunctions.toString_expression(owl_valueRes));
            throw new RuntimeException("Problem while creating ValueRestricton from owl");
        }
    }

    @Override
    public String toString() {
        return "\u2200" + role_word.stream().map(Object::toString).collect(Collectors.joining("-"))
                + "." + concept;
    }

    public ValueRestriction(LinkedList<Role> role_word, Node_Con concept) {
        this.role_word = role_word;
        this.concept = concept;
    }

    public void appendRole(Role role) {
        role_word.add(role);
    }

    public void setRole_word(LinkedList<Role> role_word) {
        this.role_word = role_word;
    }

    public void setConcept(Node_Con concept) {
        this.concept = concept;
    }

    public LinkedList<Role> getRole_word() {

        return role_word;
    }

    public Node_Con getConcept() {
        return concept;
    }

    @Override
    public ValueRestriction prepand_roleword(LinkedList<Role> roleword) {
        //roleword.addAll(this.role_word);
        //this.role_word = roleword;
        this.role_word.addAll(0, roleword);
        return this;
    }

    @Override
    public Node_Res normalize_nf11_to_nf13() {
        if (concept.equals(Top.getInstance())) {
            return Top.getInstance();
        }

        concept.normalize_nf11_to_nf13();
        return this;
    }

    @Override
    public Return_Node_Con normalize_one_step_nf14() {
        if(! (this.concept instanceof Conjunction) ) {
            return new Return_Node_Con(new Conjunction(this), false);
        }
        Conjunction class_concept = (Conjunction) this.concept;
        Conjunction new_conjunction = new Conjunction();
        class_concept.getConjuncts().forEach(conjunct -> {
            new_conjunction.appendConjunct(conjunct.prepand_roleword(this.role_word));
        });
        return new Return_Node_Con(new_conjunction, true);

    }

    @Override
    public boolean is_nf2_normalized() {
        return (this.role_word.size() == 1);
    }
}
