package org.attalos.fl0wer.normalization;

import java.util.LinkedList;

/**
 * Created by attalos on 4/24/17.
 */
public interface Node_Res extends ConceptDescription {
    ValueRestriction prepand_roleword(LinkedList<Role> roleword);
    Node_Res normalize_nf11_to_nf13();
}
