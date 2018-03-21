package evaluation;

import helpers.OntologyWrapper;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class ReasonerEvaluation {
    private Map<OntologyWrapper, PerformanceResult> performanceMap;

    public ReasonerEvaluation() {
        this.performanceMap = new HashMap<>();
    }

    public void insertResult(OntologyWrapper ontology, PerformanceResult performanceResult) {
        this.performanceMap.put(ontology, performanceResult);
    }

    public PerformanceResult resultAt(OntologyWrapper ontology) {
        return performanceMap.get(ontology);
    }

}
