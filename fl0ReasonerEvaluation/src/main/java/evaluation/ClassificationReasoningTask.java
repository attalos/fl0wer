package evaluation;

import helpers.OntologyWrapper;

public class ClassificationReasoningTask extends  ReasoningTask {
    public ClassificationReasoningTask(int taskID, OntologyWrapper ontology) {
        super(taskID, ontology);
    }

    @Override
    public PerformanceResult evaluate(ReasonerEvaluator evaluator) {
        return evaluator.classify(this.ontology);
    }
}
