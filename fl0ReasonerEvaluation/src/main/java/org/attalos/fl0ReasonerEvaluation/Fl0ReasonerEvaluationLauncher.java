package org.attalos.fl0ReasonerEvaluation;

import org.attalos.fl0ReasonerEvaluation.evaluation.*;
import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.attalos.fl0ReasonerEvaluation.reasoner.Fl0werEvaluator;
import org.attalos.fl0ReasonerEvaluation.reasoner.HermitEvaluator;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.LogManager;

import org.apache.commons.lang.StringUtils;
import org.attalos.fl0ReasonerEvaluation.reasoner.JFactEvaluator;
import org.attalos.fl0ReasonerEvaluation.reasoner.OpenlletEvaluator;
import org.attalos.fl0ReasonerEvaluation.translation.OntologyTranslator;

public class Fl0ReasonerEvaluationLauncher {
    public static void main(String[] args) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException, URISyntaxException {
        //disable logging
        LogManager.getLogManager().reset();
        BasicConfigurator.configure();
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.OFF);

        //handle parameters
        if (args.length <= 2 || !Arrays.asList("translate" , "execute", "createClassification", "createSubsumption", "createSubsumerset").contains(args[0])) {
            showHelp();
            return;
        }

        if (args[0].equals("translate")) {
            if (args.length != 3) {
                showHelp();
                return;
            }
            translate(args[1], args[2]);
            return;
        }

        if (args[0].equals("createSubsumption")) {
            if (args.length != 5) {
                showHelp();
                return;
            }
            createTask(args[1], args[2], Integer.parseInt(args[3]),
                    ontologyWrapper -> taskID -> new SubsumptionReasoningTask(taskID, ontologyWrapper, Long.parseLong(args[4])));
            return;
        }

        if (args[0].equals("createSubsumerset")) {
            if (args.length != 5) {
                showHelp();
                return;
            }
            createTask(args[1], args[2], Integer.parseInt(args[3]),
                    ontologyWrapper -> taskID -> new SuperClassesReasoningTask(taskID, ontologyWrapper, Long.parseLong(args[4])));
            return;
        }

        if (args[0].equals("createClassification")) {
            if (args.length != 5) {
                showHelp();
                return;
            }
            createTask(args[1], args[2], Integer.parseInt(args[3]),
                    ontologyWrapper -> taskID -> new ClassificationReasoningTask(taskID, ontologyWrapper, Long.parseLong(args[4])));
            return;
        }

        if (args[0].equals("execute")) {
            if (args.length != 3) {
                showHelp();
                return;
            }
            executeTask(args[1], args[2]);
            System.exit(0);     // necessary since some of the reasoners do not stop voluntary
        }
    }

    private static List<File> openAllOntologiesInDirectory(final File directory) {
        List<File> ontologyFiles = new LinkedList<>();

        for (final File fileEntry : Objects.requireNonNull(directory.listFiles())) {
            if (fileEntry.isDirectory()) {
                if (!fileEntry.getName().equals("owlxml") && !fileEntry.getName().equals("rdfxml")) {
                    ontologyFiles.addAll(openAllOntologiesInDirectory(fileEntry));
                }
            } else {
                if (fileEntry.getName().endsWith(".ont") || fileEntry.getName().endsWith(".owl")) {
                    ontologyFiles.add(fileEntry);
                }
            }
        }

        return ontologyFiles;
    }

    private static void translate(String inputDir, String outputDir) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
        File inputDirFile = new File(inputDir);
        List<File> ontologyFiles = openAllOntologiesInDirectory(inputDirFile);

        for (File ontologyFile : ontologyFiles) {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology inputOntologyOwl = manager.loadOntologyFromOntologyDocument(ontologyFile);      //open

            OWLOntology outputOntologyOwl = OntologyTranslator.createFL0Ontology(inputOntologyOwl);     //translate
            if (outputOntologyOwl == null) return;

            //save
            String ontologyName = StringUtils.difference(inputDirFile.getAbsolutePath(), ontologyFile.getAbsolutePath());
            ontologyName = ontologyName.replace("/", "_").substring(1);
            System.out.println(ontologyName);
            OWLDocumentFormat owlxmlFormat = new OWLXMLDocumentFormat();
            manager.saveOntology(outputOntologyOwl, owlxmlFormat, new FileOutputStream(outputDir + "/" +ontologyName , false));
        }
    }

    private static void createTask(String inputDir,
                                   String taskFile,
                                   int taskCount,
                                   Function<OntologyWrapper, Function<Integer, ReasoningTask>> reasoningTaskConstructor )
            throws OWLOntologyCreationException, FileNotFoundException {

        File inputDirFile = new File(inputDir);
        List<File> ontologyFiles = openAllOntologiesInDirectory(inputDirFile);
        PrintStream outputStream = new PrintStream(new FileOutputStream(taskFile, false));

        for (File ontologyFile : ontologyFiles) {

            //open
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ontologyOwl = manager.loadOntologyFromOntologyDocument(ontologyFile);
            String ontologyName = StringUtils.difference(inputDirFile.getAbsolutePath(), ontologyFile.getAbsolutePath()).substring(1);
            OntologyWrapper ontology = new OntologyWrapper(ontologyName, ontologyFile.getAbsolutePath(), ontologyOwl);

            for (int i = 0; i < taskCount; i++) {
                try {
                    ReasoningTask task = reasoningTaskConstructor.apply(ontology).apply(i);
                    outputStream.println(task);
                } catch (IllegalArgumentException e) {
                    System.err.println("The following exception occured for " + ontology.getName());
                    System.err.println(e.getMessage());
                }

            }

            manager.clearOntologies();
        }
    }

    private static void executeTask(String reasonerName, String taskCSV) throws OWLOntologyCreationException {
        ReasoningTask task;
        int csvSize = taskCSV.split(",").length;
        switch (csvSize) {
            case 4: task = new ClassificationReasoningTask(taskCSV); break;
            case 5: task = new SuperClassesReasoningTask(taskCSV); break;
            case 6: task = new SubsumptionReasoningTask(taskCSV); break;
            default: task = null;
        }
        assert  task != null;

        ReasonerEvaluator evaluator;
        switch (reasonerName) {
            case "fl0wer" : evaluator = new Fl0werEvaluator(); break;
            case "hermit" : evaluator = new HermitEvaluator(); break;
            case "openllet" : evaluator = new OpenlletEvaluator(); break;
            case "jfact" : evaluator = new JFactEvaluator(); break;
            default: evaluator = null;
        }
        assert evaluator != null;

        PerformanceResult result = evaluator.evaluate(task);
        System.out.println(result.toCsvEntry(","));
    }

    private static void showHelp() {
        String[] executedFilePath = Fl0ReasonerEvaluationLauncher.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getFile()
                .split("/");
        String executedFileName = executedFilePath[executedFilePath.length - 1];
        System.out.println("expected Syntax: ");
        System.out.println("java -jar " + executedFileName + " translate INPUT_DIR OUTPUT_DIR");
        System.out.println("java -jar " + executedFileName + " execute REASONER_NAME TASK_LINE");
        System.out.println("java -jar " + executedFileName + " createClassification INPUT_DIR TASK_FILENAME TASK_COUNT, TIMEOUT");
        System.out.println("java -jar " + executedFileName + " createSubsumption INPUT_DIR TASK_FILENAME TASK_COUNT TIMEOUT");
        System.out.println("java -jar " + executedFileName + " createSubsumerset INPUT_DIR TASK_FILENAME TASK_COUNT TIMEOUT");
        System.out.println("REASONER_NAME = (flower|hermit|jfact|openllet)");
    }
}
