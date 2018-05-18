import evaluation.*;
import helpers.OntologyWrapper;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import reasoner.Fl0werEvaluator;
import reasoner.HermitEvaluator;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.LogManager;

import org.apache.commons.lang.StringUtils;
import reasoner.OpenlletEvaluator;
import translation.OntologyTranslator;

public class Fl0ReasonerEvaluationLauncher {
    public static void main(String[] args) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException, URISyntaxException {
        //disable logging
        LogManager.getLogManager().reset();
        BasicConfigurator.configure();
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.OFF);

        //handle parameters
        if (!Arrays.asList("translate" , "execute", "createClassification", "createSubsumption", "createSubsumerset").contains(args[0])) {
            String executedFileName = Fl0ReasonerEvaluationLauncher.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath();
            System.out.println("expected Syntax: ");
            System.out.println("java -jar " + executedFileName + ".jar translate INPUT_DIR OUTPUT_DIR");
            System.out.println("java -jar " + executedFileName + ".jar execute TASK_FILENAME RESULT_FILENAME");
            System.out.println("java -jar " + executedFileName + ".jar createClassification INPUT_DIR TASK_FILENAME");
            System.out.println("java -jar " + executedFileName + ".jar createSubsumption INPUT_DIR TASK_FILENAME");
            System.out.println("java -jar " + executedFileName + ".jar createSubsumerset INPUT_DIR TASK_FILENAME");
            return;
        }

        if (args[0].equals("translate")) {
            translate(args[1], args[2]);
            return;
        }

        if (args[0].equals("createSubsumption")) {
            createTask(args[1], args[2], 20);
            return;
        }

        if (args[0].equals("execute")) {
            executeTask(args[1], args[2], args[3]);
            return;
        }

        PrintStream outputStream = System.out;
        if (args.length == 2) {
            try {
                outputStream = new PrintStream(new FileOutputStream(args[1], false));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String directoryName = args[0];
        //File ontologyDirectory = new File("src/main/resources/ontologies");
        //String directoryName = "/home/attalos/Documents/private/projects/fl0wer/ontologies/el_ontologies/tones-selection/ontologies";
        File ontologyDirectory = new File(directoryName);
        List<File> ontologyFiles = openAllOntologiesInDirectory(ontologyDirectory);

        ReasonerEvaluator fl0werEvaluator = new Fl0werEvaluator();
        ReasonerEvaluator hermitEvaluator = new HermitEvaluator();
        //ReasonerEvaluator jfactEvaluator = new JFactEvaluator();
        ReasonerEvaluator openlletEvaluator = new OpenlletEvaluator();

        //String format = "%-70s    %10d    %10.3f    %10.3f    %10.3f    %10.3f%n";
        //String formatHeadline = "%-70s    %10s    %10s    %10s    %10s    %10s%n";
        //outputStream.printf(formatHeadline, "Ontologyname", "Classcount", "fl0wer", "HermiT", "JFact", "Openllet");
        //System.out.printf(formatHeadline, "------------", "----------", "------", "------", "-----", "--------");

        outputStream.println("reasoner;ontology;classcount;time");
        for (File ontologyFile : ontologyFiles) {
            try {
                for (int i = 0; i < 5; i++) {
                    //open ontology
                    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
                    OWLOntology ontologyOwl = manager.loadOntologyFromOntologyDocument(ontologyFile);
                    String ontologyName = StringUtils.difference(directoryName, ontologyFile.getAbsolutePath());
                    OntologyWrapper ontology = new OntologyWrapper(ontologyName, ontologyFile.getAbsolutePath(), ontologyOwl);
                    ReasoningTask task = new SubsumptionReasoningTask(i, ontology);

                    if (ontology.getOntology() == null) {
                        System.out.println(ontology.getName() + "this wasn't a valid FL0 or EL ontology");
                        continue;
                    }

                    //evaluate
                    PerformanceResult fl0werResult = fl0werEvaluator.evaluate(task);
                    PerformanceResult hermitResult = hermitEvaluator.evaluate(task);
                    //PerformanceResult jfactResult = jfactEvaluator.evaluate(task);
                    PerformanceResult openlletResult = openlletEvaluator.evaluate(task);

                    //print
                    outputStream.println(fl0werResult.toCsvEntry(";"));
                    outputStream.println(hermitResult.toCsvEntry(";"));
                    //outputStream.println(jfactResult.toCsvEntry(";"));
                    outputStream.println(openlletResult.toCsvEntry(";"));
                }

                //} else {
                    /*long classcount = ontology.getOntology().classesInSignature().count();
                    float fl0werTime = ((float) fl0werResult.getDuration().toMillis()) / 1000;
                    float hermitTime = ((float) hermitResult.getDuration().toMillis()) / 1000;
                    float jfactTime = ((float) jfactResult.getDuration().toMillis()) / 1000;
                    float openlletTime = ((float) openlletResult.getDuration().toMillis()) / 1000;
                    outputStream.printf(format,
                            ontology.getName(),
                            classcount,
                            fl0werTime,
                            hermitTime,
                            jfactTime,
                            openlletTime);*/

                //}
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            }
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
                if (fileEntry.getName().endsWith(".ont")) {
                    ontologyFiles.add(fileEntry);
                }
            }
        }

        return ontologyFiles;
    }

