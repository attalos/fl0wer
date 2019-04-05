package org.attalos.fl0wer.normalization;

import org.attalos.fl0wer.utils.OwlToInternalTranslator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by attalos on 4/25/17.
 */
public class Ontology {
    private Set<GCI> assertions;
    private Set<GCI> new_assertions;

    private static OWLOntologyManager owl_manager = OWLManager.createOWLOntologyManager();
    private static OWLDataFactory owl_factory = owl_manager.getOWLDataFactory();
    private static OWLClass owl_top =  owl_factory.getOWLThing();

    private OwlToInternalTranslator o2iTranslator;

    public Ontology() {

        assertions = new HashSet<>();
        new_assertions = new HashSet<>();
    }

    public Ontology(HashSet<GCI> assertions) {

        this.assertions = assertions;
        new_assertions = new HashSet<>();
    }

    public Ontology(OWLOntology ontology, OwlToInternalTranslator o2iTranslator) {
        this.o2iTranslator = o2iTranslator;

        assertions = new HashSet<>();
        new_assertions = new HashSet<>();

        /*String num_of_axioms = Long.toString(ontology.axioms().count());
        final AtomicInteger count = new AtomicInteger();*/
        Stream<OWLAxiom> axioms = ontology.axioms();

        axioms.parallel().forEach(ax -> {
            if (ax instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom sub_ax = (OWLSubClassOfAxiom) ax;
                ConceptDescription subClass = owlClass_to_conceptDescription(sub_ax.getSubClass(), o2iTranslator);
                ConceptDescription superClass = owlClass_to_conceptDescription(sub_ax.getSuperClass(), o2iTranslator);
                addAssertion(new GCI(subClass, superClass));
                //System.out.println(toString_expression(sub_ax.getSubClass()) + " \u2291 " + toString_expression(sub_ax.getSuperClass()));
            } else if (ax instanceof  OWLEquivalentClassesAxiom) {
                OWLEquivalentClassesAxiom equ_ax = (OWLEquivalentClassesAxiom) ax;

                Iterator<OWLClassExpression> iterator = equ_ax.classExpressions().iterator();

                if (!iterator.hasNext()) {
                    throw new RuntimeException("there should be exactly 2 classes in OWLEquivalentClassAxiom but the was: 0");
                }
                OWLClassExpression owl_class_1 = iterator.next();
                ConceptDescription class_1_0 = owlClass_to_conceptDescription(owl_class_1, o2iTranslator);
                //ConceptDescription class_1_1 = owlClass_to_conceptDescription(owl_class_1, o2iTranslator);

                if (!iterator.hasNext()) {
                    throw new RuntimeException("there should be exactly 2 classes in OWLEquivalentClassAxiom but the was: 1");
                }
                OWLClassExpression owl_class_2 = iterator.next();
                ConceptDescription class_2_0 = owlClass_to_conceptDescription(owl_class_2, o2iTranslator);
                //ConceptDescription class_2_1 = owlClass_to_conceptDescription(owl_class_2, o2iTranslator);

                if (iterator.hasNext()) {
                    throw new RuntimeException("there should be exactly 2 classes in OWLEquivalentClassAxiom but the was: 3 or more");
                }


                addAssertion(new GCI(class_1_0, class_2_0));
                addAssertion(new GCI(class_2_0, class_1_0));
            } else if (ax instanceof OWLDeclarationAxiom) {
                //nothing
            } else {
                System.out.println("Axiom is not SubClassAxiom or EquivalentClassAxiom and no Declaration");
            }

            /*if( (count.incrementAndGet() % 100) == 0 ) {
                System.out.println(new SimpleDateFormat("HH.mm.ss").format(new Date()) + "\t\t" + Integer.toString(count.get()) + "/" + num_of_axioms);
            }*/

        });
    }

    public static ConceptDescription owlClass_to_conceptDescription(OWLClassExpression owl_exp, OwlToInternalTranslator o2iTranslator) {
        if (owl_exp instanceof OWLClass) {
            if(owl_exp.equals(owl_top)) {
                return Top.getInstance();
            }
            //return new NamedConcept((OWLClass) owl_exp);
            return o2iTranslator.translate((OWLClass) owl_exp);
        } else if (owl_exp instanceof OWLObjectIntersectionOf) {
            return new Conjunction((OWLObjectIntersectionOf) owl_exp, o2iTranslator);
        } else if (owl_exp instanceof OWLObjectAllValuesFrom) {
            return new ValueRestriction((OWLObjectAllValuesFrom) owl_exp, o2iTranslator);
        }

        System.out.println("Error while parsing input");
        return null;

    }

    public int get_size() {
        return assertions.size();
    }

    void add_gci(GCI gci) {
        this.new_assertions.add(gci);
    }

    private synchronized void addAssertion(GCI gci) {
        this.assertions.add(gci);
    }

    public void normalize() {
        for (GCI gci : assertions) {
            gci.normalize(this, o2iTranslator);
        }
        this.assertions.addAll(this.new_assertions);
    }

    @Override
    public String toString() {
        return assertions.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }

    public Set<GCI> getAssertions() {

        return assertions;
    }

    public void setAssertions(Set<GCI> assertions) {
        this.assertions = assertions;
    }
}
