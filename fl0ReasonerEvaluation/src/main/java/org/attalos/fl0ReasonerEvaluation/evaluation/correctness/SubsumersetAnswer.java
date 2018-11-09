package org.attalos.fl0ReasonerEvaluation.evaluation.correctness;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;

public class SubsumersetAnswer implements ReasonerAnswer {
    private List<String> answer;

    public SubsumersetAnswer(NodeSet answer) {
        Stream<Object> entities = answer.entities();
        this.answer = entities
                .filter(e -> e instanceof  OWLClass)
                .map(e -> (OWLClass) e)
                .filter(owlClass -> !owlClass.equals(OWLManager.getOWLDataFactory().getOWLThing()))
                .map(OWLClass::toString)
                .sorted()
                .collect(Collectors.toList());

        System.out.println(answer);
    }

    @Override
    public String toRepresentativeShortForm() {
        return "hierKoennteIhreWerbungStehen";
    }

    private static String nodeToString (Node node) {
        return node.toString();
    }
}
