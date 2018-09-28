package org.attalos.fl0ReasonerEvaluation.evaluation;

import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.attalos.fl0ReasonerEvaluation.translation.OntologyTranslator;

import java.io.File;


public abstract  class ReasoningTask {
    protected int taskID;
    protected OntologyWrapper ontology;
    protected long timeout = 30;            //TODO read it from csv

    protected ReasoningTask(int taskID, OntologyWrapper ontology, long timeout) {
        this.taskID = taskID;
        this.ontology = ontology;
        this.timeout = timeout;
        //translate to FL0
        try {
            if (OntologyTranslator.fullfillsOwl2ElProfile(this.ontology.getOntology())) {
                this.ontology.setOntology(OntologyTranslator.translateELtoFL0(this.ontology.getOntology()));
            } else if (!OntologyTranslator.isRawFL0(this.ontology.getOntology())){
                System.out.println("Ontology wasn't in EL or FL0");
                this.ontology.setOntology(null);
            }
        } catch (OWLOntologyCreationException e) {
            this.ontology.setOntology(null);
        }
    }

    protected ReasoningTask(String csvString) throws OWLOntologyCreationException {
        String[] attributes = csvString.split(",");
        this.taskID = Integer.parseInt(attributes[0]);
        File ontologyFile = new File(attributes[2]);
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontologyOwl = manager.loadOntologyFromOntologyDocument(ontologyFile);
        this.ontology = new OntologyWrapper(attributes[1], attributes[2], ontologyOwl);
    }

    public OntologyWrapper getOntology() {
        return this.ontology;
    }

    public abstract PerformanceResult evaluate(ReasonerEvaluator evaluator);
}
