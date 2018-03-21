package evaluation;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.time.Duration;
import java.time.Instant;

public class HermitEvaluator implements  ReasonerEvaluator{
    @Override
    public ReasonerEvaluation evaluate(ReasoningTask reasoningTask) {
        ReasonerEvaluation eval = new ReasonerEvaluation();

        reasoningTask.ontologiesToClassify().forEach(ontologyOwl -> {
            OWLReasoner hermit = new Reasoner.ReasonerFactory().createReasoner(ontologyOwl);

            //get time data
            Instant startingTime = Instant.now();
            hermit.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            Instant finishTime = Instant.now();

            Duration duration = Duration.between(startingTime, finishTime);
            eval.insertResult(ontologyOwl, new PerformanceResult(duration));
        });

        return eval;
    }
}
