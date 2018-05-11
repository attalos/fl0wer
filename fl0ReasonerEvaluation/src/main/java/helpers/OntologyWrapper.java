package helpers;

import org.semanticweb.owlapi.model.OWLOntology;

public class OntologyWrapper {
    private String name;
    private String ontologyPath;
    private OWLOntology ontology;

    public OntologyWrapper(String name, String ontologyPath, OWLOntology ontology) {
        this.name = name;
        this.ontologyPath = ontologyPath;
        this.ontology = ontology;
    }

    public String getName() {
        return name;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }

    @Override
    public String toString() {
        return name + "," + ontologyPath;
    }
}
