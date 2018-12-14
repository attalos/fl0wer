package org.attalos.fl0wer;

import org.attalos.fl0wer.controll.FunctionalElement;
import org.attalos.fl0wer.controll.SmallestFunctionalModelTree;
import org.attalos.fl0wer.normalization.Ontology;
import org.attalos.fl0wer.rete.ReteNetwork;
import org.attalos.fl0wer.rete.WorkingMemory;
import org.attalos.fl0wer.subsumption.ApplicableRule;
import org.attalos.fl0wer.subsumption.ConceptHead;
import org.attalos.fl0wer.subsumption.HeadOntology;
import org.attalos.fl0wer.utils.ConstantValues;
import org.attalos.fl0wer.utils.HelperFunctions;
import org.attalos.fl0wer.utils.OwlToInternalTranslator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by attalos on 7/1/17.
 */
public class FL0wer {
    private final static Logger LOGGER = Logger.getLogger(FL0wer.class.getName());
    private ReteNetwork rete_network;
    private Collection<OWLClass> input_owl_classes;
    private OwlToInternalTranslator owlToInternalTranslator = new OwlToInternalTranslator();


    public FL0wer(OWLOntology owl_ontology) {
        //get input classes
        OWLClass owl_top = OWLManager.createOWLOntologyManager().getOWLDataFactory().getOWLThing();
        input_owl_classes = owl_ontology.classesInSignature().filter(class_owl -> !class_owl.equals(owl_top)).collect(Collectors.toList());
        owlToInternalTranslator.initialize_original_owl_classes(input_owl_classes.stream());

        //internal ontology representation
//        ConstantValues.debug_info("creating internal ontology representation", 0);
        LOGGER.info("creating internal ontology representation");
        ConstantValues.start_timer("internal_representation");
        Ontology ontology = new Ontology(owl_ontology, owlToInternalTranslator);
        ConstantValues.stop_timer("internal_representation");
        //LOGGER.finest(ontology.toString() + "\n" + "###################");
//        if (ConstantValues.debug(2)) {
//            ConstantValues.debug_info(ontology.toString() + "\n" + "###################", 2);
//        }

        //normalize
//        ConstantValues.debug_info("normalizing ontology", 0);
        LOGGER.info("normalizing ontology");
        ConstantValues.start_timer("normalisation");
        ontology.normalize();
        ConstantValues.stop_timer("normalisation");
        //LOGGER.finest(ontology.toString() + "\n" + "###################");
//        if (ConstantValues.debug(2)) {
//            ConstantValues.debug_info(ontology.toString() + "\n" + "###################", 2);
//        }

        //lock OwlToInternalTranslator
        owlToInternalTranslator.lock();

//        if (owlToInternalTranslator.get_role_count() < 1) {
//            throw new RuntimeException("at least one role needed");
//        }

        //head ontology representation
//        ConstantValues.debug_info("creating headontology out of normalized ontology", 0);
        LOGGER.info("creating headontology out of normalized ontology");
        ConstantValues.start_timer("head_ontology");
        HeadOntology head_ontology = new HeadOntology(ontology, owlToInternalTranslator.get_role_count());
        ConstantValues.stop_timer("head_ontology");

        //rete network
        //ConstantValues.debug_info("creating rete network out of normalized ontology", 0);
        LOGGER.info("create_rete_network");
        ConstantValues.start_timer("create_rete_network");
        this.rete_network = new ReteNetwork(head_ontology, owlToInternalTranslator.get_concept_count(), owlToInternalTranslator.get_role_count());
        ConstantValues.stop_timer("create_rete_network");

        //dot graph of rete network
        if (ConstantValues.dots()) {
            LOGGER.info("writing dot graph");
//            ConstantValues.debug_info("writing dot graph", 0);
            ConstantValues.start_timer("create_dots");
            this.rete_network.write_dot_graph();
            ConstantValues.stop_timer("create_dots");
        }

    }

