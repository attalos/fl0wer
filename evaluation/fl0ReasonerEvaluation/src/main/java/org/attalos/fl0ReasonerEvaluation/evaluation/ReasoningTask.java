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

    int taskID;
    OntologyWrapper ontology;
    long timeout;

    ReasoningTask(int taskID, OntologyWrapper ontology, long timeout) {
        this.taskID = taskID;
        this.ontology = ontology;
        this.timeout = timeout;

        long classesInOntology = this.ontology.getOntology().classesInSignature().count();
        if (classesInOntology < MIN_CLASSCOUNT) {
            throw new IllegalArgumentException("Ontologie " + ontology.getName() + " contained only " + classesInOntology +
                    " classes and everything less than " + MIN_CLASSCOUNT + " gets sorted out.");
        }

        boolean containsTop = this.ontology.getOntology().classesInSignature().anyMatch(x -> x.equals(OWLManager.getOWLDataFactory().getOWLThing()));
        if (containsTop) {
            throw new IllegalArgumentException("Ontologie " + ontology.getName() + " contained TOP class, which I filter at the moment " +
                    "because I haven't implemented it yet.. i know that this is no nice solution...");
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
