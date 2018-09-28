package org.attalos.fl0wer.controll;

import org.attalos.fl0wer.blocking.BlockingCondition;
import org.attalos.fl0wer.subsumption.ApplicableRule;
import org.attalos.fl0wer.utils.ConstantValues;
import org.attalos.fl0wer.utils.HelperFunctions;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by attalos on 29.06.17.
 */
public class SmallestFunctionalModelTree {
    //TODO try performance difference for hashmap
    private Map<BigInteger, FunctionalElement> model_tree;
    private BlockingCondition blocking_condition;
    private BigInteger num_of_roles;

    public SmallestFunctionalModelTree(Integer start_concept, int num_of_roles) {
        this.blocking_condition = new BlockingCondition();
        this.num_of_roles = BigInteger.valueOf(num_of_roles);

        model_tree = new HashMap<>();
        ArrayList<Integer> root_concepts = new ArrayList<>();
        root_concepts.add(start_concept);

        FunctionalElement root_func_elem = new FunctionalElement(root_concepts);
        this.model_tree.put(BigInteger.ZERO, root_func_elem);
        this.blocking_condition.insert_blocking_element(BigInteger.ZERO, root_func_elem.getConcepts());

    }

    public FunctionalElement get_concepts_of_elem(BigInteger elem_id) {
        return this.model_tree.get(elem_id);
    }

    /**
     *
     * @param node which should get modified
     * @param new_concepts which should get added
     * @return true, if something changed. False otherwise
     */
    public boolean update_node(BigInteger node, ArrayList<Integer> new_concepts, Consumer<List<ApplicableRule>> rule_release_function) {
        FunctionalElement elem_to_change = model_tree.get(node);

        if (elem_to_change != null) {
            if (!elem_to_change.would_change_elem(new_concepts)) {
                return false;
            }

            /*
             * remove from previous blocking conditions
             */
            ConstantValues.start_timer("blocking");
            SortedSet<BigInteger> blocking_element = this.blocking_condition.get_blocking_elements(elem_to_change.getConcepts());
            if (this.blocking_condition.remove_blocking_element(node, elem_to_change.getConcepts())) {
                this.handle_blocking_element(blocking_element, rule_release_function);
            }
            ConstantValues.stop_timer("blocking");
        } else {
            elem_to_change = new FunctionalElement(new_concepts);
            this.model_tree.put(node, elem_to_change);

            //set right blocking condition
            if (!node.equals(BigInteger.ZERO)) {
                BigInteger parent_id = HelperFunctions.calculateParentId(node, num_of_roles);
                FunctionalElement parent = model_tree.get(parent_id);

                if (parent == null) {
                    throw new RuntimeException("parent should never be null");
                } else if (parent.is_blocked()) {
                    elem_to_change.set_indirectly_blocked(true, rule_release_function);
                }
            }

        }

        /*
         * manipulate element
         */
        elem_to_change.add_to_concepts(new_concepts);

        /*
         * add to new blocking conditions
         */
        ConstantValues.start_timer("blocking");
        if (this.blocking_condition.insert_blocking_element(node, elem_to_change.getConcepts())) {
            SortedSet<BigInteger> blocking_element = this.blocking_condition.get_blocking_elements(elem_to_change.getConcepts());
            this.handle_blocking_element(blocking_element, rule_release_function);
        }
        ConstantValues.stop_timer("blocking");

        return true;
    }

    private void block_element_directly(BigInteger elem_id, Consumer<List<ApplicableRule>> rule_release_function) {
        FunctionalElement func_elem = this.model_tree.get(elem_id);
        if (func_elem == null) {
            throw new RuntimeException("tried to block no existing element - in SmallestFunctionalModel");
        }

        func_elem.set_directly_blocked(true, rule_release_function);

        /*
         * block successors indirectly
         */
        Iterator<BigInteger> it = new ElementChildIdIterator(this.num_of_roles, elem_id);
        while (it.hasNext()) {
            BigInteger next = it.next();
            block_element_indirectly(next, rule_release_function);
        }
    }

