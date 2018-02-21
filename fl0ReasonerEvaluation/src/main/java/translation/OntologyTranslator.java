package translation;


import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;

public class OntologyTranslator {
    public static boolean fullfillsOwl2ElProfile(OWLOntology ontologyOwl) {

        OWL2ELProfile owl2ELProfile = new OWL2ELProfile();
        return owl2ELProfile.checkOntology(ontologyOwl).isInProfile();
    }

    public static void foo(OWLOntology ontologyOwl) {
//        ontologyOwl.applyChange();
//        OWLOntologyChange ontologyChange = new OWLOntologyChange() {
//        }
        ontologyOwl.axioms()
        //ontologyOwl.axioms().forEach(axiom -> axiom.ac);
    }
}
