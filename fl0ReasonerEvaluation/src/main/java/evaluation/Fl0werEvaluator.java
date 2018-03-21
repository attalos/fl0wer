package evaluation;

import org.attalos.fl0wer.controll.FL_0_subsumption;
import org.attalos.fl0wer.utils.ConstantValues;
import org.semanticweb.owlapi.model.OWLOntology;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

public class Fl0werEvaluator implements ReasonerEvaluator{

    @Override
    public ReasonerEvaluation evaluate(ReasoningTask reasoningTask) {
        ReasonerEvaluation eval = new ReasonerEvaluation();
        Stream<OWLOntology> ontologiesOwl = reasoningTask.ontologiesToClassify();

        ontologiesOwl.forEach(ontologyOwl -> {
            ConstantValues.initialise(-1, false, false, false);
            FL_0_subsumption fl0wer = new FL_0_subsumption(ontologyOwl);

            //get time data
            Instant startingTime = Instant.now();
            fl0wer.classify();
            Instant finishTime = Instant.now();

            Duration duration = Duration.between(startingTime, finishTime);
            eval.insertResult(ontologyOwl, new PerformanceResult(duration));
        });

        return eval;
    }
}
