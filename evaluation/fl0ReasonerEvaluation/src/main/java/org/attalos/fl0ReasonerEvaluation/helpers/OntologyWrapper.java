package org.attalos.fl0ReasonerEvaluation.helpers;

import org.attalos.fl0wer.normalization.Ontology;
import org.attalos.fl0wer.subsumption.HeadGCI;
import org.attalos.fl0wer.subsumption.HeadOntology;
import org.attalos.fl0wer.utils.ConstantValues;
import org.attalos.fl0wer.utils.OwlToInternalTranslator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class OntologyWrapper {
    private String name;
    private String ontologyPath;
    private OWLOntology ontology;
    private final long size;
    private final long rolecount;

    public OntologyWrapper(String name, String ontologyPath, OWLOntology ontology) {
        this.name = name;
        this.ontologyPath = ontologyPath;
        this.ontology = ontology;
        this.size = this.ontology.classesInSignature().count();
        this.rolecount = this.ontology.objectPropertiesInSignature().count();

//        System.out.println(name);
//        System.out.println(size);
//        System.out.println(belongsToHornFragment());
//        System.out.println("##############\n");
    }

    public String getName() {
        return name;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }

    public long getSize() {
        return size;
    }

    public long getRolecount() {
        return rolecount;
    }

    @Override
    public String toString() {
        return name + "," + ontologyPath;
    }



    private boolean belongsToHornFragment() {
        //procedere of fl0wer org.attalos.fl0ReasonerEvaluation.reasoner
        ConstantValues.initialise(-1, false, false, false);
        OwlToInternalTranslator o2iTranslator = new OwlToInternalTranslator();
        OWLClass owl_top = OWLManager.createOWLOntologyManager().getOWLDataFactory().getOWLThing();
        Collection<OWLClass> input_owl_classes = this.ontology.classesInSignature().filter(class_owl -> !class_owl.equals(owl_top)).collect(Collectors.toList());
        o2iTranslator.initialize_original_owl_classes(input_owl_classes.stream());
        Ontology fl0werOntology = new Ontology(this.ontology, o2iTranslator);
        fl0werOntology.normalize();
        o2iTranslator.lock();
        HeadOntology fl0werHeadOntology = new HeadOntology(fl0werOntology, o2iTranslator.get_role_count());


        //fl0werHeadOntology.get_gcis().forEach(fl0werHeadGCI -> {
        for (HeadGCI fl0werHeadGCI : fl0werHeadOntology.get_gcis()) {
            List<Integer> notNullSuccessors = fl0werHeadGCI.get_subConceptHead()
                    .get_not_null_successor_rolenames();

            notNullSuccessors.remove(Integer.valueOf(-1));


            if (notNullSuccessors.size() > 0) {
                ConstantValues.purge();
                return false;
            }
        }

        ConstantValues.purge();
        return true;
    }
}
