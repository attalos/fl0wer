package translation;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;

import java.util.Objects;

public class OntologyTranslator {
    private static OWL2ELProfile owl2ELProfile = new OWL2ELProfile();
    private static TranslationAxiomVisitor translationAxiomVisitor = new TranslationAxiomVisitor();
    private static RawFL0VerificationVisitor rawFL0VerificationVisitor = new RawFL0VerificationVisitor();

    public static boolean fullfillsOwl2ElProfile(OWLOntology ontologyOwl) {
        return owl2ELProfile.checkOntology(ontologyOwl).isInProfile();
    }

    public static OWLOntology translateELtoFL0(OWLOntology ontologyOwl) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        return  manager.createOntology(ontologyOwl.axioms()
                .map(owlAxiom -> owlAxiom.accept(translationAxiomVisitor))
                .filter(Objects::nonNull));
    }

    public static boolean isRawFL0(OWLOntology ontologyOwl) {
        return ontologyOwl.accept(rawFL0VerificationVisitor);
    }
}
