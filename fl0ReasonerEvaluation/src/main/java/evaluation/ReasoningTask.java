package evaluation;

import helpers.OntologyWrapper;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import translation.OntologyTranslator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReasoningTask {
    private OntologyWrapper ontology;

    public ReasoningTask(OntologyWrapper ontology) {
        this.ontology = ontology;
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

    public OntologyWrapper getOntology() {
        return this.ontology;
    }
}
