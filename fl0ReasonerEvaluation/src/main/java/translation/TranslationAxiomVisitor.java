package translation;

import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.OWLEquivalentClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

import java.util.Collection;
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
        return new OWLSubClassOfAxiomImpl(subClass, superClass, axiom.annotations().collect(Collectors.toList()));
    }

    @Override
    public OWLAxiom visit(OWLEquivalentClassesAxiom axiom) {
        Collection<OWLClassExpression> classExpressions = axiom.classExpressions()
                .map(classExpression -> classExpression.accept(this.translationClassExpressionVisitor))
                .collect(Collectors.toList());
        return new OWLEquivalentClassesAxiomImpl(classExpressions, axiom.annotations().collect(Collectors.toList()));
    }

    @Override
    public <T> OWLAxiom doDefault(T object) {
        return null;
    }
}