    private void unblock_element_directly(BigInteger elem_id, Consumer<List<ApplicableRule>> rule_release_function) {
        FunctionalElement func_elem = this.model_tree.get(elem_id);
        if (func_elem == null) {
            throw new RuntimeException("tried to unblock no existing element - in SmallestFunctionalModel");
        }

        func_elem.set_directly_blocked(false, rule_release_function);

        /*
         * unblock successors indirectly
         */
        if (!func_elem.is_indirectly_blocked()) {
            Iterator<BigInteger> it = new ElementChildIdIterator(this.num_of_roles, elem_id);
            while (it.hasNext()) {
                unblock_element_indirectly(it.next(), rule_release_function);
            }
        }

    }

    private void block_element_indirectly(BigInteger elem_id, Consumer<List<ApplicableRule>> rule_release_function) {
        FunctionalElement func_elem = this.model_tree.get(elem_id);
        if (func_elem == null || func_elem.is_indirectly_blocked()) {
            return;
        }

        func_elem.set_indirectly_blocked(true, rule_release_function);

        if (func_elem.is_directly_blocking()) {
            handle_blocking_element(this.blocking_condition.get_blocking_elements(func_elem.getConcepts()), rule_release_function);
        }

        /*
         * block successors indirectly
         */
        Iterator<BigInteger> it = new ElementChildIdIterator(this.num_of_roles, elem_id);
        while (it.hasNext()) {
            block_element_indirectly(it.next(), rule_release_function);
        }
    }

    private void unblock_element_indirectly(BigInteger elem_id, Consumer<List<ApplicableRule>> rule_release_function) {
        FunctionalElement func_elem = this.model_tree.get(elem_id);
        if (func_elem == null) {
            return;
        }

        if (func_elem.is_directly_blocking()) {
            handle_blocking_element(this.blocking_condition.get_blocking_elements(func_elem.getConcepts()), rule_release_function);
        }

        /*
         * unblock successors indirectly
         */
        if (!func_elem.is_directly_blocked()) {
            Iterator<BigInteger> it = new ElementChildIdIterator(this.num_of_roles, elem_id);
            while (it.hasNext()) {
                unblock_element_indirectly(it.next(), rule_release_function);
            }
        }

    }

    private void handle_blocking_element(SortedSet<BigInteger> blocking_element, Consumer<List<ApplicableRule>> rule_release_function) {
        Iterator<BigInteger> iterator = blocking_element.iterator();

        /*
         * unblock elements and find&set blocking element
         */
        while (iterator.hasNext()) {
            BigInteger elem_id = iterator.next();
            FunctionalElement elem = this.model_tree.get(elem_id);

            //set is blocking
            if (iterator.hasNext()) {
                elem.set_directly_blocking(true);
            } else {
                elem.set_directly_blocking(false);
            }

            //unblock
            if (elem.is_directly_blocked()) {
                this.unblock_element_directly(elem_id, rule_release_function);
            }

            //find blocking element
            if ((!elem.is_indirectly_blocked())) {
                break;
            }
        }

        /*
         * set blocked element
         */
        while (iterator.hasNext()) {
            BigInteger elem_id = iterator.next();
            FunctionalElement elem = this.model_tree.get(elem_id);

            elem.set_directly_blocking(false);
            if (!elem.is_directly_blocked()) {
                this.block_element_directly(elem_id, rule_release_function);
            }
        }
    }

    /*
    private void use_function_on_all_successors(Callable<Long> function, Long elem_id) {
        Iterator<Map.Entry<Long, FunctionalElement>> iterator = model_tree.tailMap(4 * elem_id + 1).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, FunctionalElement> map_entry = iterator.next();

            if (map_entry.getKey() > 4 * elem_id + this.num_of_roles) {
                break;
            }

            //hate java and missing function pointer...
            function.call();
        }
    }
    */
}
