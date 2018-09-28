package org.attalos.fl0wer.rete;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.attalos.fl0wer.utils.ConstantValues;
import org.attalos.fl0wer.normalization.Ontology;
import org.attalos.fl0wer.subsumption.ApplicableRule;
import org.attalos.fl0wer.subsumption.HeadOntology;
import org.attalos.fl0wer.utils.OwlToInternalTranslator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ReteNetworkTest {
    private ReteNetwork rete_network;
    private Integer class_A;
    private Integer class_B;
    private Integer class_C;
    private Integer class_D;
    private Integer roll_r;

    @BeforeClass
    public static void runOnceBeforeClass() {
        //fill Constant values
        ConstantValues.initialise(-1, false, false, false);

        //to prevent some strange owl-api-errors
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.OFF);
    }

    @Before
    public void setUp() throws Exception{
        /* owl init */
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();


        /* open ontology */
        File ontology_file = new File("src/test/resources/reteTestOntology.owl");
        OWLOntology ontology_owl = manager.loadOntologyFromOntologyDocument(ontology_file);

        OwlToInternalTranslator o2iTranslator = new OwlToInternalTranslator();
        o2iTranslator.initialize_original_owl_classes(ontology_owl.classesInSignature());
        class_A = o2iTranslator.translate(factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2018/1/reteTestOntology#A"))).getConcept_name();
        class_B = o2iTranslator.translate(factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2018/1/reteTestOntology#B"))).getConcept_name();
        class_C = o2iTranslator.translate(factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2018/1/reteTestOntology#C"))).getConcept_name();
        class_D = o2iTranslator.translate(factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2018/1/reteTestOntology#D"))).getConcept_name();
        roll_r = 0;

        //internal ontology representation
        Ontology ontology = new Ontology(ontology_owl, o2iTranslator);

        //normalize
        ontology.normalize();

        //lock translator
        o2iTranslator.lock();

        //head ontology representation
        HeadOntology head_ontology = new HeadOntology(ontology, o2iTranslator.get_role_count());

        //rete network
        this.rete_network = new ReteNetwork(head_ontology, o2iTranslator.get_concept_count(), o2iTranslator.get_role_count());
    }

    @Test
    public void testSimple_rule_detection() {
        WorkingMemory wm = rete_network.generate_new_WorkingMemory();

        Set<Integer> input_concept_set = new HashSet<>();
        input_concept_set.add(class_C);

        List<Integer> expected_output_concept_set = new ArrayList<>();
        expected_output_concept_set.add(class_D);

        rete_network.propagate_domain_elem(0L, input_concept_set, wm);
        ApplicableRule resulting_Rule = rete_network.get_next_rule_to_fire(wm);

        assertNotNull("There was no applicable rule found", resulting_Rule);
        assertNull("There were to many applicable rules found", this.rete_network.get_next_rule_to_fire(wm));
        assertEquals("Resulting rule was refering to wrong domain element", 0, resulting_Rule.get_node_id());
        assertEquals("Resulting rule contained wrong concepts", expected_output_concept_set, resulting_Rule.get_rule_right_side().get_concept_set_at(0));
        assertNull("Resulting rule contained wrong concepts", resulting_Rule.get_rule_right_side().get_concept_set_at(roll_r + 1));

    }

    @Test
    public void testWorking_with_multiple_dom_elems() {
        WorkingMemory wm = rete_network.generate_new_WorkingMemory();

        Set<Integer> input_concept_set = new HashSet<>();
        input_concept_set.add(class_C);

        rete_network.propagate_domain_elem(0L, input_concept_set, wm);
        ApplicableRule resulting_Rule = rete_network.get_next_rule_to_fire(wm);
        assertNotNull("(see simple_rule_detection)", resulting_Rule);
        assertNull("(see simple_rule_detection)", this.rete_network.get_next_rule_to_fire(wm));

        rete_network.propagate_domain_elem(77L, input_concept_set, wm);
        resulting_Rule = rete_network.get_next_rule_to_fire(wm);
        assertNotNull("There was no applicable rule found for second dom elem", resulting_Rule);
        assertNull("There were to many applicable rules found for second dom elem", this.rete_network.get_next_rule_to_fire(wm));
        assertEquals("Resulting rule was refering to wrong domain element", 77, resulting_Rule.get_node_id());
    }

    @Test
    public void testMultiple_input_node() {
        WorkingMemory wm = rete_network.generate_new_WorkingMemory();

        Set<Integer> input_concept_set_0 = new HashSet<>();
        input_concept_set_0.add(class_A);

        Set<Integer> input_concept_set_1 = new HashSet<>();
        input_concept_set_1.add(class_B);

        List<Integer> expected_output_concept_set = new ArrayList<>();
        expected_output_concept_set.add(class_D);

        rete_network.propagate_domain_elem(0L, input_concept_set_0, wm);
        rete_network.propagate_domain_elem(1L, input_concept_set_1, wm);

        ApplicableRule resulting_Rule = rete_network.get_next_rule_to_fire(wm);

        assertNotNull("There was no applicable rule found", resulting_Rule);
        assertNull("There were to many applicable rules found", this.rete_network.get_next_rule_to_fire(wm));
        assertEquals("Resulting rule was refering to wrong domain element", 0, resulting_Rule.get_node_id());
        assertEquals("Resulting rule contained wrong concepts", expected_output_concept_set, resulting_Rule.get_rule_right_side().get_concept_set_at(0));
        assertNull("Resulting rule contained wrong concepts", resulting_Rule.get_rule_right_side().get_concept_set_at(1));

    }

    @Test
    public void testFinal_node_repetation() {
        WorkingMemory wm = rete_network.generate_new_WorkingMemory();

        Set<Integer> input_concept_set = new HashSet<>();
        input_concept_set.add(class_C);

        rete_network.propagate_domain_elem(0L, input_concept_set, wm);
        rete_network.get_next_rule_to_fire(wm);

        rete_network.propagate_domain_elem(0L, input_concept_set, wm);
        ApplicableRule resulting_Rule = rete_network.get_next_rule_to_fire(wm);
        assertNull("Every rule should only be found once per dom elem", resulting_Rule);
    }

    @Test
    public void testRete_reuse() {
        WorkingMemory wm1 = rete_network.generate_new_WorkingMemory();
        WorkingMemory wm2 = rete_network.generate_new_WorkingMemory();

        Set<Integer> input_concept_set = new HashSet<>();
        input_concept_set.add(class_C);

        ApplicableRule resulting_Rule;
        rete_network.propagate_domain_elem(0L, input_concept_set, wm1);
        resulting_Rule = rete_network.get_next_rule_to_fire(wm1);
        assertNotNull("Something went completly wrong. (See testSimple_rule_detection)", resulting_Rule);

        rete_network.propagate_domain_elem(0L, input_concept_set, wm2);
        resulting_Rule = rete_network.get_next_rule_to_fire(wm2);
        assertNotNull("Working with one wm had an effekt von working with another", resulting_Rule);
    }

    @AfterClass
    public static void run_once_after_Class() {
        ConstantValues.purge();
    }
}