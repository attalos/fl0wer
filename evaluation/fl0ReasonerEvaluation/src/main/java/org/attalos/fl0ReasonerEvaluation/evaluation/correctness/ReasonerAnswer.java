package org.attalos.fl0ReasonerEvaluation.evaluation.correctness;

public interface ReasonerAnswer {

    /**
     *
     * @return This method should return some kind off representation of the answer
     * which is definitively equivalent for contentual equivalent answers.
     * If this value is different for the answers of two reasoners to the same problem,
     * then one reasoner has to be wrong (or this method has a bug).
     */
    String toRepresentativeShortForm();
}
