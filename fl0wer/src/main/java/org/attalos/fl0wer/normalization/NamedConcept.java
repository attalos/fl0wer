package org.attalos.fl0wer.normalization;

import java.util.LinkedList;

/**
 * Created by attalos on 4/24/17.
 */
public class NamedConcept implements Node_Res, Node_Con {
    int concept_name;


    public NamedConcept(int concept_name) {
        this.concept_name = concept_name;
    }

    /*public NamedConcept(OWLClass owl_class) {
        //concept_name = owl_class.toString().split("#")[1];
        concept_name = owl_class.toString();
        //concept_name = concept_name.substring(0, concept_name.length() -1);
    }*/

    public int getConcept_name() {

        return concept_name;
    }

    @Override
    public String toString() {
        /*String[] concept_name_split = concept_name.split("#");
        if ( concept_name_split.length == 2) {
            String return_name = concept_name_split[1];
            return_name = return_name.substring(0, return_name.length() - 1);
            return return_name;
        }*/
        return "A" + Integer.toString(concept_name);
    }

    /*public void setConcept_name(String concept_name) {
        this.concept_name = concept_name;
    }*/

    @Override
    public ValueRestriction prepand_roleword(LinkedList<Role> roleword) {
        return new ValueRestriction(roleword, this);
    }

    @Override
    public NamedConcept normalize_nf11_to_nf13() {
        return this;
    }

    @Override
    public Return_Node_Con normalize_one_step_nf14() {
        return new Return_Node_Con(this, false);
    }

    @Override
    public boolean is_nf2_normalized() {
        return true;
    }
}
