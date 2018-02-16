package org.attalos.fl0wer.utils;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HelperFunctions {

    private static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private static OWLDataFactory factory = manager.getOWLDataFactory();

    private static OWLOntology open_ontology(String inputFilePath) throws org.semanticweb.owlapi.model.OWLOntologyCreationException {
        File ontology_file = new File(inputFilePath);
        return  manager.loadOntologyFromOntologyDocument(ontology_file);
    }

    //TODO rework this one
    public static void print_ontology(OWLOntology ontology) {
        Stream<OWLAxiom> axioms = ontology.axioms();

        axioms.forEach(ax -> {
            if (ax instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom sub_ax = (OWLSubClassOfAxiom) ax;
                System.out.println(toString_expression(sub_ax.getSubClass()) + " \u2291 " + toString_expression(sub_ax.getSuperClass()));
            } else if (ax instanceof OWLEquivalentClassesAxiom) {
                OWLEquivalentClassesAxiom equ_ax = (OWLEquivalentClassesAxiom) ax;
                //System.out.println(toString_expression(equ_ax.classExpressions()));
                //conjuncts.stream().map(Object::toString).collect(Collectors.joining(" \u2293 "))
                System.out.println(equ_ax.classExpressions().map(HelperFunctions::toString_expression).collect(Collectors.joining(" \u2261 ")));
            } else {
                System.out.println("Axiom is not SubClassAxiom");
            }

        });
        //System.out.println("\u2200");
        //System.out.println("\u2293");
        //System.out.println(ontology.toString());
    }

    //TODO rework or delete this one
    public static String toString_expression(OWLClassExpression exp) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        if (exp instanceof OWLClass) {
            if(exp.equals(factory.getOWLThing())) {
                return "\u22A4";
            }
            return exp.toString();
        } else if (exp instanceof OWLObjectIntersectionOf) {
            return "("
                    + exp.conjunctSet()
                    .map(HelperFunctions::toString_expression)
                    .collect(Collectors.joining(" \u2293 "))
                    + ")";
        } else if (exp instanceof OWLObjectAllValuesFrom) {
            OWLObjectAllValuesFrom value_restriction = (OWLObjectAllValuesFrom) exp;
            //String role_name = value_restriction.getProperty().toString().split("#")[1];
            String role_name = value_restriction.getProperty().toString();
            //role_name = role_name.substring(0, role_name.length() -1);
            return "\u2200" + role_name + "." + toString_expression(value_restriction.getFiller());
        }

        return "#################";
    }
}
