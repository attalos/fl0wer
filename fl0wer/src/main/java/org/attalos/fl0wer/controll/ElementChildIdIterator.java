package org.attalos.fl0wer.controll;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ElementChildIdIterator implements Iterator<BigInteger> {

    private BigInteger current_element_id;      // is actually current -1 (see next())
    private BigInteger last_element_id;
    //for (Long child = this.num_of_roles * elem_id + 1; child <= this.num_of_roles * elem_id + this.num_of_roles; child++) {


    public ElementChildIdIterator(BigInteger num_of_roles, BigInteger elem_id) {
        this.current_element_id = elem_id.multiply(num_of_roles);
        last_element_id = elem_id.add(BigInteger.ONE).multiply(num_of_roles);
    }

    @Override
    public boolean hasNext() {
        return !current_element_id.equals(last_element_id);
    }

    @Override
    public BigInteger next() {
        if (current_element_id.equals(last_element_id)) throw new NoSuchElementException();
        current_element_id = current_element_id.add(BigInteger.ONE);
        return current_element_id;
    }
}
