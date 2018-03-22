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

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.LogManager;

import org.apache.commons.lang.StringUtils;
import reasoner.JFactEvaluator;

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
        ReasonerEvaluator jfactEvaluator = new JFactEvaluator();

        String format = "%-70s\t%10d\t%10.3f\t%10.3f\t%10.3f\t%10.3f\t%20.4f%n";
        String formatHeadline = "%-70s\t%10s\t%10s\t%10s\t%10s\t%10s\t%20s%n";
        System.out.printf(formatHeadline, "Ontologyname", "Classcount", "fl0wer", "HermiT", "JFact", "Openllet", "Classes/s (fl0wer)");
        System.out.printf(formatHeadline, "------------", "----------", "------", "------", "-----", "--------", "------------------");
        for (File ontologyFile : ontologyFiles) {
            try {
                //open ontology
                OWLOntology ontologyOwl = manager.loadOntologyFromOntologyDocument(ontologyFile);
                String ontologyName = StringUtils.difference(directoryName, ontologyFile.getAbsolutePath());
                OntologyWrapper ontology = new OntologyWrapper(ontologyName, ontologyOwl);
                ReasoningTask task = new ReasoningTask(ontology);

                //evaluate
                PerformanceResult fl0werResult = fl0werEvaluator.evaluate(task);
                PerformanceResult hermitResult = hermitEvaluator.evaluate(task);
                PerformanceResult jfactResult = jfactEvaluator.evaluate(task);
                PerformanceResult openlletResult = jfactEvaluator.evaluate(task);

                //print
                if (ontology.getOntology() == null) {
                    System.out.println(ontology.getName() + "this wasn't a valid FL0 or EL ontology");
                } else {
                    long classcount = ontology.getOntology().classesInSignature().count();
                    float fl0werTime = ((float) fl0werResult.getDuration().toMillis()) / 1000;
                    float hermitTime = ((float) hermitResult.getDuration().toMillis()) / 1000;
                    float jfactTime = ((float) jfactResult.getDuration().toMillis()) / 1000;
                    float openlletTime = ((float) openlletResult.getDuration().toMillis()) / 1000;
                    System.out.printf(format,
                            ontology.getName(),
                            classcount,
                            fl0werTime,
                            hermitTime,
                            jfactTime,
                            openlletTime,
                            ((float) classcount)/fl0werTime);
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
