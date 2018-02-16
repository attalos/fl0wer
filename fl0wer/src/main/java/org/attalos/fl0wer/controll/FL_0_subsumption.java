package org.attalos.fl0wer.controll;

import org.attalos.fl0wer.App;
import org.attalos.fl0wer.normalization.Ontology;
import org.attalos.fl0wer.rete.ReteNetwork;
import org.attalos.fl0wer.rete.WorkingMemory;
import org.attalos.fl0wer.subsumption.ApplicableRule;
import org.attalos.fl0wer.subsumption.ConceptHead;
import org.attalos.fl0wer.subsumption.HeadOntology;
import org.attalos.fl0wer.utils.ConstantValues;
import org.attalos.fl0wer.utils.OwlToInternalTranslator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by attalos on 7/1/17.
 */
public class FL_0_subsumption {
    private ReteNetwork rete_network;
    private Stream<OWLClass> input_owl_classes;
    OwlToInternalTranslator owlToInternalTranslator = new OwlToInternalTranslator();


    public FL_0_subsumption(OWLOntology owl_ontology) {
        //get input classes
        OWLClass owl_top = OWLManager.createOWLOntologyManager().getOWLDataFactory().getOWLThing();
        input_owl_classes = owl_ontology.classesInSignature().filter(class_owl -> !class_owl.equals(owl_top));
        owlToInternalTranslator.initialize_original_owl_classes(input_owl_classes);

        //internal ontology representation
        ConstantValues.debug_info("creating internal ontology representation", 0);
        ConstantValues.start_timer("internal_representation");
        Ontology ontology = new Ontology(owl_ontology, owlToInternalTranslator);
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

        //lock OwlToInternalTranslator
        owlToInternalTranslator.lock();

        if (owlToInternalTranslator.get_role_count() < 1) {
            throw new RuntimeException("at least one role needed");
        }

        //head ontology representation
        ConstantValues.debug_info("creating headontology out of normalized ontology", 0);
        ConstantValues.start_timer("head_ontology");
        HeadOntology head_ontology = new HeadOntology(ontology, owlToInternalTranslator.get_role_count());
        ConstantValues.stop_timer("head_ontology");

        //rete network
        ConstantValues.debug_info("creating rete network out of normalized ontology", 0);
        ConstantValues.start_timer("create_rete_network");
        this.rete_network = new ReteNetwork(head_ontology, owlToInternalTranslator.get_concept_count(), owlToInternalTranslator.get_role_count());
        ConstantValues.stop_timer("create_rete_network");

        //dot graph of rete network
        if (ConstantValues.dots()) {
            ConstantValues.debug_info("writing dot graph", 0);
            ConstantValues.start_timer("create_dots");
            this.rete_network.write_dot_graph();
            ConstantValues.stop_timer("create_dots");
        }

    }

    private boolean general_subsumerset_calculation_with_break_condition(SmallestFunctionalModelTree subsumption_tree, Function<FunctionalElement, Boolean> break_condition) {
        //create workingmemory
        WorkingMemory workingmemory = rete_network.generate_new_WorkingMemory();

        //propagate first element
        ConstantValues.debug_info("propagating root throw rete netwrok", 0);
        this.rete_network.propagate_domain_elem(0L, subsumption_tree.get_concepts_of_elem(0L).getConcepts(), workingmemory);

        //mainloop which build functional model tree stump
        while (true) {
            //check break condition
            if (break_condition.apply(subsumption_tree.get_concepts_of_elem(0L))) {
                return true;
            }

            //get next rule
            ApplicableRule applicable_rule = this.rete_network.get_next_rule_to_fire(workingmemory);
            if (applicable_rule == null) {
                break;
            }
            Long elem_id = applicable_rule.get_node_id();
            FunctionalElement elem = subsumption_tree.get_concepts_of_elem(elem_id);

            //check blocking
            if (elem.is_blocked()) {
                elem.insert_rule_to_hold_back(applicable_rule);
                continue;
            }

            //update subsumption_tree, blocking condition and propagate throw rete network
            ConceptHead new_concepts = applicable_rule.get_rule_right_side(); //TODO successor function instead of math magic everywhere
            ArrayList<Integer> successors_with_changes = new_concepts.get_not_null_sucessor_rolenames();
            if (successors_with_changes.size() == 0) {
                continue;
            }

            if (successors_with_changes.get(0) == -1) {             //update current node
                add_concepts_to_elem(elem_id, new_concepts.get_concept_set_at(0), subsumption_tree, workingmemory);
                successors_with_changes.remove(0);
            }

            Long first_successor = owlToInternalTranslator.get_role_count() * elem_id + 1; //TODO remove math "magic"
            for (Integer rolename : successors_with_changes) {      //update direct successors
                add_concepts_to_elem(first_successor + rolename, new_concepts.get_concept_set_at(rolename + 1), subsumption_tree, workingmemory);
            }

            // debug info - applied rule
            if (ConstantValues.debug(1)) {
                ConstantValues.debug_info("Applied rule: " + Long.toString(elem_id) + "\t-\t" + new_concepts.toString(), 1);
            }
        }

        if (break_condition.apply(subsumption_tree.get_concepts_of_elem(0L))) {
            return true;
        }

        return false;
    }

    public boolean decide_subsumption(OWLClass subsumed, OWLClass subsumer) {
        //get some needed values
        //TODO do not use Concept_Factory directly!
        int subsumer_int = owlToInternalTranslator.translate(subsumer).getConcept_name();
        int subsumed_int = owlToInternalTranslator.translate(subsumed).getConcept_name();
        if (subsumed_int == -1 || subsumer_int == -1) {
            throw new RuntimeException("given concept not found");
        }

        //smallest functional model representation
        SmallestFunctionalModelTree subsumption_tree = new SmallestFunctionalModelTree(subsumed_int, owlToInternalTranslator.get_role_count());

        //main part
        return general_subsumerset_calculation_with_break_condition(subsumption_tree, func_elem -> func_elem.getConcepts().contains(subsumer));
    }

