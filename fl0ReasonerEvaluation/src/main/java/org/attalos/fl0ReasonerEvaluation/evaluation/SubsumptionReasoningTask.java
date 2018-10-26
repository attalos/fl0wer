package org.attalos.fl0ReasonerEvaluation.evaluation;

import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SubsumptionReasoningTask extends ReasoningTask {
    private OWLClass subClassOwl;
    private OWLClass superClassOwl;

    public SubsumptionReasoningTask(int taskID, OntologyWrapper ontology, long timeout) {
        super(taskID, ontology, timeout);

        //select classes randomly
        List<OWLClass> classesInOntology = this.ontology.getOntology().classesInSignature().collect(Collectors.toList());
        int index = ThreadLocalRandom.current().nextInt(0, classesInOntology.size());
        this.subClassOwl = classesInOntology.get(index);

        index = ThreadLocalRandom.current().nextInt(0, classesInOntology.size());
        this.superClassOwl = classesInOntology.get(index);
    }

    public SubsumptionReasoningTask(String csvString) throws OWLOntologyCreationException {
        super(csvString);

        String[] attributes = csvString.split(",");

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        subClassOwl = factory.getOWLClass(IRI.create(attributes[4]));
        superClassOwl = factory.getOWLClass(IRI.create(attributes[5]));
    }

    @Override
    public PerformanceResult evaluate(ReasonerEvaluator evaluator) {
        return evaluator.subsumption(this.ontology, this.subClassOwl, this.superClassOwl, this.timeout);
    }

    @Override
    public String toString() {
        return super.toString() + "," + subClassOwl.toString().substring(1,subClassOwl.toString().length()-1) + "," + superClassOwl.toString().substring(1, superClassOwl.toString().length()-1);
    }
}
