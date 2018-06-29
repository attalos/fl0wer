package reasoner;

import evaluation.PerformanceResult;
import evaluation.ReasonerEvaluator;
import evaluation.ReasoningTask;
import helpers.OntologyWrapper;
import org.attalos.fl0wer.controll.FL_0_subsumption;
import org.attalos.fl0wer.utils.ConstantValues;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

import java.time.Duration;
import java.time.Instant;

public class Fl0werEvaluator implements ReasonerEvaluator {

    public Fl0werEvaluator() {
        ConstantValues.initialise(-1, false, false, false);
    }

    @Override
    public PerformanceResult evaluate(ReasoningTask reasoningTask) {
        return reasoningTask.evaluate(this);
    }

    @Override
    public PerformanceResult classify(OntologyWrapper ontology) {
        if (ontology.getOntology() != null) {
            //get time data
            Duration duration = Duration.ZERO;
            try {
                FL_0_subsumption fl0wer = new FL_0_subsumption(ontology.getOntology());
                Instant startingTime = Instant.now();
                fl0wer.classify();
                Instant finishTime = Instant.now();
                duration = Duration.between(startingTime, finishTime);
            } catch (Exception e) {
                System.err.println("Error occured - wrote time = 0 to output file");
                e.printStackTrace(System.err);
            }


            return new PerformanceResult("Fl0wer", ontology, duration);
        } else {
            return null;
        }
    }

    @Override
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
    }

    @Override
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
    }
}
