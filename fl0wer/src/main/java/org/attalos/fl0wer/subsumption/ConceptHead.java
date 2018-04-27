package org.attalos.fl0wer.subsumption;

import org.attalos.fl0wer.normalization.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by attalos on 6/8/17.
 */
public class ConceptHead implements Comparable<ConceptHead> {
    ArrayList<ArrayList<Integer>> head_array;
    ArrayList<Integer> notNullPositions;        //so you don't have to calculate it every time

    /*public ConceptHead(int num_of_roles) {
        head_array = new ArrayList<ArrayList<Long>>(num_of_roles + 1);
    }*/

    public ConceptHead(ConceptDescription concept, int num_of_roles) {
        head_array = new ArrayList<>(Collections.nCopies(num_of_roles + 1, null));
        //head_array = new ArrayList<>(num_of_roles + 1);
        //head_array.set(num_of_roles + 1, null);

        if(concept instanceof Conjunction) {
            ((Conjunction) concept).getConjuncts().forEach(conjunct -> {
                add_NodeRes(conjunct);
            });
        } else if (concept instanceof  Node_Res) {
            add_NodeRes((Node_Res) concept);
        } else {
            throw new RuntimeException("Concepts should either be Conjunctions or Node_Res - in ConceptHead");
        }

        ArrayList<Integer> head_start = head_array.get(0);
        if (head_start != null) {
            Collections.sort(head_start);
        }
    }

    private void add_NodeRes(Node_Res node_res) {
        if (node_res instanceof Top) {
            // nothing should happen
        } else if (node_res instanceof NamedConcept) {
            if (head_array.get(0) == null) {
                head_array.set(0, new ArrayList<>());
            }
            head_array.get(0).add(((NamedConcept) node_res).getConcept_name());
        } else if ( node_res instanceof ValueRestriction ) {
            ValueRestriction val_res = (ValueRestriction) node_res;
            if (val_res.getRole_word().size() != 1) {
                throw new RuntimeException("in PANF, value restrictions should contain exactly one role");
            }

            if( !(val_res.getConcept() instanceof NamedConcept) ) {
                throw new RuntimeException("in PANF, value restrictions should have named concepts as concepts");
            }

            int role_name = val_res.getRole_word().getFirst().getName();

            if (head_array.get(role_name + 1) == null) {
                head_array.set(role_name + 1, new ArrayList<>());
            }
            head_array.get(role_name + 1).add(((NamedConcept) val_res.getConcept()).getConcept_name());
        } else {
            throw new RuntimeException("unexpected Node_Res while creating ConceptHead");
        }
    }

    /**
     *
     * @return sorted (natural integer sorting) list of rolenames, which are not null successors.
     * The list contains -1 if the current element changes are not null
     * The set is calculated the first time this function is called and cached for later
     */
    public ArrayList<Integer> get_not_null_successor_rolenames() {
        if (notNullPositions == null) {
            synchronized (this) {
                //this realy should be done only ones
                if (notNullPositions == null) {
                    calculateNotNullSuccessorRoleNames();
                }
            }
        }
        return new ArrayList<>(notNullPositions);
    }

    private void calculateNotNullSuccessorRoleNames() {
        notNullPositions = new ArrayList<>();
        for (int i = 0; i < head_array.size(); i++) {
            if (head_array.get(i) != null) {
                notNullPositions.add(i - 1);
            }
        }
    }

    /**
     *
     * @param index (0 for current element and rulename + 1 otherwise)
     * @return concepts for given successor
     */
    public ArrayList<Integer> get_concept_set_at(int index) {
        return head_array.get(index);
    }

    public int get_head_index() {
        ArrayList<Integer> head_start = head_array.get(0);

        if (head_start != null && head_start.size() != 0) {
            return head_start.get(0);
        } else {
            return head_array.size() + 1;
        }
    }

    @Override
    public String toString() {

        StringJoiner outer_joiner = new StringJoiner(",");

        for (ArrayList<Integer> concept_set : head_array) {
            StringJoiner inner_joiner = new StringJoiner(", ");

            if (concept_set == null) {
                outer_joiner.add("");
            } else {
                for (Integer concept : concept_set) {
                    inner_joiner.add(concept.toString());
                }
                outer_joiner.add("{" + inner_joiner.toString() + "}");
            }
        }
        return "(" + outer_joiner.toString() + ")";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConceptHead that = (ConceptHead) o;
        return Objects.equals(head_array, that.head_array);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head_array);
    }

    @Override
    public int compareTo(ConceptHead other) {
        return Integer.compare(this.get_head_index(), other.get_head_index());
    }
}
