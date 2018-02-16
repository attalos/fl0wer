package org.attalos.fl0wer.normalization;

import org.attalos.fl0wer.App;
import org.attalos.fl0wer.utils.OwlToInternalTranslator;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by attalos on 4/24/17.
 */
public class Conjunction implements Node_Con {
    Set<Node_Res> conjuncts;

    @Override
    public String toString() {
        return "("
                + conjuncts.stream().map(Object::toString).collect(Collectors.joining(" \u2293 "))
                + ")";
    }

    public Conjunction() {
        conjuncts = new HashSet<>();
    }


    public Conjunction(Node_Res conjunct) {
        conjuncts = new HashSet<>();
        conjuncts.add(conjunct);
    }

    public Conjunction(Node_Res conjunct1, Node_Res conjunct2) {
        conjuncts = new HashSet<>();
        conjuncts.add(conjunct1);
        conjuncts.add(conjunct2);
    }

    public Conjunction(HashSet<Node_Res> conjuncts) {

        this.conjuncts = conjuncts;
    }

    public Conjunction (OWLObjectIntersectionOf owl_intersection, OwlToInternalTranslator o2iTranslator) {
        /*if(owl_intersection instanceof OWLClass) {
            return owlNamedConcept_toNamedConcept((OWLClass) owl_intersection);
        }*/
        conjuncts = new HashSet<>();
        owl_intersection.conjunctSet().forEach(conjunct -> {
            ConceptDescription concept = Ontology.owlClass_to_conceptDescription(conjunct, o2iTranslator);

            if(concept instanceof Conjunction) {
                conjuncts.addAll(((Conjunction) concept).getConjuncts());
            } else if(concept instanceof Node_Res) {
                conjuncts.add((Node_Res) concept);
            } else {
                System.out.println(concept);
                System.out.println(owl_intersection);
                System.out.println(App.toString_expression(owl_intersection));
                throw new RuntimeException("Problem while creating Conjunction from owl");
                //System.out.println("problem while creating conjunction");
            }
        });
    }

    public void appendConjunct(Node_Res conjunct) {
        conjuncts.add(conjunct);
    }

    public void remove_Conjunct(Node_Res conjunct) { conjuncts.remove(conjunct); }

    public void join_Node_Con(Node_Con conjunction) {
        if (conjunction instanceof Node_Res) {
            this.conjuncts.add((Node_Res) conjunction);
        } else if (conjunction instanceof Conjunction) {
            this.conjuncts.addAll(((Conjunction) conjunction).getConjuncts());
        } else {
            throw new RuntimeException("Conjunction should be the only Node_Con that is not also Node_Res");
        }
    }

    public Set<Node_Res> getConjuncts() {
        return conjuncts;
    }

    public void setConjuncts(Set<Node_Res> conjuncts) {
        this.conjuncts = conjuncts;
    }

    @Override
    public ConceptDescription normalize_nf11_to_nf13() {
        conjuncts.forEach(node_res -> node_res.normalize_nf11_to_nf13());
        if (conjuncts.contains(Top.getInstance()) && conjuncts.size() != 1) {
            conjuncts.remove(Top.getInstance());
        }
        if (conjuncts.size() == 1) {
            Iterator<Node_Res> i = conjuncts.iterator();
            if (i.hasNext()) {
                return i.next();
            }
        }

        return this;
    }

    @Override
    public Return_Node_Con normalize_one_step_nf14() {
        //conjuncts.stream().anyMatch(conjunct -> conjunct.normalize_one_step_nf14().isSomething_Changed());
        Return_Node_Con own_return_value = new Return_Node_Con(this, false);

        Iterator<Node_Res> iterator = conjuncts.iterator();
        Conjunction temp = new Conjunction();

        while(iterator.hasNext()) {
        //conjuncts.forEach(conjunct -> {
            Node_Res conjunct = iterator.next();
            Return_Node_Con returnValue = conjunct.normalize_one_step_nf14();
            if (returnValue.isSomething_Changed()) {
                iterator.remove();
                temp.join_Node_Con(returnValue.getConjunction());
                own_return_value.setSomething_Changed(true);
            }
        }

        this.join_Node_Con(temp);
        return own_return_value;
    }

    @Override
    public boolean is_nf2_normalized() {
        return this.conjuncts.stream().allMatch(
                conjunct -> conjunct.is_nf2_normalized()
        );
    }
}
