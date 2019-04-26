package org.attalos.fl0ReasonerEvaluation.evaluation;

import org.attalos.fl0ReasonerEvaluation.evaluation.correctness.MissingAnswer;
import org.attalos.fl0ReasonerEvaluation.evaluation.correctness.ReasonerAnswer;
import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;

import java.time.Duration;
import java.util.StringJoiner;

public class PerformanceResult {
    private int taskID;
    private String reasonerName;
    private OntologyWrapper ontology;
    private Duration duration = Duration.ofMillis(-1);
    private boolean threwException = false;
    private boolean ranIntoTimeout = false;
    private ReasonerAnswer answer = MissingAnswer.getInstance();


    PerformanceResult(int taskID, String reasonerName, OntologyWrapper ontology) {
        this.taskID = taskID;
        this.reasonerName = reasonerName;
        this.ontology = ontology;
    }

    public void threwException() {
        this.threwException = true;
    }

    public void ranIntoTimeout() {
        this.ranIntoTimeout = true;
    }

    public void setAnswer(ReasonerAnswer answer) {
        this.answer = answer;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String toCsvEntry(String separator) {
        StringJoiner joiner = new StringJoiner(separator);
        joiner
                .add(Integer.toString(taskID))
                .add(reasonerName)
                .add(ontology.getName())
                .add(Long.toString(ontology.getSize()))
                .add(Long.toString(duration.toMillis()))
                .add(booleanToString(threwException))
                .add(booleanToString(ranIntoTimeout))
                .add(answer.toRepresentativeShortForm())
                .add(Long.toString(ontology.getRolecount()));
        return joiner.toString();
    }

    public static String csvEntryHeader(String separator) {
        StringJoiner joiner = new StringJoiner(separator);
        joiner
                .add("taskID")
                .add("reasoner")
                .add("ontology")
                .add("classcount")
                .add("time")
                .add("threwException")
                .add("ranIntoTimeout")
                .add("answerHash")
                .add("rolecount");
        return joiner.toString();
    }

    private String booleanToString(boolean val) {
        if (val)
            return "true";
        return "false";
    }
}
