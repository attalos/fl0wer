package org.attalos.owlTest.controll;

import org.attalos.owlTest.App;
import org.attalos.owlTest.normalization.Concept_Factory;
import org.attalos.owlTest.normalization.Ontology;
import org.attalos.owlTest.rete.ReteNetwork;
import org.attalos.owlTest.subsumption.ApplicableRule;
import org.attalos.owlTest.subsumption.ConceptHead;
import org.attalos.owlTest.subsumption.HeadOntology;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by attalos on 7/1/17.
 */
public class FL_0_subsumption {
    private SmallestFunctionalModelTree subsumption_tree;
    private ReteNetwork rete_network;

    private Integer subsumer;
    private Integer subsumed;

    private int num_of_concepts;
    private int num_of_roles;

    //not a nice solution - but FunctionalElements net access to this because of the rete network
    private static FL_0_subsumption instance;

    public FL_0_subsumption(OWLOntology owl_ontology, OWLClass subsumed, OWLClass subsumer) {

        instance = this;

        //internal ontology representation
        ConstantValues.debug_info("creating internal ontology representation", 0);
        ConstantValues.start_timer("internal_representation");
        Ontology ontology = new Ontology(owl_ontology);
        ConstantValues.stop_timer("internal_representation");
        if (ConstantValues.debug(2)) {
            ConstantValues.debug_info(ontology.toString() + "\n" + "###################", 2);
        }

        //normalize
        ConstantValues.debug_info("normalizing ontology", 0);
        ConstantValues.start_timer("normalisation");
        ontology.normalize();
        ConstantValues.stop_timer("normalisation");
        if (ConstantValues.debug(2)) {
            ConstantValues.debug_info(ontology.toString() + "\n" + "###################", 2);
        }

        //get some needed values
        this.subsumer = ontology.get_internal_representation_of(subsumer);
        this.subsumed = ontology.get_internal_representation_of(subsumed);
        this.num_of_concepts = ontology.get_num_of_concepts();
        this.num_of_roles = ontology.get_num_of_roles();

        if (this.subsumed == -1) {
            throw new RuntimeException("given concept not found");
        }

        if (this.num_of_roles < 1) {
            throw new RuntimeException("at least one role needed");
        }

        //head ontology representation
        ConstantValues.debug_info("creating headontology out of normalized ontology", 0);
        ConstantValues.start_timer("head_ontology");
        HeadOntology head_ontology = new HeadOntology(ontology);
        ConstantValues.stop_timer("head_ontology");

        //smallest functional model representation
        this.subsumption_tree = new SmallestFunctionalModelTree(this.subsumed, num_of_roles);

        //rete network
        ConstantValues.debug_info("creating rete network out of normalized ontology", 0);
        ConstantValues.start_timer("create_rete_network");
        this.rete_network = new ReteNetwork(head_ontology, num_of_concepts, num_of_roles);
        ConstantValues.stop_timer("create_rete_network");

        //dot graph of rete network
        if (ConstantValues.dots()) {
            ConstantValues.debug_info("writing dot graph", 0);
            ConstantValues.start_timer("create_dots");
            this.rete_network.write_dot_graph();
            ConstantValues.stop_timer("create_dots");
        }

        //propagate first element
        ConstantValues.debug_info("propagating root throw rete netwrok", 0);
        this.rete_network.propagate_domain_elem(0L, this.subsumption_tree.get_concepts_of_elem(0L).getConcepts());

    }

    /**
     *
     * @return true, if subsumer subsumes subsumed and false otherwise
     */
    public boolean decide_subsumption() {
        //boolean rule_to_apply_left = true;
        //TODO add stop condition, when root element contains subsumer earlier

        ConstantValues.debug_info("starting main algorithm SUBS - so deciding subsumption", 0);

        while (true) {
            /* check whether subsumption relation is already solved */
            if (ConstantValues.is_subsumption()) {
                if (this.subsumption_tree.get_concepts_of_elem(0L).getConcepts().contains(this.subsumer)) {
                    System.out.println("The subsumption relation holds");
                    return true;
                }
            }

            ApplicableRule applicable_rule = this.rete_network.get_next_rule_to_fire();

            if (applicable_rule == null) {
                break;
            }

            Long elem_id = applicable_rule.get_node_id();
            FunctionalElement elem = this.subsumption_tree.get_concepts_of_elem(elem_id);
            if (elem.is_blocked()) {
                elem.insert_rule_to_hold_back(applicable_rule);
                continue;
            }

            Long first_successor = this.num_of_roles * elem_id + 1;
            ConceptHead new_concepts = applicable_rule.get_rule_right_side();

            // debug info - applied rule
            if (ConstantValues.debug(1)) {
                ConstantValues.debug_info(Long.toString(elem_id) + "\t-\t" + new_concepts.toString(), 1);
            }

            ArrayList<Integer> successors_with_changes = new_concepts.get_not_null_sucessor_rolenames();

            if (successors_with_changes.size() == 0) {
                continue;
            }

            if (successors_with_changes.get(0) == -1) {
                add_concepts_to_elem(elem_id, new_concepts.get_concept_set_at(0));
                successors_with_changes.remove(0);
            }



            for (Integer rolename : successors_with_changes) {
                add_concepts_to_elem(first_successor + rolename, new_concepts.get_concept_set_at(rolename + 1));
            }

        }

        if(!ConstantValues.is_subsumption()) {
            ConstantValues.start_timer("backtranslation");
            List<OWLClass> subsumerset = Concept_Factory.getInstance().translate_int_to_OWLClass(this.subsumption_tree.get_concepts_of_elem(0L).getConcepts());
            ConstantValues.stop_timer("backtranslation");
            for (OWLClass subsumer : subsumerset) {
                System.out.println(App.toString_expression(subsumer));
            }
        } else {
            // just because it should exist
            ConstantValues.start_timer("backtranslation");
            ConstantValues.stop_timer("backtranslation");
            if (this.subsumption_tree.get_concepts_of_elem(0L).getConcepts().contains(this.subsumer)) {
                System.out.println("The subsumption relation holds");
                return  true;
            } else {
                System.out.println("The subsumption relation doesn't hold");
                return false;
            }
        }


        // TODO doesn't make realy sense when calculating subsumerset
        return false;
    }

    private void add_concepts_to_elem(Long elem_id, ArrayList<Integer> new_concepts) {
        if (this.subsumption_tree.update_node(elem_id, new_concepts)) {
            ConstantValues.start_timer("rete_propagation");
            this.rete_network.propagate_domain_elem(elem_id, subsumption_tree.get_concepts_of_elem(elem_id).getConcepts());
            ConstantValues.stop_timer("rete_propagation");
        }
    }

    protected static FL_0_subsumption get_instance() {
        return instance;
    }

    protected void reenter_rules_to_queue(Collection<ApplicableRule> applicable_rules) {
        for (ApplicableRule applicable_rule : applicable_rules) {
            this.rete_network.reenter_rule_to_queue(applicable_rule);
        }
    }
}
