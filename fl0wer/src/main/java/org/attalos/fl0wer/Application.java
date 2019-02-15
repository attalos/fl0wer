package org.attalos.fl0wer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.attalos.fl0wer.utils.ConstantValues;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;

import org.apache.commons.cli.*;


public class Application {
    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Application.class.getName());

    private static OWLDataFactory factory;

    private static String inputFilePath;

    private static boolean decide_subsumption_relation = false;
    private static boolean calculate_subsumerset  = false;
    private static boolean classify = false;

    private static String root_concept;
    private static String subsumer;


    public static void main(String[] args) {

        if (!init(args)) return;

        OWLOntology ontology_owl;
        try {
            ontology_owl = open_ontology();
        } catch (Exception e) {
            System.out.println("Something went wrong while opening the ontology and an Exception was thrown: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        /* get owl class of input classes */
        OWLClass root_concept_owl;
        if (!classify) {
            root_concept_owl = factory.getOWLClass(IRI.create(root_concept));
        } else {
            root_concept_owl = null;
        }

        OWLClass subsumer_owl;
        if (decide_subsumption_relation) {
            subsumer_owl = factory.getOWLClass(IRI.create(subsumer));
        } else {
            subsumer_owl = null;
        }

        /* the important part :) */
        ConstantValues.startTimer("initialisation");
        FL0wer fl0wer = new FL0wer(ontology_owl);
        ConstantValues.stopTimer("initialisation");

        ConstantValues.startTimer("main task");
        if (decide_subsumption_relation) {
            fl0wer.decide_subsumption(root_concept_owl, subsumer_owl);
        } else if (calculate_subsumerset) {
            fl0wer.calculate_subsumerset(root_concept_owl);
        } else {
            fl0wer.classify();
        }
        ConstantValues.stopTimer("main task");

        ConstantValues.printTimes();
    }


    /**
     *
     * @param args commandline parameter given to main
     * @return success status - if false, most likely the input parsing went wrong
     */
    private static boolean init(String[] args) {
        ConstantValues.startTimer("program_init");

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

        Option classify_option = new Option("C", "classify", false, "classifies given ontology");

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
        options.addOption(classify_option);

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
        calculate_subsumerset = cmd.hasOption("supers");

        inputFilePath = cmd.getOptionValue("input");
        String subsumed = cmd.getOptionValue("subsumed");
        subsumer = cmd.getOptionValue("subsumer");
        boolean time = cmd.hasOption("time");
        boolean dots = cmd.hasOption("dots");
        String subsumerset_of = cmd.getOptionValue("supers");
        classify = cmd.hasOption("classify");

        Integer debuglevel = -1;
        if (cmd.hasOption("verbose")) {
            debuglevel = Integer.valueOf(cmd.getOptionValue("verbose"));
            if (!(debuglevel >= 0 && debuglevel <= 2)) {
                throw new RuntimeException("debug level has to be 0, 1 or 2");
            }
        }

        //if ( !(decide_subsumption_relation ^ calculate_subsumerset ^ classify) ||
        //        (decide_subsumption_relation && calculate_subsumerset) ) {
        if ((decide_subsumption_relation && calculate_subsumerset) ||
                (decide_subsumption_relation && classify) ||
                (calculate_subsumerset && classify)) {
            System.out.println("You can only use one of -c1, -c2 and -S and -C at the same time");
            System.out.println("This program either decides subsumption between two given concepts (-c1, -c2) or calculates the subsumer set of a single given concept (-S) or classifies the ontology (-C)");
            return false;
        } else if (decide_subsumption_relation) {
            root_concept = subsumed;
        } else if (calculate_subsumerset) {
            root_concept = subsumerset_of;
        } else if (!classify){
            System.out.println("Please use (-c1 and -c2) or (-S) or (-C)");
            return false;
        }

        //fill Constant values
        ConstantValues.initialise(debuglevel, time, dots, decide_subsumption_relation);

        //to prevent some strange owl-api-errors
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.OFF);


        ConstantValues.stopTimer("program_init");

        return true;
    }

    /**
     * open ontology at inputFilePath and translate input classes to owl classes
     *
     * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException
     * originating from OWLOntologyManager.loadOntologyFromOntologyDocument(...)
     */
    private static OWLOntology open_ontology() throws org.semanticweb.owlapi.model.OWLOntologyCreationException {
        ConstantValues.startTimer("open_ontology");

        /* owl init */
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();

        /* open ontology */
//        ConstantValues.debug_info("opening ontologie with the owl-api", 0);
        LOGGER.info("opening ontology with the owl-api");

        File ontology_file = new File(inputFilePath);
        OWLOntology ontology_owl = manager.loadOntologyFromOntologyDocument(ontology_file);

        ConstantValues.stopTimer("open_ontology");

        return ontology_owl;
    }
}

