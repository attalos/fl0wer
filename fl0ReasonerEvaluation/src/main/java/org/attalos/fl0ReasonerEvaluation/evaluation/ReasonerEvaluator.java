package org.attalos.fl0ReasonerEvaluation.evaluation;

import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;
import org.semanticweb.owlapi.model.OWLClass;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Function;

public abstract class ReasonerEvaluator<T> {

    protected abstract Duration classificationMethod(T reasoner);
    protected abstract Duration superClassesMethod(T reasoner, OWLClass classOwl);
    protected abstract Duration substumptionMethod(T reasoner, OWLClass subClassOwl, OWLClass superClassOwl);
    protected abstract T init(OntologyWrapper ontWrp);
    protected abstract String getReasonerName();

    public abstract PerformanceResult evaluate(ReasoningTask reasoningTask);

    /**
     *
     * Classification org.attalos.fl0ReasonerEvaluation.evaluation
     *
     * @param ontWrp
     * @param timeout
     * @return
     */
    public final PerformanceResult classify(OntologyWrapper ontWrp, long timeout) {
        return reason(ontWrp, timeout, (reasoner) -> classificationMethod(reasoner));
    }

    /**
     *
     * SuperClasses calculation org.attalos.fl0ReasonerEvaluation.evaluation
     *
     * @param ontWrp
     * @param classOwl
     * @param timeout
     * @return
     */
    public final PerformanceResult superClasses(OntologyWrapper ontWrp, OWLClass classOwl, long timeout) {
        return reason(ontWrp, timeout, (reasoner) -> superClassesMethod(reasoner, classOwl));
    }

    /**
     *
     * Subsumption org.attalos.fl0ReasonerEvaluation.evaluation
     *
     * @param ontWrp
     * @param subClassOwl
     * @param superClassOwl
     * @param timeout
     * @return
     */
    public final PerformanceResult subsumption(OntologyWrapper ontWrp,
                                                  OWLClass subClassOwl,
                                                  OWLClass superClassOwl,
                                                  long timeout) {
        return reason(ontWrp, timeout, (reasoner) -> substumptionMethod(reasoner, subClassOwl, superClassOwl));
    }


    /**
     *
     * Generalised org.attalos.fl0ReasonerEvaluation.evaluation
     *
     * @param ontWrp
     * @param timeout
     * @param reasoningExecution
     * @return
     */
    private PerformanceResult reason(OntologyWrapper ontWrp, long timeout, Function<T, Duration> reasoningExecution) {
        if (ontWrp.getOntology() != null) {
            //get time data
            Duration duration = Duration.ofSeconds(timeout);

            T reasoner = null;
            ExecutorService executor = null;
            Future<Duration> timeoutTask = null;
            try {
                // init
                reasoner = init(ontWrp);
                T finalReasoner = reasoner;

                // prepare timeout method
                executor = Executors.newCachedThreadPool();
                timeoutTask = executor.submit(() -> reasoningExecution.apply(finalReasoner));

                // run timeout method
                duration = timeoutTask.get(timeout, TimeUnit.SECONDS);

                executor.shutdown();

            } catch (TimeoutException e) {
                System.err.println("Task ran into timeout. It took longer than " + timeout + " seconds.");
            } catch (Exception e) {
                System.err.println("Error occured - wrote time = " + timeout + " (maxtime) to output file");
                e.printStackTrace(System.err);
            }

            return new PerformanceResult(this.getReasonerName(), ontWrp, duration);
        } else {
            return null;
        }
    }
}
