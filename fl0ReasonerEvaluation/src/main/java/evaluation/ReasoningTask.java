package evaluation;

import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;
import java.util.stream.Stream;

public class ReasoningTask {
    private List<OWLOntology> ontologyList;

    public Stream<OWLOntology> ontologiesToClassify() {
        return ontologyList.stream();
    }
}
