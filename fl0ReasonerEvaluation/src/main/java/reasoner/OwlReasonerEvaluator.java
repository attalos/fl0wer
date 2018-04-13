package reasoner;

import evaluation.PerformanceResult;
import evaluation.ReasonerEvaluator;
import evaluation.ReasoningTask;
import helpers.OntologyWrapper;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.time.Duration;
import java.time.Instant;

public abstract class OwlReasonerEvaluator implements ReasonerEvaluator {

    protected abstract OWLReasoner createReasoner(OntologyWrapper ontology);

    @Override
    public PerformanceResult evaluate(ReasoningTask reasoningTask) {
        return reasoningTask.evaluate(this);
    }

    @Override
    public PerformanceResult classify(OntologyWrapper ontology) {
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

    @Override
    public PerformanceResult superClasses(OWLClassExpression classOwl) {
        return null;
    }

    @Override
    public PerformanceResult subsumption(OWLClassExpression subClassOwl, OWLClassExpression superClassOwl) {
        return null;
    }
}
