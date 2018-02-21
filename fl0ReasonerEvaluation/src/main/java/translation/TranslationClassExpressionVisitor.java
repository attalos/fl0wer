package translation;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapitools.builders.BuilderDataAllValuesFrom;
import org.semanticweb.owlapitools.builders.BuilderObjectAllValuesFrom;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectAllValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;

public class TranslationClassExpressionVisitor implements OWLClassExpressionVisitorEx<OWLClassExpression> {

    public TranslationClassExpressionVisitor() {}

    public OWLClassExpression visit(OWLObjectIntersectionOf ce) {
        new OWLObjectIntersectionOfImpl(ce.conjunctSet().map(conjunct -> conjunct.accept(this)));
        return ce;
    }

    public OWLClassExpression visit(OWLObjectSomeValuesFrom ce) {
        return new OWLObjectAllValuesFromImpl(ce.getProperty(), ce.getFiller().accept(this));
    }

    public OWLClassExpression visit(OWLObjectAllValuesFrom ce) {
        return new OWLObjectAllValuesFromImpl(ce.getProperty(), ce.getFiller().accept(this));
    }

    public OWLClassExpression visit(OWLClass ce) {
        return ce;
    }

    public <T> OWLClassExpression doDefault(T object) {
        assert false : "found owl class expression which shouldn't exist anymore at this state";
        return null;
    }
}
