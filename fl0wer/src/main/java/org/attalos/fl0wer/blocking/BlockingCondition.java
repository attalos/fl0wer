package org.attalos.fl0wer.blocking;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by attalos on 7/2/17.
 */
public class BlockingCondition {
    private Map<Set<Integer>, BlockingElement> blocking_map;

    public BlockingCondition() {
        this.blocking_map = new HashMap<>();
    }

    /*
     *
     * @param elem_id which gets updated
     * @param elem_concepts concepts of the updated elements
     * @return true, if the set changed. False otherwise
     */

    /**
     *
     * @param elem_id
     * @param elem_concepts
     * @return false, if there are no changes in blocking relation possible and true otherwise
     */
    public boolean insert_blocking_element(BigInteger elem_id, Set<Integer> elem_concepts) {
        BlockingElement elem_of_interest = this.blocking_map.get(elem_concepts);

        if (elem_of_interest == null) {
            this.blocking_map.put(new HashSet<>(elem_concepts), new BlockingElementSingle(elem_id));
        } else if (elem_of_interest instanceof BlockingElementSingle) {
            this.blocking_map.put(new HashSet<>(elem_concepts), new BlockingElementMulti(((BlockingElementSingle) elem_of_interest).get_elem_id(), elem_id));
            return true;
        } else if (elem_of_interest instanceof BlockingElementMulti) {
            ((BlockingElementMulti) elem_of_interest).insert_elem(elem_id);
            return true;
        } else {
            throw new RuntimeException("Blocking Element should be Multi or Single");
        }

        return false;
    }

    /**
     *
     * @param elem_id
     * @param elem_concepts
     * @return false, if there are no changes in blocking relation possible and true otherwise
     */
    public boolean remove_blocking_element(BigInteger elem_id, Set<Integer> elem_concepts) {
        BlockingElement elem_of_interest = this.blocking_map.get(elem_concepts);

        if (elem_of_interest == null) {
            throw new RuntimeException("tried removing element from blocking map which wasn't in it");
        } else if (elem_of_interest instanceof BlockingElementSingle) {
            this.blocking_map.remove(elem_concepts);
        } else if (elem_of_interest instanceof BlockingElementMulti) {
            BigInteger one_elem_left = ((BlockingElementMulti) elem_of_interest).remove_elem(elem_id);
            if (one_elem_left != null) {
                this.blocking_map.put(new HashSet<>(elem_concepts), new BlockingElementSingle(one_elem_left));
            }
            return true;
        } else {
            throw new RuntimeException("Blocking Element should be Multi or Single");
        }

        return false;
    }

    /**
     *
     * @param elem_concepts
     * @return returns all elem_ids which have this exact concept set
     */
    public SortedSet<BigInteger> get_blocking_elements(Set<Integer> elem_concepts) {
        BlockingElement be = this.blocking_map.get(elem_concepts);

        if (be == null) {
            throw new RuntimeException("Wrong parameter");
        }

        return be.get_blocking_elements();
    }
}
