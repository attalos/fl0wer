import evaluation.*;
import helpers.OntologyWrapper;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import translation.OntologyTranslator;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;

public class Fl0ReasonerEvaluationLauncher {
    public static void main(String[] args) {
        //disable logging
        LogManager.getLogManager().reset();
        BasicConfigurator.configure();
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.OFF);

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        //File ontologyDirectory = new File("src/main/resources/ontologies");
        String directoryName = "/home/attalos/Documents/private/projects/fl0wer/ontologies/el_ontologies/tones-selection/ontologies";
        File ontologyDirectory = new File(directoryName);
        List<File> ontologyFiles = openAllOntologiesInDirectory(ontologyDirectory);

        ReasonerEvaluator fl0werEvaluator = new Fl0werEvaluator();
        ReasonerEvaluator hermitEvaluator = new HermitEvaluator();

        String format = "%-70s%10.3f\t%10.3f%n";
        String formatHeadline = "%-70s%10s\t%10s%n";
        System.out.printf(formatHeadline, "Ontologyname", "fl0wer", "HermiT");
        System.out.printf(formatHeadline, "------------", "------", "------");
        for (File ontologyFile : ontologyFiles) {
            try {
                //open ontology
                OWLOntology ontologyOwl = manager.loadOntologyFromOntologyDocument(ontologyFile);
                String ontologyName = StringUtils.difference(directoryName, ontologyFile.getAbsolutePath());
                System.out.println(ontologyName);
                OntologyWrapper ontology = new OntologyWrapper(ontologyName, ontologyOwl);
                ReasoningTask task = new ReasoningTask(ontology);

                //evaluate
                PerformanceResult fl0werResult = fl0werEvaluator.evaluate(task);
                PerformanceResult hermitResult = hermitEvaluator.evaluate(task);

                //print
                if (ontology.getOntology() == null) {
                    System.out.println(ontology.getName() + "this wasn't a valid FL0 or EL ontology");
                } else {
                    System.out.printf(format,
                            ontology.getName(),
                            ((float) fl0werResult.getDuration().toMillis()) / 1000,
                            ((float) hermitResult.getDuration().toMillis()) / 1000);
                }
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<File> openAllOntologiesInDirectory(final File directory) {
        List<File> ontologyFiles = new LinkedList<>();

        for (final File fileEntry : directory.listFiles()) {
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
