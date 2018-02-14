package org.attalos.fl0wer.rete;

import java.util.*;

public class WorkingMemoryMultipleInputNodeData {
    private int incomming_path_count;
    private Map<Long, List<Boolean>> current_elements;

    protected WorkingMemoryMultipleInputNodeData(int incomming_path_count) {
        this.incomming_path_count = incomming_path_count;
        this.current_elements = new HashMap<>();
    }

    /**
     *
     * @param elem_index which gets propagated through the rete network
     * @param index_of_role index of role in currect
     * @return true, if the element came from every incomming path, so the rule is fullfilled
     */
    protected boolean elem_came_from(Long elem_index, int index_of_role) {

        List<Boolean> current_element;

        if (!current_elements.containsKey(elem_index)) {
            current_element = new ArrayList<>(Collections.nCopies(incomming_path_count, false));
            current_elements.put(elem_index, current_element);
        } else {
            current_element = current_elements.get(elem_index);
        }

        current_element.set(index_of_role, true);

        // all path are full filled
        return current_element.stream().allMatch(Boolean::booleanValue);
    }
}
