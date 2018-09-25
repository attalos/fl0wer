package org.attalos.fl0wer.controll;

import java.math.BigInteger;
import java.util.Iterator;

public class ElementIdIterator implements Iterator<BigInteger> {

    private int num_of_roles;
    private BigInteger current_element_id;
    private BigInteger last_element_id;
    //for (Long child = this.num_of_roles * elem_id + 1; child <= this.num_of_roles * elem_id + this.num_of_roles; child++) {

    //TODO - continue here -> implement constructor and hasNext() und next()

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public BigInteger next() {
        return null;
    }
}
