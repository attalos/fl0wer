package evaluation;

import helpers.OntologyWrapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class ClassificationReasoningTask extends  ReasoningTask {
    public ClassificationReasoningTask(int taskID, OntologyWrapper ontology) {
        super(taskID, ontology);
    }

    public ClassificationReasoningTask(String csvString) throws OWLOntologyCreationException {
        super(csvString);

        String[] attributes = csvString.split(",");

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
    }

    @Override
    public PerformanceResult evaluate(ReasonerEvaluator evaluator) {
        return evaluator.classify(this.ontology);
    }

    @Override
    public String toString() {
        return taskID + "," + ontology;
    }
}
