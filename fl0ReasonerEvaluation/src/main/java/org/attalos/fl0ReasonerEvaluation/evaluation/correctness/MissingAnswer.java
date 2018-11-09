package org.attalos.fl0ReasonerEvaluation.evaluation.correctness;

public class MissingAnswer implements ReasonerAnswer {

    private static final MissingAnswer instance = new MissingAnswer();

    private MissingAnswer() {}

    public static MissingAnswer getInstance() {
        return instance;
    }

    @Override
    public String toRepresentativeShortForm() {
        return "missingAnswer";
    }
}
