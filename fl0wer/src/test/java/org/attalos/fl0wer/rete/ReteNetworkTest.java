package org.attalos.fl0wer.rete;

import org.attalos.fl0wer.controll.ConstantValues;
import org.attalos.fl0wer.normalization.Concept_Factory;
import org.attalos.fl0wer.normalization.Ontology;
import org.attalos.fl0wer.subsumption.ApplicableRule;
import org.attalos.fl0wer.subsumption.HeadOntology;
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
    }

    @Before
    public void setUp() {
        /* owl init */
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();

        try {
            /* open ontology */
            ConstantValues.debug_info("opening ontologie with the owl-api", 0);
            File ontology_file = new File("src/test/resources/reteTestOntology.owl");
            OWLOntology ontology_owl = m.loadOntologyFromOntologyDocument(ontology_file);
            class_A = Concept_Factory.getInstance().get_concept_from_owl_class(factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2018/1/reteTestOntology#A"))).getConcept_name();
            class_B = Concept_Factory.getInstance().get_concept_from_owl_class(factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2018/1/reteTestOntology#B"))).getConcept_name();
            class_C = Concept_Factory.getInstance().get_concept_from_owl_class(factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2018/1/reteTestOntology#C"))).getConcept_name();
            class_D = Concept_Factory.getInstance().get_concept_from_owl_class(factory.getOWLClass(IRI.create("http://www.semanticweb.org/attalos/ontologies/2018/1/reteTestOntology#D"))).getConcept_name();
            roll_r = 0;

            //internal ontology representation
            Ontology ontology = new Ontology(ontology_owl);

            //normalize
            ontology.normalize();

            //head ontology representation
            HeadOntology head_ontology = new HeadOntology(ontology);

            //rete network
            int num_of_concepts = ontology.get_num_of_concepts();
            int num_of_roles = ontology.get_num_of_roles();
            this.rete_network = new ReteNetwork(head_ontology, num_of_concepts, num_of_roles);
            this.rete_network.write_dot_graph();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSimple_rule_detection() {
        Set<Integer> input_concept_set = new HashSet<>();
        input_concept_set.add(class_C);

        List<Integer> expected_output_concept_set = new ArrayList<>();
        expected_output_concept_set.add(class_D);

        rete_network.propagate_domain_elem(0L, input_concept_set);
        ApplicableRule resulting_Rule = rete_network.get_next_rule_to_fire();

        assertNotNull("There was no applicable rule found", resulting_Rule);
        assertNull("There were to many applicable rules found", this.rete_network.get_next_rule_to_fire());
        assertEquals("Resulting rule was refering to wrong domain element", 0, resulting_Rule.get_node_id());
        assertEquals("Resulting rule contained wrong concepts", expected_output_concept_set, resulting_Rule.get_rule_right_side().get_concept_set_at(0));
        assertNull("Resulting rule contained wrong concepts", resulting_Rule.get_rule_right_side().get_concept_set_at(roll_r + 1));

    }

    @Test
    public void testWorking_with_multiple_dom_elems() {
        Set<Integer> input_concept_set = new HashSet<>();
        input_concept_set.add(class_C);

        rete_network.propagate_domain_elem(0L, input_concept_set);
        ApplicableRule resulting_Rule = rete_network.get_next_rule_to_fire();
        assertNotNull("(see simple_rule_detection)", resulting_Rule);
        assertNull("(see simple_rule_detection)", this.rete_network.get_next_rule_to_fire());

        rete_network.propagate_domain_elem(77L, input_concept_set);
        resulting_Rule = rete_network.get_next_rule_to_fire();
        assertNotNull("There was no applicable rule found for second dom elem", resulting_Rule);
        assertNull("There were to many applicable rules found for second dom elem", this.rete_network.get_next_rule_to_fire());
        assertEquals("Resulting rule was refering to wrong domain element", 77, resulting_Rule.get_node_id());
    }

    @Test
    public void testMultiple_input_node() {
        Set<Integer> input_concept_set_0 = new HashSet<>();
        input_concept_set_0.add(class_A);

        Set<Integer> input_concept_set_1 = new HashSet<>();
        input_concept_set_1.add(class_B);

        List<Integer> expected_output_concept_set = new ArrayList<>();
        expected_output_concept_set.add(class_D);

        rete_network.propagate_domain_elem(0L, input_concept_set_0);
        rete_network.propagate_domain_elem(1L, input_concept_set_1);

        ApplicableRule resulting_Rule = rete_network.get_next_rule_to_fire();

        assertNotNull("There was no applicable rule found", resulting_Rule);
        assertNull("There were to many applicable rules found", this.rete_network.get_next_rule_to_fire());
        assertEquals("Resulting rule was refering to wrong domain element", 0, resulting_Rule.get_node_id());
        assertEquals("Resulting rule contained wrong concepts", expected_output_concept_set, resulting_Rule.get_rule_right_side().get_concept_set_at(0));
        assertNull("Resulting rule contained wrong concepts", resulting_Rule.get_rule_right_side().get_concept_set_at(1));

    }

    @Test
    public void testFinal_node_repetation() {
        Set<Integer> input_concept_set = new HashSet<>();
        input_concept_set.add(class_C);

        rete_network.propagate_domain_elem(0L, input_concept_set);
        rete_network.get_next_rule_to_fire();

        rete_network.propagate_domain_elem(0L, input_concept_set);
        ApplicableRule resulting_Rule = rete_network.get_next_rule_to_fire();
        assertNull("Every rule should only be found once per dom elem", resulting_Rule);
    }
}