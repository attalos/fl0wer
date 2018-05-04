package reasoner;

import evaluation.PerformanceResult;
import evaluation.ReasonerEvaluator;
import evaluation.ReasoningTask;
import helpers.OntologyWrapper;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.time.Duration;
import java.time.Instant;

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
