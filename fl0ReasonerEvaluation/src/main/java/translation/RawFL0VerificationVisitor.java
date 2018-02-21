package translation;

import org.semanticweb.owlapi.model.*;

import java.util.stream.Collectors;

public class RawFL0VerificationVisitor implements OWLObjectVisitorEx<Boolean> {
    @Override
    public Boolean visit(OWLAnnotation node) {
        return true;
    }

    @Override
    public Boolean visit(OWLDeclarationAxiom axiom) {
        return true;
    }

    @Override
    public Boolean visit(OWLDatatypeDefinitionAxiom axiom) {
        return true;
    }

    @Override
    public Boolean visit(OWLObjectIntersectionOf ce) {
        return ce.conjunctSet().allMatch(owlClassExpression -> owlClassExpression.accept(this));
    }

    @Override
    public Boolean visit(OWLObjectAllValuesFrom ce) {
        return ce.getProperty().accept(this) && ce.getFiller().accept(this);
    }

    @Override
    public Boolean visit(OWLClass ce) {
        return true;
    }

    @Override
    public Boolean visit(OWLObjectProperty property) {
        return true;
    }

    @Override
    public Boolean visit(OWLSubClassOfAxiom axiom) {
        return axiom.getSubClass().accept(this) && axiom.getSubClass().accept(this);
    }

    @Override
    public Boolean visit(OWLEquivalentClassesAxiom axiom) {
        return axiom.classExpressions().allMatch(owlClassExpression -> owlClassExpression.accept(this));
    }

    @Override
    public Boolean visit(OWLOntology ontology) {
        return ontology.axioms().allMatch(owlAxiom -> owlAxiom.accept(this));
    }

    @Override
    public <T> Boolean doDefault(T object) {
        return false;
    }
}
