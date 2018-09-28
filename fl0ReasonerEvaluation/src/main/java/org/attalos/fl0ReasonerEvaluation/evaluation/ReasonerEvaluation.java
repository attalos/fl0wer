package org.attalos.fl0ReasonerEvaluation.evaluation;

import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;

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
