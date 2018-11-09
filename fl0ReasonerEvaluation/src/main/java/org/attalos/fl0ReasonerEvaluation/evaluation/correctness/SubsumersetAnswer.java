package org.attalos.fl0ReasonerEvaluation.evaluation.correctness;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.NodeSet;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubsumersetAnswer implements ReasonerAnswer {
    private List<String> answer;

    public SubsumersetAnswer(NodeSet<OWLClass> answer) {
        this.answer = answer.entities()
                .filter(owlClass -> !owlClass.equals(OWLManager.getOWLDataFactory().getOWLThing()))
                .map(OWLClass::toString)
                .sorted()
                .collect(Collectors.toList());
    }

    public SubsumersetAnswer(List<OWLClass> answer, OWLClass baseClass) {
        this.answer = answer.stream()
                .filter(owlClass -> !owlClass.equals(baseClass))
                .map(OWLClass::toString)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public String toRepresentativeShortForm() {
        return "hash:" + Integer.toHexString(answer.hashCode());
    }
}
