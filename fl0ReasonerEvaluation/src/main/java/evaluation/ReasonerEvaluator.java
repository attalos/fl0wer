package evaluation;

import helpers.OntologyWrapper;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

public interface ReasonerEvaluator {
    public PerformanceResult evaluate(ReasoningTask reasoningTask);

    public PerformanceResult classify(OntologyWrapper ontology);
    public PerformanceResult superClasses(OntologyWrapper ontology, OWLClass classOwl);
    public PerformanceResult subsumption(OntologyWrapper ontology, OWLClass subClassOwl, OWLClass superClassOwl);
}
