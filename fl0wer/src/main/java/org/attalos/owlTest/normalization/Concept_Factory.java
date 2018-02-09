package org.attalos.owlTest.normalization;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import java.util.*;

/**
 * Created by attalos on 6/6/17.
 */
public class Concept_Factory {
    private static Concept_Factory ourInstance = new Concept_Factory();

    HashMap<OWLClass, NamedConcept> concept_map = new HashMap<>();
    HashMap<OWLObjectPropertyExpression, Role> role_map = new HashMap<>();

    //private static long new_concept_counter = 0;
    private static int concept_counter = 0;
    private static int owl_role_counter = 0;

    public static Concept_Factory getInstance() {
        return ourInstance;
    }

    private Concept_Factory() {
    }

    public synchronized NamedConcept get_new_concept() {
        return new NamedConcept(concept_counter++);
    }

    public synchronized NamedConcept get_concept_from_owl_class(OWLClass owl_class) {
        if ( !concept_map.containsKey(owl_class) ) {
            concept_map.put(owl_class, new NamedConcept(concept_counter++));
        }

        return concept_map.get(owl_class);
    }

    public synchronized Role get_role_from_owl_property(OWLObjectPropertyExpression owl_property) {
        if ( !role_map.containsKey(owl_property) ) {
            role_map.put(owl_property, new Role(owl_role_counter++));
        }

        return role_map.get(owl_property);
    }

    public int get_role_num() {
        return owl_role_counter;
    }

    public int get_concept_num() {
        return concept_counter;
    }

    public NamedConcept translate_owl_class_to_named_concept(OWLClass owl_class) {
        return this.concept_map.get(owl_class);
    }

    public List<OWLClass> translate_int_to_OWLClass(Collection<Integer> int_collection) {
        List<OWLClass> return_set = new ArrayList<>();

        for (Map.Entry<OWLClass, NamedConcept> pair : concept_map.entrySet()) {
            if (int_collection.contains(pair.getValue().getConcept_name())) {
                return_set.add(pair.getKey());
            }
        }

        return return_set;
    }
}
