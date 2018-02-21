package translation;

import org.semanticweb.owlapi.model.*;

public class TranslationAxiomVisitor implements OWLAxiomVisitorEx<OWLAxiom> {
    private TranslationClassExpressionVisitor translationClassExpressionVisitor;

    public TranslationAxiomVisitor() {
        this.translationClassExpressionVisitor = new TranslationClassExpressionVisitor();
    }


    @Override
    public OWLAxiom visit(OWLSubClassOfAxiom axiom) {
        axiom.getSubClass().accept(this.translationClassExpressionVisitor);
        axiom.getSuperClass().accept(this.translationClassExpressionVisitor);
        return null;
    }

    @Override
    public OWLAxiom visit(OWLEquivalentClassesAxiom axiom) {
        return null;
    }

    @Override
    public <T> OWLAxiom doDefault(T object) {
        return null;
    }
}