    private boolean general_subsumerset_calculation_with_break_condition(SmallestFunctionalModelTree subsumption_tree, Function<FunctionalElement, Boolean> break_condition) {
        //create workingmemory
        WorkingMemory workingmemory = rete_network.generate_new_WorkingMemory();

        //propagate first element
//        ConstantValues.debug_info("propagating root throw rete netwrok", 0);
        LOGGER.fine("propagating root throw rete network");
        this.rete_network.propagate_domain_elem(BigInteger.ZERO, subsumption_tree.get_concepts_of_elem(BigInteger.ZERO).getConcepts(), workingmemory);

        //mainloop which build functional model tree stump
        while (true) {
            //check break condition
            if (break_condition.apply(subsumption_tree.get_concepts_of_elem(BigInteger.ZERO))) {
                return true;
            }

            //get next rule
            ApplicableRule applicable_rule = this.rete_network.get_next_rule_to_fire(workingmemory);
            if (applicable_rule == null) {
                break;
            }
            BigInteger elem_id = applicable_rule.get_node_id();
            FunctionalElement elem = subsumption_tree.get_concepts_of_elem(elem_id);

            //check blocking
            if (elem.is_blocked()) {
                elem.insert_rule_to_hold_back(applicable_rule);
                continue;
            }

            //update subsumption_tree, blocking condition and propagate throw rete network
            ConceptHead new_concepts = applicable_rule.get_rule_right_side(); //TODO successor function instead of math magic everywhere
            ArrayList<Integer> successors_with_changes = new_concepts.get_not_null_successor_rolenames();
            if (successors_with_changes.size() == 0) {
                continue;
            }

            //temp debug:
            //System.out.print(applicable_rule.get_node_id().toString() + ", [");
            //for (int i : applicable_rule.get_rule_right_side().get_not_null_successor_rolenames()) {
            //    String classes = owlToInternalTranslator.translate_reverse(applicable_rule.get_rule_right_side().get_concept_set_at(i+1)).toString();
            //    System.out.print(" (" + i + " -> " + classes + ") ");
            //}
            //System.out.println("]");

            if (successors_with_changes.get(0) == -1) {             //update current node
                add_concepts_to_elem(elem_id, new_concepts.get_concept_set_at(0), subsumption_tree, workingmemory);
                successors_with_changes.remove(0);
            }

            //BigInteger first_successor = owlToInternalTranslator.get_role_count() * elem_id + 1; //TODO remove math "magic"
            BigInteger first_successor = HelperFunctions.calculateFirstChildId(elem_id,
                    BigInteger.valueOf(owlToInternalTranslator.get_role_count()));
            for (Integer rolename : successors_with_changes) {      //update direct successors
                add_concepts_to_elem(
                        first_successor.add(BigInteger.valueOf(rolename)),
                        new_concepts.get_concept_set_at(rolename + 1),
                        subsumption_tree,
                        workingmemory);
            }

//            // debug info - applied rule
//            if (ConstantValues.debug(1)) {
//                ConstantValues.debug_info("Applied rule: " + Long.toString(elem_id) + "\t-\t" + new_concepts.toString(), 1);
//            }
            //LOGGER.finer("Applied rule: " + Long.toString(elem_id) + "\t-\t" + new_concepts.toString());
        }
        //System.out.println(subsumption_tree.toDotGraph());

        return break_condition.apply(subsumption_tree.get_concepts_of_elem(BigInteger.ZERO));

    }

    @SuppressWarnings("UnusedReturnValue")
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
        return general_subsumerset_calculation_with_break_condition(subsumption_tree, func_elem ->
                func_elem.getConcepts().contains(subsumer_int));
    }

    @SuppressWarnings("UnusedReturnValue")
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
        List<OWLClass> subsumerset = owlToInternalTranslator.translate_reverse(
                subsumption_tree.get_concepts_of_elem(BigInteger.ZERO).getConcepts());
        ConstantValues.stop_timer("backtranslation");

        return subsumerset;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Map<OWLClass, Collection<OWLClass>> classify() {
        Map<OWLClass, Collection<OWLClass>> classificatoin_map = new HashMap<>();

        /*int class_count = input_owl_classes.size();
        int i = 0;
        long start_time = System.currentTimeMillis();
        for (OWLClass class_owl : input_owl_classes) {
            if (i % 1000 == 0) {
                System.out.println("status: " + Integer.toString(i) + " of " + Integer.toString(class_count) + " (average time per superclass calculation: " + Double.toString(((double)(System.currentTimeMillis() - start_time)) / ((double) i)) + "ms)");
            }
            i++;
            classificatoin_map.put(class_owl, calculate_subsumerset(class_owl));
        }*/
        input_owl_classes.parallelStream().forEach(class_owl -> {
            List<OWLClass> subsumerset = calculate_subsumerset(class_owl);
            synchronized (this) {
                classificatoin_map.put(class_owl, subsumerset);
            }
        });

        return classificatoin_map;
    }

    private void add_concepts_to_elem(BigInteger elem_id, ArrayList<Integer> new_concepts, SmallestFunctionalModelTree subsumption_tree, WorkingMemory wm) {
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
