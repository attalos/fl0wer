package org.attalos.fl0wer.normalization;

/**
 * Created by attalos on 4/25/17.
 */
public interface ConceptDescription {
    String toString();

    ConceptDescription normalize_nf11_to_nf13();
    Return_Node_Con normalize_one_step_nf14();
    boolean is_nf2_normalized();
}
