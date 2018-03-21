package evaluation;

import helpers.OntologyWrapper;
import org.attalos.fl0wer.controll.FL_0_subsumption;
import org.attalos.fl0wer.utils.ConstantValues;
import org.semanticweb.owlapi.model.OWLOntology;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

public class Fl0werEvaluator implements ReasonerEvaluator{

    public Fl0werEvaluator() {
        ConstantValues.initialise(-1, false, false, false);
    }

    @Override
    public PerformanceResult evaluate(ReasoningTask reasoningTask) {
        OntologyWrapper ontology = reasoningTask.getOntology();

        if (ontology.getOntology() != null) {
            FL_0_subsumption fl0wer = new FL_0_subsumption(ontology.getOntology());

            //get time data
            Instant startingTime = Instant.now();
            fl0wer.classify();
            Instant finishTime = Instant.now();

            Duration duration = Duration.between(startingTime, finishTime);
            return new PerformanceResult(duration);
        } else {
            return null;
        }
    }
}
