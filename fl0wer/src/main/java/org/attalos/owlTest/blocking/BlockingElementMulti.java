package org.attalos.owlTest.blocking;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by attalos on 7/2/17.
 */
public class BlockingElementMulti extends BlockingElement {
    //private Long blocking_elem
    private SortedSet<Long> blocking_elements;

    protected BlockingElementMulti(Long elem1_id, Long elem2_id) {
        this.blocking_elements = new TreeSet<>();
        this.blocking_elements.add(elem1_id);
        this.blocking_elements.add(elem2_id);
    }

    protected void insert_elem (Long elem_id) {
        this.blocking_elements.add(elem_id);
    }

    /**
     *
     * @param elem_id
     * @return null if there is more than one element left and the elem_id otherwise
     */
    protected Long remove_elem (Long elem_id) {
        this.blocking_elements.remove(elem_id);

        return this.blocking_elements.size() == 1 ? this.blocking_elements.first() : null;
    }

    @Override
    protected SortedSet<Long> get_blocking_elements() {
        return blocking_elements;
    }


    @Override
    public String toString() {
        String output = "{";
        for (Long id :blocking_elements) {
            output += id + ",";
        }
        output += "}";
        return output;
    }
}