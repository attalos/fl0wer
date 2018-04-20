package org.attalos.fl0wer.utils;

import org.attalos.fl0wer.normalization.NamedConcept;
import org.attalos.fl0wer.normalization.Role;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by attalos on 2/16/18.
 */
public class OwlToInternalTranslator {
    private HashMap<OWLClass, NamedConcept> concept_map = new HashMap<>();
    private HashMap<Integer, OWLClass> reverse_concept_map = new HashMap<>();

    private HashMap<OWLObjectPropertyExpression, Role> role_map = new HashMap<>();

    private int concept_counter = 0;
    private int owl_role_counter = 0;
    private boolean locked = false;

    public OwlToInternalTranslator() {
    }

    public void lock() {
        locked = true;
    }

    public void initialize_original_owl_classes(Stream<OWLClass> original_owl_classes) {

        original_owl_classes.forEach(class_owl -> {
            if ( !concept_map.containsKey(class_owl) ) {
                NamedConcept namedConcept = new NamedConcept(concept_counter++);
                concept_map.put(class_owl, namedConcept);
                reverse_concept_map.put(namedConcept.getConcept_name(), class_owl);
            }
        });

    }

    public synchronized NamedConcept get_new() {
        if (locked) {
            throw new RuntimeException("tried to get new concept name, after translator got locked");
        }
        return new NamedConcept(concept_counter++);
    }

    /**
     *
     * @param owl_class to translate
     * @return resulting named concept or null if it wasn't found
     */
    public NamedConcept translate(OWLClass owl_class) {
        return this.concept_map.get(owl_class);
    }

    public synchronized Role translate(OWLObjectPropertyExpression owl_property) {
        if (locked) {
            throw new RuntimeException("tried to get new role name, after translator got locked");
        }

        if ( !role_map.containsKey(owl_property) ) {
            role_map.put(owl_property, new Role(owl_role_counter++));
        }

        return role_map.get(owl_property);
    }

    public int get_role_count() {
        if (!locked) {
            throw new RuntimeException("tried to get role count before locking the translator");
        }

        return owl_role_counter;
    }

    public int get_concept_count() {
        if (!locked) {
            throw new RuntimeException("tried to get concept count before locking the translator");
        }

        return concept_counter;
    }


    public List<OWLClass> translate_reverse(Collection<Integer> int_collection) {
        List<OWLClass> return_set = new ArrayList<>();
        for (Integer currentConcept : int_collection) {
            OWLClass original_class = reverse_concept_map.get(currentConcept);
            if (original_class != null) {
                return_set.add(original_class);
            }
        }

        return return_set;
    }

    @Deprecated
    public List<OWLClass> translate_reverse_0(Collection<Integer> int_collection) {
        List<OWLClass> return_set = new ArrayList<>();

        for (Map.Entry<OWLClass, NamedConcept> pair : concept_map.entrySet()) {
            if (int_collection.contains(pair.getValue().getConcept_name())) {
                return_set.add(pair.getKey());
            }
        }

        return return_set;
    }
}
