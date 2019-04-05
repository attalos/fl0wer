package org.attalos.fl0ReasonerEvaluation.reasoner;

import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class HermitEvaluator extends  OwlReasonerEvaluator {

    @Override
    protected OWLReasoner createReasoner(OntologyWrapper ontology) {
        return new Reasoner.ReasonerFactory().createReasoner(ontology.getOntology());
    }

    @Override
    protected String getReasonerName() {
        return "Hermit";
    }
}
