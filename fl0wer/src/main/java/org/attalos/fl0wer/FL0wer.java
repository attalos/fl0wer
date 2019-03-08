package org.attalos.fl0wer;

import com.google.common.collect.Iterables;
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

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by attalos on 7/1/17.
 */
public class FL0wer {
    private final static Logger LOGGER = Logger.getLogger(FL0wer.class.getName());
    private ReteNetwork reteNetwork;
    private Collection<OWLClass> inputOwlClasses;
    private OwlToInternalTranslator owlToInternalTranslator = new OwlToInternalTranslator();


    public FL0wer(OWLOntology owl_ontology) {
        //get input classes
        OWLClass owl_top = OWLManager.createOWLOntologyManager().getOWLDataFactory().getOWLThing();
        inputOwlClasses = owl_ontology.classesInSignature().filter(class_owl -> !class_owl.equals(owl_top)).collect(Collectors.toList());
        owlToInternalTranslator.initialize_original_owl_classes(inputOwlClasses.stream());

        //internal ontology representation
        LOGGER.info("creating internal ontology representation");
        ConstantValues.startTimer("internal_representation");
        Ontology ontology = new Ontology(owl_ontology, owlToInternalTranslator);
        ConstantValues.stopTimer("internal_representation");

        //normalize
        LOGGER.info("normalizing ontology");
        ConstantValues.startTimer("normalisation");
        ontology.normalize();
        ConstantValues.stopTimer("normalisation");

        //lock OwlToInternalTranslator
        owlToInternalTranslator.lock();

//        if (owlToInternalTranslator.get_role_count() < 1) {
//            throw new RuntimeException("at least one role needed");
//        }

        //head ontology representation
        LOGGER.info("creating headontology out of normalized ontology");
        ConstantValues.startTimer("head_ontology");
        HeadOntology head_ontology = new HeadOntology(ontology, owlToInternalTranslator.get_role_count());
        ConstantValues.stopTimer("head_ontology");

        //rete network
        LOGGER.info("create_rete_network");
        ConstantValues.startTimer("create_rete_network");
        this.reteNetwork = new ReteNetwork(head_ontology, owlToInternalTranslator.get_concept_count(), owlToInternalTranslator.get_role_count());
        ConstantValues.stopTimer("create_rete_network");

        //dot graph of rete network
        if (ConstantValues.dots()) {
            LOGGER.info("writing dot graph");
            ConstantValues.startTimer("create_dots");
            this.reteNetwork.write_dot_graph();
            ConstantValues.stopTimer("create_dots");
        }

    }

    private boolean generalSubsumersetCalculationWithBreakCondition(SmallestFunctionalModelTree subsumption_tree, Function<FunctionalElement, Boolean> break_condition) {
        //create workingmemory
        WorkingMemory workingmemory = reteNetwork.generate_new_WorkingMemory();

        //propagate first element
        LOGGER.fine("propagating root throw rete network");
        this.reteNetwork.propagateDomainElem(BigInteger.ZERO, subsumption_tree.getConceptsOfElem(BigInteger.ZERO).getConcepts(), workingmemory);

        //mainloop which builds functional model tree stump
        while (true) {
            /*
             * check break condition
             */
            if (break_condition.apply(subsumption_tree.getConceptsOfElem(BigInteger.ZERO))) {
                return true;
            }

            /*
             * get next rule
             */
            ApplicableRule applicable_rule = this.reteNetwork.get_next_rule_to_fire(workingmemory);
            if (applicable_rule == null) {
                break;
            }
            BigInteger elem_id = applicable_rule.get_node_id();
            FunctionalElement elem = subsumption_tree.getConceptsOfElem(elem_id);

            /*
             * check blocking
             */
            if (elem.is_blocked()) {
                elem.insert_rule_to_hold_back(applicable_rule);
                continue;
            }

            /*
             * update subsumption_tree, blocking condition and propagate throw rete network
             */
            ConceptHead new_concepts = applicable_rule.get_rule_right_side();
            ArrayList<Integer> successors_with_changes = new_concepts.get_not_null_successor_rolenames();
            if (successors_with_changes.size() == 0)
                continue;
            //update current node
            if (successors_with_changes.get(0) == -1) {
                addConceptsToElem(elem_id, new_concepts.get_concept_set_at(0), subsumption_tree, workingmemory);
                successors_with_changes.remove(0);
            }
            //update direct successors
            BigInteger first_successor = HelperFunctions.calculateFirstChildId(elem_id,
                    BigInteger.valueOf(owlToInternalTranslator.get_role_count()));
            for (Integer rolename : successors_with_changes) {
                addConceptsToElem(
                        first_successor.add(BigInteger.valueOf(rolename)),
                        new_concepts.get_concept_set_at(rolename + 1),
                        subsumption_tree,
                        workingmemory);
            }

            /*
             * feedback
             */
            // print applied rules
            if (ConstantValues.showAppliedRules()) {
                System.out.print(applicable_rule.get_node_id().toString() + ", [");
                for (int i : applicable_rule.get_rule_right_side().get_not_null_successor_rolenames()) {
                    String classes = owlToInternalTranslator.translate_reverse(applicable_rule.get_rule_right_side().get_concept_set_at(i + 1)).toString();
                    System.out.print(" (" + i + " -> " + classes + ") ");
                }
                System.out.println("]");
            }

            // animate functional model tree
            if (ConstantValues.animateFunctionalModelTree())
                showCurrentFunctionalModelTree(subsumption_tree,500);
        }

        return break_condition.apply(subsumption_tree.getConceptsOfElem(BigInteger.ZERO));

    }

    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public boolean decide_subsumption(OWLClass subsumed, OWLClass subsumer) {
        //get some needed values
        int subsumer_int = owlToInternalTranslator.translate(subsumer).getConcept_name();
        int subsumed_int = owlToInternalTranslator.translate(subsumed).getConcept_name();
        if (subsumed_int == -1 || subsumer_int == -1) {
            throw new RuntimeException("given concept not found");
        }

        //smallest functional model representation
        SmallestFunctionalModelTree subsumption_tree = new SmallestFunctionalModelTree(subsumed_int, owlToInternalTranslator.get_role_count());

        //main part
        return generalSubsumersetCalculationWithBreakCondition(subsumption_tree, func_elem ->
                func_elem.getConcepts().contains(subsumer_int));
    }

