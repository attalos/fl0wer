package org.attalos.fl0ReasonerEvaluation.evaluation.correctness;

import org.attalos.fl0ReasonerEvaluation.helpers.Tuple;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.NodeSet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassificationAnswer implements ReasonerAnswer {
    private List<String> answer;

    public ClassificationAnswer(Map<OWLClass, Stream<OWLClass>> answer) {
        this.answer = answer.entrySet().stream()
                .filter(e -> !e.getKey().equals(OWLManager.getOWLDataFactory().getOWLNothing()))
                .map(e -> {
                    String owlClass = e.getKey().toString();
                    SubsumersetAnswer subsumerset = new SubsumersetAnswer(e.getValue());
                    return owlClass + ":  " + subsumerset.toRepresentativeShortForm();
                })
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public String toRepresentativeShortForm() {
//        answer.forEach(System.out::println);
        return "hash:" + Integer.toHexString(answer.hashCode());
    }
}
