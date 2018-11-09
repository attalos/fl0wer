package org.attalos.fl0ReasonerEvaluation.reasoner;

import org.attalos.fl0ReasonerEvaluation.evaluation.PerformanceResult;
import org.attalos.fl0ReasonerEvaluation.evaluation.ReasonerEvaluator;
import org.attalos.fl0ReasonerEvaluation.evaluation.ReasoningTask;
import org.attalos.fl0ReasonerEvaluation.evaluation.correctness.*;
import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;
import org.attalos.fl0ReasonerEvaluation.helpers.Tuple;
import org.attalos.fl0wer.FL0wer;
import org.attalos.fl0wer.utils.ConstantValues;
import org.semanticweb.owlapi.model.OWLClass;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Fl0werEvaluator extends ReasonerEvaluator<FL0wer> {

    public Fl0werEvaluator() {
        ConstantValues.initialise(-1, false, false, false);
    }

    @Override
    protected Tuple<Duration, ReasonerAnswer> classificationMethod(FL0wer fl0wer) {
        Instant startingTime = Instant.now();
        Map<OWLClass, Collection<OWLClass>> answerValue = fl0wer.classify();
        Instant finishTime = Instant.now();

        Duration duration = Duration.between(startingTime, finishTime);
        ReasonerAnswer answer = new ClassificationAnswer(answerValue);
        return new Tuple<>(duration, answer);
    }

    @Override
    protected Tuple<Duration, ReasonerAnswer> superClassesMethod(FL0wer fl0wer, OWLClass classOwl) {
        Instant startingTime = Instant.now();
        List<OWLClass> answerValue = fl0wer.calculate_subsumerset(classOwl);
        Instant finishTime = Instant.now();

        Duration duration = Duration.between(startingTime, finishTime);
        ReasonerAnswer answer = new SubsumersetAnswer(answerValue, classOwl);
        return new Tuple<>(duration, answer);
    }

    @Override
    protected Tuple<Duration, ReasonerAnswer> subsumptionMethod(FL0wer fl0wer, OWLClass subClassOwl, OWLClass superClassOwl) {
        Instant startingTime = Instant.now();
        boolean answerValue = fl0wer.decide_subsumption(subClassOwl, superClassOwl);
        Instant finishTime = Instant.now();

        Duration duration = Duration.between(startingTime, finishTime);
        ReasonerAnswer answer = new BooleanAnswer(answerValue);
        return new Tuple<>(duration, answer);
    }

    @Override
    protected FL0wer init(OntologyWrapper ontWrp) {
        return new FL0wer(ontWrp.getOntology());
    }

    @Override
    protected String getReasonerName() {
        return "Fl0wer";
    }

    @Override
    public PerformanceResult evaluate(ReasoningTask reasoningTask) {
        return reasoningTask.evaluate(this);
    }

    /*@Override
    public PerformanceResult classify(OntologyWrapper ontology) {
        if (ontology.getOntology() != null) {
            //get time data
            Duration duration = Duration.ZERO;
            try {
                FL_0_subsumption fl0wer = new FL_0_subsumption(ontology.getOntology());


                ExecutorService executor = Executors.newFixedThreadPool(1);
                Future<Void> timeoutTask = executor.submit(() -> {
                    Instant startingTime = Instant.now();
                    fl0wer.classify();
                    Instant finishTime = Instant.now();
                    return null;
                });


                duration = Duration.between(startingTime, finishTime);
            } catch (Exception e) {
                System.err.println("Error occured - wrote time = 0 to output file");
                e.printStackTrace(System.err);
            }


            return new PerformanceResult("Fl0wer", ontology, duration);
        } else {
            return null;
        }
    }*/

    /*@Override
    public PerformanceResult superClasses(OntologyWrapper ontology, OWLClass classOwl) {
        if (ontology.getOntology() != null) {
            FL_0_subsumption fl0wer = new FL_0_subsumption(ontology.getOntology());

            //get time data
            Instant startingTime = Instant.now();
            fl0wer.calculate_subsumerset(classOwl);
            Instant finishTime = Instant.now();

            Duration duration = Duration.between(startingTime, finishTime);
            return new PerformanceResult("Fl0wer", ontology, duration);
        } else {
            return null;
        }
    }*/

    /*@Override
    public PerformanceResult subsumption(OntologyWrapper ontology, OWLClass subClassOwl, OWLClass superClassOwl) {
        if (ontology.getOntology() != null) {
            FL_0_subsumption fl0wer = new FL_0_subsumption(ontology.getOntology());

            //get time data
            Instant startingTime = Instant.now();
            fl0wer.decide_subsumption(subClassOwl, superClassOwl);
            Instant finishTime = Instant.now();

            Duration duration = Duration.between(startingTime, finishTime);
            return new PerformanceResult("Fl0wer", ontology, duration);
        } else {
            return null;
        }
    }*/
}
