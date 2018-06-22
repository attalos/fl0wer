package evaluation;

import helpers.OntologyWrapper;
import org.semanticweb.owlapi.model.OWLOntology;

import java.time.Duration;
import java.util.StringJoiner;

public class PerformanceResult {
    private String reasonerName;
    private OntologyWrapper ontology;
    private Duration duration;

    public PerformanceResult(String reasonerName, OntologyWrapper ontology, Duration duration) {
        this.reasonerName = reasonerName;
        this.ontology = ontology;
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public String toCsvEntry(String separator) {
        StringJoiner joiner = new StringJoiner(separator);
        joiner
                .add(reasonerName)
                .add(ontology.getName())
                .add(Long.toString(ontology.getSize()))
                .add(Long.toString(duration.toMillis()));
        return joiner.toString();
    }
}
