package org.attalos.fl0ReasonerEvaluation.evaluation;

import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.attalos.fl0ReasonerEvaluation.translation.OntologyTranslator;

import java.io.File;

public abstract  class ReasoningTask {
    private static final long MIN_CLASSCOUNT = 500;

    private int taskID;
    OntologyWrapper ontology;
    long timeout;

    ReasoningTask(int taskID, OntologyWrapper ontology, long timeout) {
        this.taskID = taskID;
        this.ontology = ontology;
        this.timeout = timeout;
        //translate to FL0

        if (!OntologyTranslator.isRawFL0(this.ontology.getOntology())) {
            throw new IllegalArgumentException("all ontologies should be FL0 ontologies at this point, but " + ontology.getName() + "was not.");
        }

        long classesInOntology = this.ontology.getOntology().classesInSignature().count();
        if (classesInOntology < MIN_CLASSCOUNT) {
            throw new IllegalArgumentException("Ontologie " + ontology.getName() + " contained only " + classesInOntology +
                    " classes and everything less than " + MIN_CLASSCOUNT + " gets sorted out.");
        }
    }

    ReasoningTask(String csvString) throws OWLOntologyCreationException {
        String[] attributes = csvString.split(",");
        this.taskID = Integer.parseInt(attributes[0]);
        this.timeout = Integer.parseInt(attributes[1]);
        File ontologyFile = new File(attributes[3]);
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontologyOwl = manager.loadOntologyFromOntologyDocument(ontologyFile);
        this.ontology = new OntologyWrapper(attributes[2], attributes[3], ontologyOwl);
    }

    public OntologyWrapper getOntology() {
        return this.ontology;
    }

    public abstract PerformanceResult evaluate(ReasonerEvaluator evaluator);

    @Override
    public String toString() {
        return taskID + "," + timeout + "," + ontology;
    }
}
