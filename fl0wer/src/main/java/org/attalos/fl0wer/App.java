package org.attalos.fl0wer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.attalos.fl0wer.utils.ConstantValues;
import org.attalos.fl0wer.controll.FL_0_subsumption;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.*;


public class App {
    private static OWLDataFactory factory;

    private static String inputFilePath;

    private static boolean decide_subsumption_relation;

    private static String root_concept;
    private static String subsumer;


    public static void main(String[] args) {

        if (!init(args)) return;

        OWLOntology ontology_owl = null;
        try {
            ontology_owl = open_ontology();
        } catch (Exception e) {
            System.out.println("Something went wrong and an Exception was thrown: " + e.getMessage());
            e.printStackTrace();
        }

        /* get owl class of input classes */
        OWLClass root_concept_owl = factory.getOWLClass(IRI.create(root_concept));

        OWLClass subsumer_owl;
        if (decide_subsumption_relation) {
            subsumer_owl = factory.getOWLClass(IRI.create(subsumer));
        } else {
            subsumer_owl = null;
        }

        //the important part :)
        ConstantValues.start_timer("initialisation");
        FL_0_subsumption fl_0_subsumption = new FL_0_subsumption(ontology_owl);
        ConstantValues.stop_timer("initialisation");

        ConstantValues.start_timer("subsumption");
        if (ConstantValues.is_subsumption()) {
            fl_0_subsumption.decide_subsumption(root_concept_owl, subsumer_owl);
        } else {
            fl_0_subsumption.calculate_subsumerset(root_concept_owl);
        }
        ConstantValues.stop_timer("subsumption");

        ConstantValues.print_times();

//        fl_0_subsumption.classify().forEach((subsumed_class, subsumer_classes) -> {
//            System.out.println(subsumed_class + " is subsumed by:");
//            subsumer_classes.forEach(subsumer_class -> {
//                System.out.println("\t\t" + subsumer_class.toString());
//            });
//            System.out.println("");
//        });

    }


    /**
     *
     * @param args commandline parameter given to main
     * @return success status - if false, most likely the input parsing went wrong
     */
    private static boolean init(String[] args) {
        ConstantValues.start_timer("program_init");

        /*
         * read commandline parameters
         */
        Options options = new Options();

        Option input_ontology = new Option("i", "input", true, "input ontology path");
        input_ontology.setRequired(true);

        Option concept1 = new Option("c1", "subsumed", true, "subsumed concept");
        //concept1.setRequired(true);

        Option concept2 = new Option("c2", "subsumer", true, "subsumer concept");
        //concept2.setRequired(true);

        Option subsumerset = new Option("S", "supers", true, "output concepts subsuming the input concept <arg>");

        Option debug_level = new Option("v", "verbose", true, "set debuglevel (standard: -1, possible: 0,1,2\")");
        //Option debug_level = new Option("d", "debug-level", true, "set higher debug level (standard: 0, possible: 0,1,2");
        //debug_level.setType(Integer.class);

        Option time_information = new Option("t", "time", false, "display time analysis");
        Option dot_graph = new Option("d", "dots", false, "output dotfile for rete network (not recommendet for big ontologies)");

        options.addOption(input_ontology);
        options.addOption(debug_level);
        options.addOption(time_information);
        options.addOption(dot_graph);
        options.addOption(concept1);
        options.addOption(concept2);
        options.addOption(subsumerset);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse( options, args);
        } catch (ParseException e) {
            System.out.println("Error while parsing parameters\n\n");

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("subs", options, true);
            return false;
        }


        decide_subsumption_relation = cmd.hasOption("subsumed") && cmd.hasOption("subsumer");
        boolean calculate_subsumerset = cmd.hasOption("supers");

        inputFilePath = cmd.getOptionValue("input");
        String subsumed = cmd.getOptionValue("subsumed");
        subsumer = cmd.getOptionValue("subsumer");
        boolean time = cmd.hasOption("time");
        boolean dots = cmd.hasOption("dots");
        String subsumerset_of = cmd.getOptionValue("supers");

        Integer debuglevel = -1;
        if (cmd.hasOption("verbose")) {
            debuglevel = Integer.valueOf(cmd.getOptionValue("verbose"));
            if (!(debuglevel >= 0 && debuglevel <= 2)) {
                throw new RuntimeException("debug level has to be 0, 1 or 2");
            }
        }

        if (decide_subsumption_relation && calculate_subsumerset) {
            System.out.println("You can't use -c1, -c2 and -S at the same time");
            System.out.println("This program either decides subsumption between two given concepts (-c1, -c2) or calculates the subsumer set of a single given concept (-S)");
            return false;
        } else if (decide_subsumption_relation) {
            root_concept = subsumed;
        } else if (calculate_subsumerset) {
            root_concept = subsumerset_of;
        } else {
            System.out.println("Please use (-c1 and -c2) or (-S)");
            return false;
        }

        //fill Constant values
        ConstantValues.initialise(debuglevel, time, dots, decide_subsumption_relation);

        //to prevent some strange owl-api-errors
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.OFF);


        ConstantValues.stop_timer("program_init");

        return true;
    }

    /**
     * open ontology at inputFilePath and translate input classes to owl classes
     *
     * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException
     * originating from OWLOntologyManager.loadOntologyFromOntologyDocument(...)
     */
    private static OWLOntology open_ontology() throws org.semanticweb.owlapi.model.OWLOntologyCreationException {
        ConstantValues.start_timer("open_ontology");

        /* owl init */
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();

        /* open ontology */
        ConstantValues.debug_info("opening ontologie with the owl-api", 0);
        File ontology_file = new File(inputFilePath);
        OWLOntology ontology_owl = m.loadOntologyFromOntologyDocument(ontology_file);

        ConstantValues.stop_timer("open_ontology");

        return ontology_owl;
    }


    //TODO rework this one
    private static void print_ontology(OWLOntology ontology) {
        Stream<OWLAxiom> axioms = ontology.axioms();

        axioms.forEach(ax -> {
            if (ax instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom sub_ax = (OWLSubClassOfAxiom) ax;
                System.out.println(toString_expression(sub_ax.getSubClass()) + " \u2291 " + toString_expression(sub_ax.getSuperClass()));
            } else if (ax instanceof  OWLEquivalentClassesAxiom) {
                OWLEquivalentClassesAxiom equ_ax = (OWLEquivalentClassesAxiom) ax;
                //System.out.println(toString_expression(equ_ax.classExpressions()));
                //conjuncts.stream().map(Object::toString).collect(Collectors.joining(" \u2293 "))
                System.out.println(equ_ax.classExpressions().map(App::toString_expression).collect(Collectors.joining(" \u2261 ")));
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
                    .map(App::toString_expression)
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

