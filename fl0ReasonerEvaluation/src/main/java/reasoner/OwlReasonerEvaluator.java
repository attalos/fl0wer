package reasoner;

import evaluation.PerformanceResult;
import evaluation.ReasonerEvaluator;
import evaluation.ReasoningTask;
import helpers.OntologyWrapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.time.Duration;
import java.time.Instant;

public abstract class OwlReasonerEvaluator implements ReasonerEvaluator {

    protected abstract OWLReasoner createReasoner(OntologyWrapper ontology);

    protected abstract String getReasonerName();

    @Override
    public PerformanceResult evaluate(ReasoningTask reasoningTask) {
        return reasoningTask.evaluate(this);
    }

    @Override
    public PerformanceResult classify(OntologyWrapper ontology) {
        if (ontology.getOntology() != null) {
            OWLReasoner reasoner = createReasoner(ontology);

            //get time data
            Instant startingTime = Instant.now();
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            Instant finishTime = Instant.now();

            Duration duration = Duration.between(startingTime, finishTime);
            return new PerformanceResult(getReasonerName(), ontology, duration);
        } else {
            return null;
        }
    }

    @Override
    public PerformanceResult superClasses(OntologyWrapper ontology, OWLClass classOwl) {
        if (ontology.getOntology() != null) {
            OWLReasoner reasoner = createReasoner(ontology);

            //get time data
            Instant startingTime = Instant.now();
            reasoner.getSuperClasses(classOwl);
            Instant finishTime = Instant.now();

            Duration duration = Duration.between(startingTime, finishTime);
            return new PerformanceResult(getReasonerName(), ontology, duration);
        } else {
            return null;
        }
    }

    @Override
    public PerformanceResult subsumption(OntologyWrapper ontology, OWLClass subClassOwl, OWLClass superClassOwl) {
        if (ontology.getOntology() != null) {
            OWLReasoner reasoner = createReasoner(ontology);

            //prepare subsumption term
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLClassExpression notSuperClassOwl = factory.getOWLObjectComplementOf(superClassOwl);
            OWLClassExpression subsumptionExpression = factory.getOWLObjectIntersectionOf(subClassOwl, notSuperClassOwl);

            //get time data
            Instant startingTime = Instant.now();
            reasoner.isSatisfiable(subsumptionExpression);
            Instant finishTime = Instant.now();

            Duration duration = Duration.between(startingTime, finishTime);
            return new PerformanceResult(getReasonerName(), ontology, duration);
        } else {
            return null;
        }
    }
}
