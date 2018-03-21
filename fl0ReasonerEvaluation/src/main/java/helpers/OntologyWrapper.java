package helpers;

import org.semanticweb.owlapi.model.OWLOntology;

public class OntologyWrapper {
    private String Name;
    private OWLOntology ontology;

    public OntologyWrapper(String name, OWLOntology ontology) {
        Name = name;
        this.ontology = ontology;
    }

    public String getName() {
        return Name;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }
}
