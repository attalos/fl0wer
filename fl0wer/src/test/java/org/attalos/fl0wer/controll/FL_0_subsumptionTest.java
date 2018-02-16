package org.attalos.fl0wer.controll;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.attalos.fl0wer.utils.ConstantValues;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import static org.junit.Assert.*;

public class FL_0_subsumptionTest {
    private FL_0_subsumption fl_0_subsumption;
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

        fl_0_subsumption = new FL_0_subsumption(ontology_owl);
    }

    @Test
    public void decide_subsumption() {
        //read output stream
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(outContent));

        fl_0_subsumption.calculate_subsumerset(root_concept_owl);
        String given_answer = outContent.toString().replace("\n", "");
        String corret_answer = "<http://www.semanticweb.org/attalos/ontologies/2017/6/testing-ontology#E><http://www.semanticweb.org/attalos/ontologies/2017/6/testing-ontology#B>";

        assertEquals("Answer to subsumptiontask was wrong", corret_answer, given_answer);

        //reset output stream to stdout
        System.setOut(stdout);
    }

    @Test
    public void testBlocking() {
        fl_0_subsumption = new FL_0_subsumption(openOwlOntology("src/test/resources/blockingTestOntology.owl"));

        /* get owl class of input classes */
        root_concept_owl = factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2018/1/blockingTestOntology#A"));

        //if this runs for to long - blocking probably doesn't work
        List<OWLClass> subsumerset = fl_0_subsumption.calculate_subsumerset(root_concept_owl);

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