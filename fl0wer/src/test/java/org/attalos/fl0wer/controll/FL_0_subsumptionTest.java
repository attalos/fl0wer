package org.attalos.fl0wer.controll;

import junit.framework.TestCase;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.attalos.fl0wer.normalization.Concept_Factory;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

public class FL_0_subsumptionTest extends TestCase {
    private FL_0_subsumption fl_0_subsumption;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        //fill Constant values
        ConstantValues.initialise(-1, false, false, false);

        //to prevent some strange owl-api-errors
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.OFF);

        /* owl init */
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();

        /* open ontology */
        File ontology_file = new File("src/main/resources/file/test-ontology-2.owl");
        OWLOntology ontology_owl = null;
        try {
            ontology_owl = m.loadOntologyFromOntologyDocument(ontology_file);
        } catch (Exception e) {
            System.out.println("Something went wrong and an Exception was thrown: " + e.getMessage());
            e.printStackTrace();
        }

        /* get owl class of input classes */
        OWLClass root_concept_owl = factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2017/6/testing-ontology#B"));
        Concept_Factory.getInstance().get_concept_from_owl_class(root_concept_owl);     //make sure it has a number representation

        fl_0_subsumption = new FL_0_subsumption(ontology_owl, root_concept_owl, null);

        //read output stream
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testDecide_subsumption() {
        fl_0_subsumption.decide_subsumption();
        String given_answer = outContent.toString().replace("\n", "");
        String corret_answer = "<http://www.semanticweb.org/attalos/ontologies/2017/6/testing-ontology#E><http://www.semanticweb.org/attalos/ontologies/2017/6/testing-ontology#B>";
        assertEquals("Answer to subsumptiontask was wrong", corret_answer, given_answer);
        //assertTrue(true);
    }
}