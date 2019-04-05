package org.attalos.fl0ReasonerEvaluation.translation;

import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class TranslationAxiomVisitor implements OWLAxiomVisitorEx<OWLAxiom> {
    private TranslationClassExpressionVisitor translationClassExpressionVisitor;

    protected TranslationAxiomVisitor() {
        this.translationClassExpressionVisitor = new TranslationClassExpressionVisitor();
    }


    @Override
    public OWLAxiom visit(OWLSubClassOfAxiom axiom) {
        OWLClassExpression subClass = axiom.getSubClass().accept(this.translationClassExpressionVisitor);
        OWLClassExpression superClass = axiom.getSuperClass().accept(this.translationClassExpressionVisitor);
        if (subClass != null && superClass != null){
            return new OWLSubClassOfAxiomImpl(subClass, superClass, axiom.annotations().collect(Collectors.toList()));
        } else {
            return null;
        }
    }

    @Override
    public OWLAxiom visit(OWLEquivalentClassesAxiom axiom) {
        Collection<OWLClassExpression> classExpressions = axiom.classExpressions()
                .map(classExpression -> classExpression.accept(this.translationClassExpressionVisitor))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (classExpressions.size() <= 1) {
            return null;
        }
        return new OWLEquivalentClassesAxiomImpl(classExpressions, axiom.annotations().collect(Collectors.toList()));
    }

    @Override
    public <T> OWLAxiom doDefault(T object) {
        return null;
    }
}
