package org.attalos.fl0ReasonerEvaluation.evaluation;

import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class ClassificationReasoningTask extends  ReasoningTask {
    public ClassificationReasoningTask(int taskID, OntologyWrapper ontology, long timeout) {
        super(taskID, ontology, timeout);
    }

    public ClassificationReasoningTask(String csvString) throws OWLOntologyCreationException {
        super(csvString);
    }

    @Override
    public PerformanceResult evaluate(ReasonerEvaluator evaluator) {
        return evaluator.classify(this.ontology, this.taskID, this.timeout);
    }

    /*
     * does not realy change what ReasoningTask already does, but this way i won't search for it later
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
