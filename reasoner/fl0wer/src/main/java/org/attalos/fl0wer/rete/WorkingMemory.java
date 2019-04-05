package org.attalos.fl0wer.rete;

import org.attalos.fl0wer.subsumption.ApplicableRule;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class WorkingMemory {
    private PriorityQueue<ApplicableRule> ruleQueue;
    private WorkingMemoryMultipleInputNodeData[] multipleInputNodeMemory;
    private Map<Integer, WorkingMemoryFinalNodeData> finalNodeMemory = new HashMap<>();

    WorkingMemory(int numOfMultipleInputNodes) {
        ruleQueue = new PriorityQueue<>();
        multipleInputNodeMemory = new WorkingMemoryMultipleInputNodeData[numOfMultipleInputNodes];
    }

    /**
     *
     * @return highest priority rule
     */
    ApplicableRule pollRule() {
        return  ruleQueue.poll();
    }

    /**
     *
     * @param rule to insert
     * @return see java.util.PriorityQueue.offer( ... )
     */
    boolean offerRule(ApplicableRule rule) {
        return ruleQueue.offer(rule);      //is the same as PriorityQueue.add( ... )
    }

    /**
     *
     * @param nodeId of corresponding ReteMultipleInputNode
     * @return working memory found at id. If it doest exist yet, it will be initialisiert
     */
    WorkingMemoryMultipleInputNodeData getMultipleInputNodeMemoryAt(int nodeId, int incommingPathCount) {
        WorkingMemoryMultipleInputNodeData requestedMemory = multipleInputNodeMemory[nodeId];
        if (requestedMemory == null) {
            requestedMemory = new WorkingMemoryMultipleInputNodeData(incommingPathCount);
            multipleInputNodeMemory[nodeId] = requestedMemory;
        }

        return requestedMemory;
    }

    /**
     *
     * @param nodeId of corresponding ReteFinalNode
     * @return working memory found at id. If it doest exist yet, it will be initialisiert
     */
    WorkingMemoryFinalNodeData getFinalNodeMemoryAt(int nodeId) {
        WorkingMemoryFinalNodeData requestedMemory = finalNodeMemory.get(nodeId);
        if (requestedMemory == null) {
            requestedMemory = new WorkingMemoryFinalNodeData();
            finalNodeMemory.put(nodeId, requestedMemory);
        }

        return requestedMemory;
    }

}