package org.attalos.owlTest.normalization;

import java.util.LinkedList;

/**
 * Created by attalos on 4/25/17.
 */
public class Top implements Node_Res, Node_Con{
    private static Top instance = new Top();

    public static Top getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "\u22A4";
    }

    private Top() {
    }

    @Override
    public ValueRestriction prepand_roleword(LinkedList<Role> roleword) {
        return new ValueRestriction(roleword, this);
    }

    @Override
    public Top normalize_nf11_to_nf13() {
        return this;
    }

    @Override
    public Return_Node_Con normalize_one_step_nf14() {
        return new Return_Node_Con(this, false);
    }

    @Override
    public boolean is_nf2_normalized() {
        return true;
    }
}
