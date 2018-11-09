package org.attalos.fl0ReasonerEvaluation.reasoner;

import org.attalos.fl0ReasonerEvaluation.evaluation.PerformanceResult;
import org.attalos.fl0ReasonerEvaluation.evaluation.ReasonerEvaluator;
import org.attalos.fl0ReasonerEvaluation.evaluation.ReasoningTask;
import org.attalos.fl0ReasonerEvaluation.evaluation.correctness.*;
import org.attalos.fl0ReasonerEvaluation.helpers.OntologyWrapper;
import org.attalos.fl0ReasonerEvaluation.helpers.Tuple;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class OwlReasonerEvaluator extends ReasonerEvaluator<OWLReasoner> {

    protected abstract OWLReasoner createReasoner(OntologyWrapper ontWrp);

    @Override
    protected Tuple<Duration, ReasonerAnswer> classificationMethod(OWLReasoner reasoner) {
        Instant startingTime = Instant.now();
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        Instant finishTime = Instant.now();

        Map<OWLClass, NodeSet<OWLClass>> answerValue = new HashMap<>();
        reasoner.getRootOntology().classesInSignature().forEach(owlClass -> {       //todo maybe the getRootOntology ist the problem here
            answerValue.put(owlClass, reasoner.getSuperClasses(owlClass));
        });

        Duration duration = Duration.between(startingTime, finishTime);
        ReasonerAnswer answer = new ClassificationAnswer(answerValue, null);
        return new Tuple<>(duration, answer);

    }

    @Override
    protected Tuple<Duration, ReasonerAnswer> superClassesMethod(OWLReasoner reasoner, OWLClass classOwl) {
        Instant startingTime = Instant.now();
        NodeSet answerValue = reasoner.getSuperClasses(classOwl);
        Instant finishTime = Instant.now();

        Duration duration = Duration.between(startingTime, finishTime);
        ReasonerAnswer answer = new SubsumersetAnswer(answerValue);
        return new Tuple<>(duration, answer);

    }

    @Override
    protected Tuple<Duration, ReasonerAnswer> subsumptionMethod(OWLReasoner reasoner, OWLClass subClassOwl, OWLClass superClassOwl) {
        //prepare subsumption term
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClassExpression notSuperClassOwl = factory.getOWLObjectComplementOf(superClassOwl);
        OWLClassExpression subsumptionExpression = factory.getOWLObjectIntersectionOf(subClassOwl, notSuperClassOwl);

        //get data
        Instant startingTime = Instant.now();
        boolean answerValue = !reasoner.isSatisfiable(subsumptionExpression);
        Instant finishTime = Instant.now();

        //prepare returnvalue
        Duration duration = Duration.between(startingTime, finishTime);
        ReasonerAnswer answer = new BooleanAnswer(answerValue);
        return new Tuple<>(duration, answer);
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
