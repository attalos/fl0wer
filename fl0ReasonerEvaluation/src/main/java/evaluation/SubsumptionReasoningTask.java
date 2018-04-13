package evaluation;

import helpers.OntologyWrapper;
import org.semanticweb.owlapi.model.OWLClass;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SubsumptionReasoningTask extends ReasoningTask {
    private OWLClass subClassOwl;
    private OWLClass superClassOwl;

    public SubsumptionReasoningTask(OntologyWrapper ontology) {
        super(ontology);

        //select class randomly
        List<OWLClass> classesInOntology = this.ontology.getOntology().classesInSignature().collect(Collectors.toList());
        int index = ThreadLocalRandom.current().nextInt(0, classesInOntology.size());
        this.subClassOwl = classesInOntology.get(index);
        this.superClassOwl = classesInOntology.get(index);
    }

    @Override
    public PerformanceResult evaluate(ReasonerEvaluator evaluator) {
        return evaluator.subsumption(this.ontology, this.subClassOwl, this.superClassOwl);
    }
}
