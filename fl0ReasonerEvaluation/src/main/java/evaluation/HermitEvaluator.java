package evaluation;

import java.time.Duration;
import java.time.Instant;

public class HermitEvaluator implements  ReasonerEvaluator{
    @Override
    public ReasonerEvaluation evaluate(ReasoningTask reasoningTask) {
        ReasonerEvaluation eval = new ReasonerEvaluation();

        reasoningTask.ontologiesToClassify().forEach(ontologyOwl -> {
            Instant startingTime = Instant.now();

            Instant finishTime = Instant.now();

            Duration duration = Duration.between(startingTime, finishTime);
            eval.insertResult(ontologyOwl, new PerformanceResult(duration));
        });

        return eval;
    }
}
