package evaluation;

import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashMap;
import java.util.Map;

public class ReasonerEvaluation {
    private Map<OWLOntology, PerformanceResult> performanceMap;

    public ReasonerEvaluation() {
        this.performanceMap = new HashMap<>();
    }

    public void insertResult(OWLOntology ontologyOwl, PerformanceResult performanceResult) {
        this.performanceMap.put(ontologyOwl, performanceResult);
    }

    public PerformanceResult resultAt(OWLOntology ontologyOwl) {
        return performanceMap.get(ontologyOwl);
    }

}
