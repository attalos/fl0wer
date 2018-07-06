package evaluation;

import helpers.OntologyWrapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassLiteralCollector;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SuperClassesReasoningTask extends ReasoningTask {
    private OWLClass classOwl;

    public SuperClassesReasoningTask(int taskID, OntologyWrapper ontology, long timeout) {
        super(taskID, ontology, timeout);

        //select class randomly
        List<OWLClass> classesInOntology = this.ontology.getOntology().classesInSignature().collect(Collectors.toList());
        int index = ThreadLocalRandom.current().nextInt(0, classesInOntology.size());
        this.classOwl = classesInOntology.get(index);
    }

    public SuperClassesReasoningTask(String csvString) throws OWLOntologyCreationException {
        super(csvString);

        String[] attributes = csvString.split(",");

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        classOwl = factory.getOWLClass(IRI.create(attributes[3]));
    }

    @Override
    public PerformanceResult evaluate(ReasonerEvaluator evaluator) {
        return evaluator.superClasses(this.ontology, this.classOwl, this.timeout);
    }

    @Override
    public String toString() {
        return taskID + "," + ontology + "," + classOwl.toString().substring(1,classOwl.toString().length()-1);
    }
}
