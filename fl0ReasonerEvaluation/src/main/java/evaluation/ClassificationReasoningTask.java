package evaluation;

import helpers.OntologyWrapper;

public class ClassificationReasoningTask extends  ReasoningTask {
    public ClassificationReasoningTask(OntologyWrapper ontology) {
        super(ontology);
    }

    @Override
    public PerformanceResult evaluate(ReasonerEvaluator evaluator) {
        return evaluator.classify(this.ontology);
    }
}
