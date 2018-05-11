package evaluation;

import helpers.OntologyWrapper;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.util.OWLClassLiteralCollector;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SuperClassesReasoningTask extends ReasoningTask {
    private OWLClass classOwl;

    public SuperClassesReasoningTask(int taskID, OntologyWrapper ontology) {
        super(taskID, ontology);

        //select class randomly
        List<OWLClass> classesInOntology = this.ontology.getOntology().classesInSignature().collect(Collectors.toList());
        int index = ThreadLocalRandom.current().nextInt(0, classesInOntology.size());
        this.classOwl = classesInOntology.get(index);
    }

    @Override
    public PerformanceResult evaluate(ReasonerEvaluator evaluator) {
        return evaluator.superClasses(this.ontology, this.classOwl);
    }
}
