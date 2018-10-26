package org.attalos.fl0ReasonerEvaluation.reasoner;

import org.attalos.fl0ReasonerEvaluation.evaluation.PerformanceResult;
import org.attalos.fl0ReasonerEvaluation.evaluation.ReasonerEvaluator;
import org.attalos.fl0ReasonerEvaluation.evaluation.ReasoningTask;
import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.time.Duration;
import java.time.Instant;

public abstract class OwlReasonerEvaluator extends ReasonerEvaluator<OWLReasoner> {

    protected abstract OWLReasoner createReasoner(OntologyWrapper ontWrp);

    @Override
    protected Duration classificationMethod(OWLReasoner reasoner) {
        Instant startingTime = Instant.now();
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        Instant finishTime = Instant.now();
        return Duration.between(startingTime, finishTime);

    }

    @Override
    protected Duration superClassesMethod(OWLReasoner reasoner, OWLClass classOwl) {
        Instant startingTime = Instant.now();
        reasoner.getSuperClasses(classOwl);
        Instant finishTime = Instant.now();
        return Duration.between(startingTime, finishTime);

    }

    @Override
    protected Duration substumptionMethod(OWLReasoner reasoner, OWLClass subClassOwl, OWLClass superClassOwl) {
        //prepare subsumption term
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClassExpression notSuperClassOwl = factory.getOWLObjectComplementOf(superClassOwl);
        OWLClassExpression subsumptionExpression = factory.getOWLObjectIntersectionOf(subClassOwl, notSuperClassOwl);

        //get time data
        Instant startingTime = Instant.now();
        reasoner.isSatisfiable(subsumptionExpression);
        Instant finishTime = Instant.now();
        return Duration.between(startingTime, finishTime);
    }

    @Override
    protected OWLReasoner init(OntologyWrapper ontWrp) {
        return createReasoner(ontWrp);
    }

    @Override
    public PerformanceResult evaluate(ReasoningTask reasoningTask) {
        return reasoningTask.evaluate(this);
    }
}
