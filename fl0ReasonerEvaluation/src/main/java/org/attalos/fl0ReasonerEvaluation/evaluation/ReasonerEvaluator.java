package org.attalos.fl0ReasonerEvaluation.evaluation;

import org.attalos.fl0ReasonerEvaluation.evaluation.correctness.ReasonerAnswer;
import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;
import org.attalos.fl0ReasonerEvaluation.helpers.Tuple;
import org.semanticweb.owlapi.model.OWLClass;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Function;

public abstract class ReasonerEvaluator<T> {

    protected abstract Tuple<Duration, ReasonerAnswer> classificationMethod(T reasoner);
    protected abstract Tuple<Duration, ReasonerAnswer> superClassesMethod(T reasoner, OWLClass classOwl);
    protected abstract Tuple<Duration, ReasonerAnswer> subsumptionMethod(T reasoner, OWLClass subClassOwl, OWLClass superClassOwl);
    protected abstract T init(OntologyWrapper ontWrp);
    protected abstract String getReasonerName();

    public abstract PerformanceResult evaluate(ReasoningTask reasoningTask);

    /**
     *
     * Classification org.attalos.fl0ReasonerEvaluation.evaluation
     *
     */
    public final PerformanceResult classify(OntologyWrapper ontWrp,
                                            int taskID,
                                            long timeout) {
        return reason(ontWrp, taskID, timeout, (reasoner) -> classificationMethod(reasoner));
    }

    /**
     *
     * SuperClasses calculation org.attalos.fl0ReasonerEvaluation.evaluation
     *
     */
    public final PerformanceResult superClasses(OntologyWrapper ontWrp,
                                                int taskID,
                                                OWLClass classOwl,
                                                long timeout) {
        return reason(ontWrp, taskID, timeout, (reasoner) -> superClassesMethod(reasoner, classOwl));
    }

    /**
     *
     * Subsumption org.attalos.fl0ReasonerEvaluation.evaluation
     *
     */
    public final PerformanceResult subsumption(OntologyWrapper ontWrp,
                                                  int taskID,
                                                  OWLClass subClassOwl,
                                                  OWLClass superClassOwl,
                                                  long timeout) {
        return reason(ontWrp, taskID, timeout, (reasoner) -> subsumptionMethod(reasoner, subClassOwl, superClassOwl));
    }


    /**
     *
     * Generalised org.attalos.fl0ReasonerEvaluation.evaluation
     *
     */
    private PerformanceResult reason(OntologyWrapper ontWrp, int taskID, long timeout, Function<T, Tuple<Duration, ReasonerAnswer>> reasoningExecution) {
        if (ontWrp.getOntology() != null) {

            PerformanceResult result = new PerformanceResult(taskID, this.getReasonerName(), ontWrp);

            try {
                // init
                T reasoner = init(ontWrp);

                // prepare timeout method
                ExecutorService executor = Executors.newCachedThreadPool();
                Future<Tuple<Duration, ReasonerAnswer>> timeoutTask = executor.submit(() -> reasoningExecution.apply(reasoner));

                // run timeout method
                Tuple<Duration, ReasonerAnswer> answer = timeoutTask.get(timeout, TimeUnit.SECONDS);
                result.setDuration(answer.getLeft());
                result.setAnswer(answer.getRight());

                executor.shutdown();

            } catch (TimeoutException e) {
                System.err.println("Task ran into timeout. It took longer than " + timeout + " seconds.");
                result.ranIntoTimeout();
            } catch (Exception e) {
                System.err.println("Error occured - wrote time = " + timeout + " (maxtime) to output file");
                e.printStackTrace(System.err);
                result.threwException();
            }

            return result;
        } else {
            return null;
        }
    }
}