    public List<OWLClass> calculate_subsumerset(OWLClass subsumed) {
        //get some needed values
        //TODO do not use Concept_Factory directly!
        int subsumed_int = owlToInternalTranslator.translate(subsumed).getConcept_name();
        if (subsumed_int == -1) {
            throw new RuntimeException("given concept not found");
        }

        //smallest functional model representation
        SmallestFunctionalModelTree subsumption_tree = new SmallestFunctionalModelTree(subsumed_int, owlToInternalTranslator.get_role_count());

        //main part
        general_subsumerset_calculation_with_break_condition(subsumption_tree, func_elem -> false);

        //backtranslation
        ConstantValues.start_timer("backtranslation");
        List<OWLClass> subsumerset = owlToInternalTranslator.translate_reverse(subsumption_tree.get_concepts_of_elem(0L).getConcepts());
        ConstantValues.stop_timer("backtranslation");
        for (OWLClass subsumer : subsumerset) {
            System.out.println(App.toString_expression(subsumer));
        }

        return subsumerset;
    }

    public Map<OWLClass, Collection<OWLClass>> classify() {
        Map<OWLClass, Collection<OWLClass>> classificatoin_map = new HashMap<>();

        input_owl_classes.forEach(class_owl -> {
            classificatoin_map.put(class_owl, calculate_subsumerset(class_owl));
        });
        return classificatoin_map;
    }

//    /**
//     *
//     * @return true, if subsumer subsumes subsumed and false otherwise
//     */
//    public boolean decide_subsumption() {
//        //boolean rule_to_apply_left = true;
//        //TODO add stop condition, when root element contains subsumer earlier
//
//        ConstantValues.debug_info("starting main algorithm SUBS - so deciding subsumption", 0);
//
//        while (true) {
//            /* check whether subsumption relation is already solved */
//            if (ConstantValues.is_subsumption()) {
//                if (this.subsumption_tree.get_concepts_of_elem(0L).getConcepts().contains(this.subsumer)) {
//                    System.out.println("The subsumption relation holds");
//                    return true;
//                }
//            }
//
//            ApplicableRule applicable_rule = this.rete_network.get_next_rule_to_fire(this.workingmemory);
//
//            if (applicable_rule == null) {
//                break;
//            }
//
//            Long elem_id = applicable_rule.get_node_id();
//            FunctionalElement elem = this.subsumption_tree.get_concepts_of_elem(elem_id);
//            if (elem.is_blocked()) {
//                elem.insert_rule_to_hold_back(applicable_rule);
//                continue;
//            }
//
//            Long first_successor = this.num_of_roles * elem_id + 1;
//            ConceptHead new_concepts = applicable_rule.get_rule_right_side();
//
//            // debug info - applied rule
//            if (ConstantValues.debug(1)) {
//                ConstantValues.debug_info(Long.toString(elem_id) + "\t-\t" + new_concepts.toString(), 1);
//            }
//
//            ArrayList<Integer> successors_with_changes = new_concepts.get_not_null_sucessor_rolenames();
//
//            if (successors_with_changes.size() == 0) {
//                continue;
//            }
//
//            if (successors_with_changes.get(0) == -1) {
//                add_concepts_to_elem(elem_id, new_concepts.get_concept_set_at(0));
//                successors_with_changes.remove(0);
//            }
//
//
//
//            for (Integer rolename : successors_with_changes) {
//                add_concepts_to_elem(first_successor + rolename, new_concepts.get_concept_set_at(rolename + 1));
//            }
//
//        }
//
//        if(!ConstantValues.is_subsumption()) {
//            ConstantValues.start_timer("backtranslation");
//            List<OWLClass> subsumerset = Concept_Factory.getInstance().translate_int_to_OWLClass(this.subsumption_tree.get_concepts_of_elem(0L).getConcepts());
//            ConstantValues.stop_timer("backtranslation");
//            for (OWLClass subsumer : subsumerset) {
//                System.out.println(App.toString_expression(subsumer));
//            }
//        } else {
//            // just because it should exist
//            ConstantValues.start_timer("backtranslation");
//            ConstantValues.stop_timer("backtranslation");
//            if (this.subsumption_tree.get_concepts_of_elem(0L).getConcepts().contains(this.subsumer)) {
//                System.out.println("The subsumption relation holds");
//                return  true;
//            } else {
//                System.out.println("The subsumption relation doesn't hold");
//                return false;
//            }
//        }
//
//
//        // TODO doesn't make realy sense when calculating subsumerset
//        return false;
//    }

    private void add_concepts_to_elem(Long elem_id, ArrayList<Integer> new_concepts, SmallestFunctionalModelTree subsumption_tree, WorkingMemory wm) {
        if (subsumption_tree.update_node(elem_id, new_concepts, applicable_rules -> reenter_rules_to_queue(applicable_rules, wm))) {
            ConstantValues.start_timer("rete_propagation");
            this.rete_network.propagate_domain_elem(elem_id, subsumption_tree.get_concepts_of_elem(elem_id).getConcepts(), wm);
            ConstantValues.stop_timer("rete_propagation");
        }
    }

    private void reenter_rules_to_queue(Collection<ApplicableRule> applicable_rules, WorkingMemory wm) {
        for (ApplicableRule applicable_rule : applicable_rules) {
            this.rete_network.reenter_rule_to_queue(applicable_rule, wm);
        }
    }
}
