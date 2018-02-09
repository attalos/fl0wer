package org.attalos.fl0wer.normalization;

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

    public Ontology() {

        assertions = new HashSet<>();
        new_assertions = new HashSet<>();
    }

    public Ontology(HashSet<GCI> assertions) {

        this.assertions = assertions;
        new_assertions = new HashSet<>();
    }

    public Ontology(OWLOntology ontology) {
        assertions = new HashSet<>();
        new_assertions = new HashSet<>();

        /*String num_of_axioms = Long.toString(ontology.axioms().count());
        final AtomicInteger count = new AtomicInteger();*/
        Stream<OWLAxiom> axioms = ontology.axioms();

        axioms.parallel().forEach(ax -> {
            if (ax instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom sub_ax = (OWLSubClassOfAxiom) ax;
                ConceptDescription subClass = owlClass_to_conceptDescription(sub_ax.getSubClass());
                ConceptDescription superClass = owlClass_to_conceptDescription(sub_ax.getSuperClass());
                assertions.add(new GCI(subClass, superClass));
                //System.out.println(toString_expression(sub_ax.getSubClass()) + " \u2291 " + toString_expression(sub_ax.getSuperClass()));
            } else if (ax instanceof  OWLEquivalentClassesAxiom) {
                OWLEquivalentClassesAxiom equ_ax = (OWLEquivalentClassesAxiom) ax;

                Iterator<OWLClassExpression> iterator = equ_ax.classExpressions().iterator();

                if (!iterator.hasNext()) {
                    throw new RuntimeException("there should be exactly 2 classes in OWLEquivalentClassAxiom but the was: 0");
                }
                OWLClassExpression owl_class_1 = iterator.next();
                ConceptDescription class_1_0 = owlClass_to_conceptDescription(owl_class_1);
                ConceptDescription class_1_1 = owlClass_to_conceptDescription(owl_class_1);

                if (!iterator.hasNext()) {
                    throw new RuntimeException("there should be exactly 2 classes in OWLEquivalentClassAxiom but the was: 1");
                }
                OWLClassExpression owl_class_2 = iterator.next();
                ConceptDescription class_2_0 = owlClass_to_conceptDescription(owl_class_2);
                ConceptDescription class_2_1 = owlClass_to_conceptDescription(owl_class_2);

                if (iterator.hasNext()) {
                    throw new RuntimeException("there should be exactly 2 classes in OWLEquivalentClassAxiom but the was: 3 or more");
                }


                assertions.add(new GCI(class_1_0, class_2_0));
                assertions.add(new GCI(class_2_0, class_1_0));
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

    public static ConceptDescription owlClass_to_conceptDescription(OWLClassExpression owl_exp) {
        //OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        //OWLDataFactory factory = manager.getOWLDataFactory();

        if (owl_exp instanceof OWLClass) {
            if(owl_exp.equals(owl_top)) {
                return Top.getInstance();
            }
            //return new NamedConcept((OWLClass) owl_exp);
            return Concept_Factory.getInstance().get_concept_from_owl_class((OWLClass) owl_exp);
        } else if (owl_exp instanceof OWLObjectIntersectionOf) {
            return new Conjunction((OWLObjectIntersectionOf) owl_exp);
        } else if (owl_exp instanceof OWLObjectAllValuesFrom) {
            return new ValueRestriction((OWLObjectAllValuesFrom) owl_exp);
        }

        System.out.println("Error while parsing input");
        return null;

    }

    public int get_size() {
        return assertions.size();
    }

    public void add_gci(GCI gci) {
        this.new_assertions.add(gci);
    }

    public void normalize() {
        assertions.forEach(gci -> gci.normalize(this));
        assertions.addAll(new_assertions);
    }

    /**
     *
     * @return current num of concepts used in ontology
     */
    public int get_num_of_concepts() {
        return Concept_Factory.getInstance().get_concept_num();
    }

    /**
     *
     * @return total num of roles used in ontology
     */
    public int get_num_of_roles() {
        return Concept_Factory.getInstance().get_role_num();
    }

    /**
     *
     * @param owl_class
     * @return internal representation or -1 if class was not found
     */
    public int get_internal_representation_of(OWLClass owl_class) {
        NamedConcept concept = Concept_Factory.getInstance().translate_owl_class_to_named_concept(owl_class);
        if (concept == null) {
            return -1;
        }

        return concept.getConcept_name();
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