    @SuppressWarnings("UnusedReturnValue")
    public List<OWLClass> calculate_subsumerset(OWLClass subsumed) {
        //get some needed values
        int subsumed_int = owlToInternalTranslator.translate(subsumed).getConcept_name();
        if (subsumed_int == -1) {
            throw new RuntimeException("given concept not found");
        }

        //smallest functional model representation
        SmallestFunctionalModelTree subsumption_tree = new SmallestFunctionalModelTree(subsumed_int, owlToInternalTranslator.get_role_count());

        //main part
        generalSubsumersetCalculationWithBreakCondition(subsumption_tree, func_elem -> false);
        if (ConstantValues.animateFunctionalModelTree())
            showCurrentFunctionalModelTree(subsumption_tree, 40000);

        //backtranslation
        ConstantValues.startTimer("backtranslation");
        List<OWLClass> subsumerset = owlToInternalTranslator.translate_reverse(
                subsumption_tree.getConceptsOfElem(BigInteger.ZERO).getConcepts());
        ConstantValues.stopTimer("backtranslation");

        return subsumerset;
    }

    /**
     *
     * Classify the ontology given to the constructor of this class
     * @return A mapping of owl classes to all subsumers
     */
    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public Map<OWLClass, Collection<OWLClass>> classify() {
        Map<OWLClass, Collection<OWLClass>> classificationMap = new HashMap<>();

        /* progress info */
        final AtomicInteger i = new AtomicInteger();
        long startTime = System.currentTimeMillis();
        int classCount = inputOwlClasses.size();

/*        inputOwlClasses.parallelStream().forEach(class_owl -> {
            /* calculate */
/*            List<OWLClass> subsumerset = calculate_subsumerset(class_owl);
/*            synchronized (this) {
                /* write */
/*                classificationMap.put(class_owl, subsumerset);

                /* print progress */
/*                if (ConstantValues.progress()) {
                    if (i.getAndIncrement() % 1000 == 0) {
                        System.out.println("status: " + i + " of " + classCount + " (average time per superclass calculation: " +
                                ((double) (System.currentTimeMillis() - startTime)) / ((double) i.get()) + "ms)");
                    }
                }
            }
        });*/

        ExecutorService es = Executors.newFixedThreadPool(4);
        //System.out.println("gogogo");
        Iterable<List<OWLClass>> inputClasses = Iterables.partition(inputOwlClasses, 48);
        for (List<OWLClass> ic : inputClasses) {
            es.submit(() -> {
                for (OWLClass class_owl : ic) {
                    List<OWLClass> subsumerset = calculate_subsumerset(class_owl);
                    synchronized (this) {
                        classificationMap.put(class_owl, subsumerset);
                    }
                }
            });
        }
        try {
            es.shutdown();
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*int i = 0;
        int class_count = inputOwlClasses.size();
        long start_time = System.currentTimeMillis();
        for (OWLClass class_owl : inputOwlClasses) {
            List<OWLClass> subsumerset = calculate_subsumerset(class_owl);
            synchronized (this) {
                classificationMap.put(class_owl, subsumerset);
            }

            if (i % 1000 == 0) {
                System.out.println("status: " + i + " of " + class_count + " (average time per superclass calculation: " +
                        ((double) (System.currentTimeMillis() - start_time)) / ((double) i) + "ms)");
            }
            i++;
        }*/


        return classificationMap;
    }


    /**
     * Changes a node inside the subsumption tree, including an update of the blocking condition and a propagation through
     * the rete network.
     *
     * @param elemId element in question
     * @param newConcepts concepts, that will get added to the element described by elemId
     * @param subsumptionTree smallest functional model tree
     * @param wm current workingmemory
     */
    private void addConceptsToElem(BigInteger elemId, ArrayList<Integer> newConcepts, SmallestFunctionalModelTree subsumptionTree, WorkingMemory wm) {
        if (subsumptionTree.updateNode(elemId, newConcepts, applicableRules -> reenterRulesToQueue(applicableRules, wm))) {
            ConstantValues.startTimer("rete_propagation");
            this.reteNetwork.propagateDomainElem(elemId, subsumptionTree.getConceptsOfElem(elemId).getConcepts(), wm);
            ConstantValues.stopTimer("rete_propagation");
        }
    }

    private void reenterRulesToQueue(Collection<ApplicableRule> applicable_rules, WorkingMemory wm) {
        for (ApplicableRule applicable_rule : applicable_rules) {
            this.reteNetwork.reenter_rule_to_queue(applicable_rule, wm);
        }
    }

    private void showCurrentFunctionalModelTree(SmallestFunctionalModelTree subsumptionTree, int showForXMilliSeconds) {
        try {
            String showForXSeconds = Integer.toString((showForXMilliSeconds / 1000) + 2);
            String xdotStarter = "xdot -g 1920x1080 <( echo '" + subsumptionTree.toDotGraph() + "') & sleep " + showForXSeconds + "; kill $!";
            Process p = new ProcessBuilder("/bin/bash", "-c", xdotStarter).start();
            p.waitFor(showForXMilliSeconds, TimeUnit.MILLISECONDS);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
