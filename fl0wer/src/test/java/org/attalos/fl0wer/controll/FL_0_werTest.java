package org.attalos.fl0wer.controll;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.attalos.fl0wer.FL0wer;
import org.attalos.fl0wer.utils.ConstantValues;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FL_0_werTest {
    private FL0wer fl_0_wer;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private OWLClass root_concept_owl;

    OWLOntologyManager manager;
    OWLDataFactory factory;

    @BeforeClass
    public static void runOnceBeforeClass() {
        //fill Constant values
        ConstantValues.initialise(-1, false, false, false);

        //to prevent some strange owl-api-errors
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.OFF);
    }

    @Before
    public void setUp() throws Exception {
        /* owl init */
        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();

        /* open ontology */
        File ontology_file = new File("src/main/resources/file/test-ontology-2.owl");
        OWLOntology ontology_owl = ontology_owl = manager.loadOntologyFromOntologyDocument(ontology_file);

        /* get owl class of input classes */
        root_concept_owl = factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2017/6/testing-ontology#B"));

        fl_0_wer = new FL0wer(ontology_owl);
    }

    @Test
    public void testCalculae_subsumerset() {
        List<OWLClass> given_answer = fl_0_wer.calculate_subsumerset(root_concept_owl);

        List<OWLClass> correct_answer = new ArrayList<>();
        correct_answer.add(factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2017/6/testing-ontology#E")));
        correct_answer.add(factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2017/6/testing-ontology#B")));

        assertEquals("Answer to subsumptiontask was wrong", correct_answer, given_answer);
    }

    @Test
    public void testBlocking() {
        fl_0_wer = new FL0wer(openOwlOntology("src/test/resources/blockingTestOntology.owl"));

        /* get owl class of input classes */
        root_concept_owl = factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2018/1/blockingTestOntology#A"));

        //if this runs for to long - blocking probably doesn't work
        List<OWLClass> subsumerset = fl_0_wer.calculate_subsumerset(root_concept_owl);

        assertNotNull(subsumerset);

    }

    @AfterClass
    public static void run_once_after_Class() {
        ConstantValues.purge();
    }

    public OWLOntology openOwlOntology(String ontology_path) {
        /* open ontology */
        File ontology_file = new File(ontology_path);
        try {
            return manager.loadOntologyFromOntologyDocument(ontology_file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}