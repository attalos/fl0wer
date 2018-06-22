package helpers;

import org.semanticweb.owlapi.model.OWLOntology;

public class OntologyWrapper {
    private String name;
    private String ontologyPath;
    private OWLOntology ontology;
    private final long size;

    public OntologyWrapper(String name, String ontologyPath, OWLOntology ontology) {
        this.name = name;
        this.ontologyPath = ontologyPath;
        this.ontology = ontology;
        this.size = this.ontology.classesInSignature().count();
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

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return name + "," + ontologyPath;
    }
}
