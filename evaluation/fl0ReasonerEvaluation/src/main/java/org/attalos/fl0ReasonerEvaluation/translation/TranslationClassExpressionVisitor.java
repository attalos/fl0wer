package org.attalos.fl0ReasonerEvaluation.translation;


import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectAllValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TranslationClassExpressionVisitor implements OWLClassExpressionVisitorEx<OWLClassExpression> {

    protected TranslationClassExpressionVisitor() {}

    @Override
    public OWLClassExpression visit(OWLObjectIntersectionOf ce) {
        List<OWLClassExpression> conjunctList = ce.conjunctSet()
                .map(conjunct -> conjunct.accept(this))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (conjunctList.size() == 0) {
            return null;
        } else if (conjunctList.size() == 1) {
            return conjunctList.get(0);
        } else {
            return new OWLObjectIntersectionOfImpl(conjunctList.stream());
        }
    }

    @Override
    public OWLClassExpression visit(OWLObjectSomeValuesFrom ce) {
        OWLClassExpression filler = ce.getFiller().accept(this);
        if (filler == null) { return null; }
        return new OWLObjectAllValuesFromImpl(ce.getProperty(), filler);
    }

    @Override
    public OWLClassExpression visit(OWLObjectAllValuesFrom ce) {
        OWLClassExpression filler = ce.getFiller().accept(this);
        if (filler == null) { return null; }
        return new OWLObjectAllValuesFromImpl(ce.getProperty(), filler);
    }

    @Override
    public OWLClassExpression visit(OWLClass ce) {
        return ce;
    }

    public <T> OWLClassExpression doDefault(T object) {
        //assert false : "found owl class expression which shouldn't exist anymore at this state:\n" + object.getClass().getName();
        return null;
    }
}
