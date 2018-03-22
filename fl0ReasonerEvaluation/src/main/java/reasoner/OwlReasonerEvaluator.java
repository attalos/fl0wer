package reasoner;

import evaluation.PerformanceResult;
import evaluation.ReasonerEvaluator;
import evaluation.ReasoningTask;
import helpers.OntologyWrapper;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.time.Duration;
import java.time.Instant;

public abstract class OwlReasonerEvaluator implements ReasonerEvaluator {

    protected abstract OWLReasoner createReasoner(OntologyWrapper ontology);

    @Override
    public PerformanceResult evaluate(ReasoningTask reasoningTask) {

        OntologyWrapper ontology = reasoningTask.getOntology();
        if (ontology.getOntology() != null) {
            OWLReasoner reasoner = createReasoner(ontology);

            //get time data
            Instant startingTime = Instant.now();
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            Instant finishTime = Instant.now();

            Duration duration = Duration.between(startingTime, finishTime);
            return new PerformanceResult(duration);
        } else {
            return null;
        }


    }
}
