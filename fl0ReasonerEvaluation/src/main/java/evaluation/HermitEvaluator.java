package evaluation;

import helpers.OntologyWrapper;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.time.Duration;
import java.time.Instant;

public class HermitEvaluator implements  ReasonerEvaluator{
    @Override
    public PerformanceResult evaluate(ReasoningTask reasoningTask) {

        OntologyWrapper ontology = reasoningTask.getOntology();
        if (ontology.getOntology() != null) {
            OWLReasoner hermit = new Reasoner.ReasonerFactory().createReasoner(ontology.getOntology());

            //get time data
            Instant startingTime = Instant.now();
            hermit.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            Instant finishTime = Instant.now();

            Duration duration = Duration.between(startingTime, finishTime);
            return new PerformanceResult(duration);
        } else {
            return null;
        }


    }
}
