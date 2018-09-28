package org.attalos.fl0wer.blocking;

import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by attalos on 7/2/17.
 */
public class BlockingElementMulti extends BlockingElement {
    //private Long blocking_elem
    private SortedSet<BigInteger> blocking_elements;

    BlockingElementMulti(BigInteger elem1_id, BigInteger elem2_id) {
        this.blocking_elements = new TreeSet<>();
        this.blocking_elements.add(elem1_id);
        this.blocking_elements.add(elem2_id);
    }

    protected void insert_elem (BigInteger elem_id) {
        this.blocking_elements.add(elem_id);
    }

    /**
     *
     * @return null if there is more than one element left and the elem_id otherwise
     */
    protected BigInteger remove_elem (BigInteger elem_id) {
        this.blocking_elements.remove(elem_id);

        return this.blocking_elements.size() == 1 ? this.blocking_elements.first() : null;
    }

    @Override
    protected SortedSet<BigInteger> get_blocking_elements() {
        return blocking_elements;
    }


    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("{");
        for (BigInteger id : blocking_elements) {
            output.append(id).append(",");
        }
        output.append("}");
        return output.toString();
    }
}