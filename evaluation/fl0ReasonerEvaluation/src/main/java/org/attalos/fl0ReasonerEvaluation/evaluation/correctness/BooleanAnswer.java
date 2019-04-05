package org.attalos.fl0ReasonerEvaluation.evaluation.correctness;

public class BooleanAnswer implements ReasonerAnswer{

    private boolean val;

    public BooleanAnswer(boolean val) {
        this.val = val;
    }

    @Override
    public String toRepresentativeShortForm() {
        return val ? "answer:true" : "answer:false";
    }
}
