package org.attalos.fl0wer.blocking;

import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by attalos on 7/2/17.
 */
public class BlockingElementSingle extends BlockingElement {
    private BigInteger elem_id;

    BlockingElementSingle(BigInteger elem_id) {
        this.elem_id = elem_id;
    }

    protected BigInteger get_elem_id() {
        return elem_id;
    }

    @Override
    protected SortedSet<BigInteger> get_blocking_elements() {
        SortedSet<BigInteger> return_set = new TreeSet<>();
        return_set.add(elem_id);
        return return_set;
    }
}
