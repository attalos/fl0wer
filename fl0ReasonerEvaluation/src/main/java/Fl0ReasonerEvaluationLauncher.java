import evaluation.*;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Fl0ReasonerEvaluationLauncher {
    public static void main(String[] args) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        System.out.println("hello world");

        List<OWLOntology> ontologiesOwl= new LinkedList<>();

        //open file
        File ontologyFile = new File("src/main/resources/gene_ontology.ont");
        try {
            ontologiesOwl.add(manager.loadOntologyFromOntologyDocument(ontologyFile));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

        //translate to FL0
        ontologiesOwl = ontologiesOwl.stream()
                .filter(OntologyTranslator::fullfillsOwl2ElProfile)
                .map(ontologyOwl -> {
                    try {
                        return OntologyTranslator.translateELtoFL0(ontologyOwl);
                    } catch (OWLOntologyCreationException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        ReasoningTask reasoningTask = new ReasoningTask(ontologiesOwl);

        ReasonerEvaluator fl0werEvaluator = new Fl0werEvaluator();
        ReasonerEvaluator hermitEvaluator = new HermitEvaluator();
        ReasonerEvaluation fl0werResult = fl0werEvaluator.evaluate(reasoningTask);
        ReasonerEvaluation hermitResult = hermitEvaluator.evaluate(reasoningTask);

        reasoningTask.ontologiesToClassify().forEach(ontologyOwl -> {
            System.out.print(fl0werResult.resultAt(ontologyOwl).getDuration().getSeconds());
            System.out.print("\t\t");
            System.out.println(hermitResult.resultAt(ontologyOwl).getDuration().getSeconds());
        });
    }
}
