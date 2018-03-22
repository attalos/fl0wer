package reasoner;

import helpers.OntologyWrapper;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.manchester.cs.jfact.JFactFactory;

public class JFactEvaluator extends OwlReasonerEvaluator {
    @Override
    protected OWLReasoner createReasoner(OntologyWrapper ontology) {
        return new JFactFactory().createReasoner(ontology.getOntology());
    }
}
