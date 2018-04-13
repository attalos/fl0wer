import evaluation.*;
import helpers.OntologyWrapper;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import reasoner.Fl0werEvaluator;
import reasoner.HermitEvaluator;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.LogManager;

import org.apache.commons.lang.StringUtils;
import reasoner.JFactEvaluator;
import reasoner.OpenlletEvaluator;

public class Fl0ReasonerEvaluationLauncher {
    public static void main(String[] args) {
        //disable logging
        LogManager.getLogManager().reset();
        BasicConfigurator.configure();
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.OFF);

        //handle parameters
        if (args.length != 1 && args.length != 2) {
            System.out.println("use\n java -jar PROGRAMNAME ONTOLOGIE_DIR [OUTPUTFILE]");
            return;
        }

        PrintStream outputStream = System.out;
        if (args.length == 2) {
            try {
                outputStream = new PrintStream(new FileOutputStream(args[1], false)); ;
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
        ReasonerEvaluator jfactEvaluator = new JFactEvaluator();
        ReasonerEvaluator openlletEvaluator = new OpenlletEvaluator();

        String format = "%-70s    %10d    %10.3f    %10.3f    %10.3f    %10.3f%n";
        String formatHeadline = "%-70s    %10s    %10s    %10s    %10s    %10s%n";
        outputStream.printf(formatHeadline, "Ontologyname", "Classcount", "fl0wer", "HermiT", "JFact", "Openllet");
        //System.out.printf(formatHeadline, "------------", "----------", "------", "------", "-----", "--------");

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        for (File ontologyFile : ontologyFiles) {
            try {
                //open ontology
                OWLOntology ontologyOwl = manager.loadOntologyFromOntologyDocument(ontologyFile);
                String ontologyName = StringUtils.difference(directoryName, ontologyFile.getAbsolutePath());
                OntologyWrapper ontology = new OntologyWrapper(ontologyName, ontologyOwl);
                ReasoningTask task = new ClassificationReasoningTask(ontology);

                //evaluate
                PerformanceResult fl0werResult = fl0werEvaluator.evaluate(task);
                PerformanceResult hermitResult = hermitEvaluator.evaluate(task);
                PerformanceResult jfactResult = jfactEvaluator.evaluate(task);
                PerformanceResult openlletResult = openlletEvaluator.evaluate(task);

                //print
                if (ontology.getOntology() == null) {
                    System.out.println(ontology.getName() + "this wasn't a valid FL0 or EL ontology");
                } else {
                    long classcount = ontology.getOntology().classesInSignature().count();
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
                            openlletTime);
                }
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
}
