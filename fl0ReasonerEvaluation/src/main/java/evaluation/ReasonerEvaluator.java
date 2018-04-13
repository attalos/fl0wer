package evaluation;

import helpers.OntologyWrapper;
import org.semanticweb.owlapi.model.OWLClassExpression;

public interface ReasonerEvaluator {
    public PerformanceResult evaluate(ReasoningTask reasoningTask);

    public PerformanceResult classify(OntologyWrapper ontology);
    public PerformanceResult superClasses(OWLClassExpression classOwl);
    public PerformanceResult subsumption(OWLClassExpression subClassOwl, OWLClassExpression superClassOwl);
}
