package org.attalos.fl0wer.controll;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.attalos.fl0wer.normalization.Concept_Factory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class FL_0_subsumptionTest {
    private FL_0_subsumption fl_0_subsumption;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private OWLClass root_concept_owl;

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
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();

        /* open ontology */
        File ontology_file = new File("src/main/resources/file/test-ontology-2.owl");
        OWLOntology ontology_owl = ontology_owl = m.loadOntologyFromOntologyDocument(ontology_file);

        /* get owl class of input classes */
        root_concept_owl = factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2017/6/testing-ontology#B"));
        Concept_Factory.getInstance().get_concept_from_owl_class(root_concept_owl);     //make sure it has a number representation

        fl_0_subsumption = new FL_0_subsumption(ontology_owl);

        //read output stream
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void decide_subsumption() {
        fl_0_subsumption.calculate_subsumerset(root_concept_owl);
        String given_answer = outContent.toString().replace("\n", "");
        String corret_answer = "<http://www.semanticweb.org/attalos/ontologies/2017/6/testing-ontology#E><http://www.semanticweb.org/attalos/ontologies/2017/6/testing-ontology#B>";

        assertEquals("Answer to subsumptiontask was wrong", corret_answer, given_answer);
    }

    @AfterClass
    public static void run_once_after_Class() {
        ConstantValues.purge();
    }
}