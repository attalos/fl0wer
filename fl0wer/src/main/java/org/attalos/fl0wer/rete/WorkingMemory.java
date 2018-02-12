package org.attalos.fl0wer.rete;

import org.attalos.fl0wer.subsumption.ApplicableRule;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class WorkingMemory {
    private PriorityQueue<ApplicableRule> rule_queue;
    private ArrayList<WorkingMemoryMultipleInputNodeData> multipleInputNode_memory;
    private ArrayList<WorkingMemoryFinalNodeData> finalNode_memory;

    public WorkingMemory(int num_of_multipleInputNodes, int num_of_finalNodes) {
        rule_queue = new PriorityQueue<>();
        multipleInputNode_memory = new ArrayList<>(num_of_multipleInputNodes);
        multipleInputNode_memory = new ArrayList<>(num_of_finalNodes);
    }

    /**
     *
     * @param node_id of corresponding ReteMultipleInputNode
     * @return working memory found at id. If it doest exist yet, it will be initialisiert
     */
    protected WorkingMemoryMultipleInputNodeData get_multipleInputNode_memory_at(int node_id, int incomming_path_count) {
        WorkingMemoryMultipleInputNodeData requested_memory = multipleInputNode_memory.get(node_id);
        if (requested_memory == null) {git
            requested_memory = new WorkingMemoryMultipleInputNodeData(incomming_path_count);
            multipleInputNode_memory.set(node_id, requested_memory);
        }

        return requested_memory;
    }

    /**
     *
     * @param node_id of corresponding ReteFinalNode
     * @return working memory found at id. If it doest exist yet, it will be initialisiert
     */
    protected WorkingMemoryFinalNodeData get_finalNode_memory_at(int node_id) {
        WorkingMemoryFinalNodeData requested_memory = finalNode_memory.get(node_id);
        if (requested_memory == null) {
            requested_memory = new WorkingMemoryFinalNodeData();
            finalNode_memory.set(node_id, requested_memory);
        }

        return requested_memory;
    }

}