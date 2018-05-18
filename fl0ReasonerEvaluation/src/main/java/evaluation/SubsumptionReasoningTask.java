package evaluation;

import helpers.OntologyWrapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SubsumptionReasoningTask extends ReasoningTask {
    private OWLClass subClassOwl;
    private OWLClass superClassOwl;

    public SubsumptionReasoningTask(int taskID, OntologyWrapper ontology) {
        super(taskID, ontology);

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

        subClassOwl = factory.getOWLClass(IRI.create(attributes[3]));
        superClassOwl = factory.getOWLClass(IRI.create(attributes[4]));
    }

    @Override
    public PerformanceResult evaluate(ReasonerEvaluator evaluator) {
        return evaluator.subsumption(this.ontology, this.subClassOwl, this.superClassOwl);
    }

    @Override
    public String toString() {
        return taskID + "," + ontology + "," + subClassOwl.toString().substring(1,subClassOwl.toString().length()-1) + "," + superClassOwl.toString().substring(1, superClassOwl.toString().length()-1);
    }
}