    private static void translate(String inputDir, String outputDir) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
        List<File> ontologyFiles = openAllOntologiesInDirectory(new File(inputDir));

        for (File ontologyFile : ontologyFiles) {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology inputOntologyOwl = manager.loadOntologyFromOntologyDocument(ontologyFile);      //open

            OWLOntology outputOntologyOwl = OntologyTranslator.createFL0Ontology(inputOntologyOwl);     //translate
            if (outputOntologyOwl == null) return;

            //save
            String ontologyName = StringUtils.difference(inputDir, ontologyFile.getAbsolutePath());
            ontologyName = ontologyName.replace("/", "_").substring(1);
            System.out.println(ontologyName);
            OWLDocumentFormat owlxmlFormat = new OWLXMLDocumentFormat();
            manager.saveOntology(outputOntologyOwl, owlxmlFormat, new FileOutputStream(outputDir + "/" +ontologyName , false));
        }
    }

    private static void createTask(String inputDir, String taskFile, int taskCount) throws OWLOntologyCreationException, FileNotFoundException {
        List<File> ontologyFiles = openAllOntologiesInDirectory(new File(inputDir));
        PrintStream outputStream = new PrintStream(new FileOutputStream(taskFile, false));

        for (File ontologyFile : ontologyFiles) {

            //open
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ontologyOwl = manager.loadOntologyFromOntologyDocument(ontologyFile);
            String ontologyName = StringUtils.difference(inputDir, ontologyFile.getAbsolutePath()).substring(1);
            OntologyWrapper ontology = new OntologyWrapper(ontologyName, ontologyFile.getAbsolutePath(), ontologyOwl);

            for (int i = 0; i < taskCount; i++) {
                ReasoningTask task = new SubsumptionReasoningTask(i, ontology);
                outputStream.println(task);
            }
        }
    }

    private static void executeTask(String reasonerName, String taskCSV, String resultFilename) throws OWLOntologyCreationException {
        ReasoningTask task = new SubsumptionReasoningTask(taskCSV);

        ReasonerEvaluator evaluator = null;
        switch (reasonerName) {
            case "fl0wer" : evaluator = new Fl0werEvaluator(); break;
            case "hermit" : evaluator = new HermitEvaluator(); break;
            case "openllet" : evaluator = new OpenlletEvaluator(); break;
        }
        assert evaluator != null;

        PerformanceResult result = evaluator.evaluate(task);
        System.out.println(result.toCsvEntry(","));

    }
}
