package org.attalos.owlTest.blocking;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by attalos on 7/2/17.
 */
public class BlockingElementSingle extends BlockingElement {
    private Long elem_id;

    protected BlockingElementSingle(Long elem_id) {
        this.elem_id = elem_id;
    }

    protected Long get_elem_id() {
        return elem_id;
    }

    @Override
    protected SortedSet<Long> get_blocking_elements() {
        SortedSet<Long> return_set = new TreeSet<>();
        return_set.add(elem_id);
        return return_set;
    }
}
