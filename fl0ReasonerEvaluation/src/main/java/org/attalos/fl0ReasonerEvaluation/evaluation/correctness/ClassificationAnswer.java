package org.attalos.fl0ReasonerEvaluation.evaluation.correctness;

import org.attalos.fl0ReasonerEvaluation.helpers.Tuple;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.NodeSet;

import java.util.*;
import java.util.stream.Collectors;

public class ClassificationAnswer implements ReasonerAnswer {
    private List<Tuple<String, SubsumersetAnswer>> answer;

    /**
     *
     * @param a simply use null here - its a workarround because of "same method erasure"
     */
    public ClassificationAnswer(Map<OWLClass, NodeSet<OWLClass>> answer, Object a) {
        this.answer = answer.entrySet().stream()
                .map(e -> {
                    String owlClass = e.getKey().toString();
                    SubsumersetAnswer subsumerset = new SubsumersetAnswer(e.getValue());
                    return new Tuple<>(owlClass, subsumerset);
                })
                .sorted(Comparator.comparing(Tuple::getLeft))
                .collect(Collectors.toList());

    }

    public ClassificationAnswer(Map<OWLClass, Collection<OWLClass>> answer) {
        this.answer = answer.entrySet().stream()
                .map(e -> {
                    String owlClass = e.getKey().toString();
                    SubsumersetAnswer subsumerset = new SubsumersetAnswer(new ArrayList<>(e.getValue()), e.getKey());
                    return new Tuple<>(owlClass, subsumerset);
                })
                .sorted(Comparator.comparing(Tuple::getLeft))
                .collect(Collectors.toList());
    }

    @Override
    public String toRepresentativeShortForm() {
        return "hash:" + Integer.toHexString(answer.hashCode());
    }
}
