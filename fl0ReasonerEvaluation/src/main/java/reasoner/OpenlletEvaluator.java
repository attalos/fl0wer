package reasoner;

import helpers.OntologyWrapper;
import openllet.owlapi.OpenlletReasonerFactory;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class OpenlletEvaluator extends OwlReasonerEvaluator {
    @Override
    protected OWLReasoner createReasoner(OntologyWrapper ontology) {
        return OpenlletReasonerFactory.getInstance().createReasoner(ontology.getOntology());
    }
}
