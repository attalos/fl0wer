package translation;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;

import static org.junit.Assert.*;

public class OntologyTranslatorTest {
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    OWLDataFactory factory = manager.getOWLDataFactory();

    @Test
    public void fullfillsOwl2ElProfile() throws Exception{
        File ontology_file = new File("src/main/resources/gene_ontology.ont");
        OWLOntology ontology_owl = manager.loadOntologyFromOntologyDocument(ontology_file);

        assertTrue("EL Ontology wasn't detected as EL ontology", OntologyTranslator.fullfillsOwl2ElProfile(ontology_owl));
    }

    @Test
    public void translateELtoFL0() throws Exception {
        File ontology_file = new File("src/main/resources/gene_ontology.ont");
        OWLOntology ontology_owl_el = manager.loadOntologyFromOntologyDocument(ontology_file);

        OWLOntology ontology_owl_fl0 = OntologyTranslator.translateELtoFL0(ontology_owl_el);
        assertNotNull(ontology_owl_fl0);

        assertTrue("resulting Ontology either wasn't FL_0 or the FL_0 check doesn't work properly", OntologyTranslator.isRawFL0(ontology_owl_fl0));
    }

    @Test
    public void isRawFL0() throws Exception {
        File ontology_file_fl0 = new File("src/main/resources/own_fl0_testontology.owl");
        OWLOntology ontology_owl_fl0 = manager.loadOntologyFromOntologyDocument(ontology_file_fl0);

        assertTrue("Wrongly decided that Ontology isn't FL_0", OntologyTranslator.isRawFL0(ontology_owl_fl0));

        File ontology_file_el = new File("src/main/resources/gene_ontology.ont");
        OWLOntology ontology_owl_el = manager.loadOntologyFromOntologyDocument(ontology_file_el);

        assertFalse("Wrongly decided that Ontology is FL_0", OntologyTranslator.isRawFL0(ontology_owl_el));
    }
}