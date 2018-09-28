package org.attalos.fl0wer.rete;

import org.attalos.fl0wer.subsumption.ApplicableRule;

import java.util.PriorityQueue;

public class WorkingMemory {
    private PriorityQueue<ApplicableRule> rule_queue;
    private WorkingMemoryMultipleInputNodeData[] multipleInputNode_memory;
    private WorkingMemoryFinalNodeData[] finalNode_memory;

    WorkingMemory(int num_of_multipleInputNodes, int num_of_finalNodes) {
        rule_queue = new PriorityQueue<>();
        multipleInputNode_memory = new WorkingMemoryMultipleInputNodeData[num_of_multipleInputNodes];
        finalNode_memory = new WorkingMemoryFinalNodeData[num_of_finalNodes];
    }

    /**
     *
     * @return highest priority rule
     */
    protected ApplicableRule poll_rule () {
        return  rule_queue.poll();
    }

    /**
     *
     * @param rule to insert
     * @return see java.util.PriorityQueue.offer( ... )
     */
    protected boolean offer_rule(ApplicableRule rule) {
        return rule_queue.offer(rule);      //is the same as PriorityQueue.add( ... )
    }

    /**
     *
     * @param node_id of corresponding ReteMultipleInputNode
     * @return working memory found at id. If it doest exist yet, it will be initialisiert
     */
    protected WorkingMemoryMultipleInputNodeData get_multipleInputNode_memory_at(int node_id, int incomming_path_count) {
        WorkingMemoryMultipleInputNodeData requested_memory = multipleInputNode_memory[node_id];
        if (requested_memory == null) {
            requested_memory = new WorkingMemoryMultipleInputNodeData(incomming_path_count);
            multipleInputNode_memory[node_id] = requested_memory;
        }

        return requested_memory;
    }

    /**
     *
     * @param node_id of corresponding ReteFinalNode
     * @return working memory found at id. If it doest exist yet, it will be initialisiert
     */
    protected WorkingMemoryFinalNodeData get_finalNode_memory_at(int node_id) {
        WorkingMemoryFinalNodeData requested_memory = finalNode_memory[node_id];
        if (requested_memory == null) {
            requested_memory = new WorkingMemoryFinalNodeData();
            finalNode_memory[node_id] = requested_memory;
        }

        return requested_memory;
    }

}