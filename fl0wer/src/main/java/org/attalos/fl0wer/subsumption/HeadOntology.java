package org.attalos.fl0wer.subsumption;

import org.attalos.fl0wer.normalization.Concept_Factory;
import org.attalos.fl0wer.normalization.GCI;
import org.attalos.fl0wer.normalization.Ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by attalos on 6/8/17.
 */
public class HeadOntology {
    ArrayList<HeadGCI> gcis;

    public HeadOntology(Ontology ontology) {
        gcis = new ArrayList<>();

        Set<GCI> assertions = ontology.getAssertions();
        int num_of_roles = Concept_Factory.getInstance().get_role_num();

        assertions.forEach(assertion -> {
            gcis.add(new HeadGCI(assertion, num_of_roles));
        });

        Collections.sort(gcis);
    }

    public ArrayList<HeadGCI> get_gcis() {
        return gcis;
    }

    @Override
    public String toString() {
        return gcis.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
}
