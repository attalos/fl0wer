package org.attalos.fl0wer.rete;

import org.attalos.fl0wer.subsumption.ApplicableRule;
import org.attalos.fl0wer.subsumption.HeadGCI;
import org.attalos.fl0wer.subsumption.HeadOntology;
import org.attalos.fl0wer.utils.HelperFunctions;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by attalos on 6/25/17.
 */
public class ReteNetwork {
    private ArrayList<ReteNode> top_level_nodes;

    private int num_of_roles;
    private int num_of_multipleInputNodes = 0;
    private int num_of_finalNodes = 0;

    public ReteNetwork(HeadOntology headOntology, int num_of_concepts, int num_of_roles) {
        this.top_level_nodes = new ArrayList<>(Collections.nCopies(num_of_concepts + 1, null));

        this.num_of_roles = num_of_roles;

        headOntology.get_gcis().forEach((HeadGCI head_gci) -> {
            ArrayList<Integer> used_roles = head_gci.get_subConceptHead().get_not_null_successor_rolenames();


            ReteFinalNode final_node = new ReteFinalNode(head_gci.get_superConceptHead(), num_of_finalNodes++);

            if (used_roles.size() == 0) {
                //TODO (top concept)
                throw new RuntimeException("The top concept was used but the code is not able to handle it at the moment");
            } else if (used_roles.size() == 1) {
                int used_role = used_roles.get(0);
                ArrayList<Integer> needed_concepts = head_gci.get_subConceptHead().get_concept_set_at(used_roles.get(0) + 1);
                //top_level_nodes.add(new ReteIntraElemNode(used_role, needed_concepts, final_node));
                add_rete_node(used_role, needed_concepts, final_node);
            } else {
                ReteMultipleInput collector = new ReteMultipleInput(used_roles, final_node, num_of_multipleInputNodes++);
                for (Integer used_role : used_roles) {
                    ArrayList<Integer> needed_concepts = head_gci.get_subConceptHead().get_concept_set_at(used_role + 1);
                    if (used_role == -1) {
                        ReteRoleNode role_node = new ReteRoleNode(-1, collector);
                        this.add_rete_node(used_role, needed_concepts, role_node);
                    } else {
                        this.add_rete_node(used_role, needed_concepts, collector);
                    }
                }
            }
        });
    }

    private void add_rete_node(int used_role, ArrayList<Integer> needed_concepts, ReteNode final_node) {
        int insert_index = needed_concepts.get(0);
        ReteNode corresponding_node = top_level_nodes.get(insert_index);
        if (corresponding_node == null) {
            top_level_nodes.set(insert_index, new ReteIntraElemNode(used_role, needed_concepts, final_node));
        } else {
            corresponding_node.integrate_new_node(used_role, needed_concepts, final_node);
        }
    }

    public void propagateDomainElem(BigInteger elem_id, Set<Integer> elem_concepts, WorkingMemory wm) {
        //void propagateDomainElem(Long elem_index, int rolename, ArrayList<Long> domain_elem, int num_of_roles);

        int rolename = HelperFunctions.calculateRolename(elem_id, BigInteger.valueOf(num_of_roles));

        for (Integer node_index : elem_concepts) {
            ReteNode rete_node = this.top_level_nodes.get(node_index);
            if (rete_node != null) {
                rete_node.propagate_domain_elem(elem_id, rolename, elem_concepts, this.num_of_roles, wm);
            }
        }
    }

    public WorkingMemory generate_new_WorkingMemory() {
        return new WorkingMemory(num_of_multipleInputNodes);
    }

    /**
     *
     * @return the applicable rule with the highest priority or null if no rule is applicable
     */
    public ApplicableRule get_next_rule_to_fire(WorkingMemory wm) {
        return wm.pollRule();
    }

    public void reenter_rule_to_queue(ApplicableRule ar, WorkingMemory wm) { wm.offerRule(ar); }

    private String to_dot_graph() {
        StringBuilder dot_string = new StringBuilder("digraph g {\n");
        ArrayList<String> top_level = new ArrayList<>();
        ArrayList<String> role_level = new ArrayList<>();
        ArrayList<String> eq_level = new ArrayList<>();
        ArrayList<String> gci_level = new ArrayList<>();
        for (ReteNode top_level_node:top_level_nodes) {
            if (top_level_node != null) {
                top_level.add(Integer.toString(top_level_node.hashCode()));
                dot_string.append("root -> ").append(top_level_node.hashCode()).append("\n");
                dot_string.append(top_level_node.to_dot_graph(top_level, role_level, eq_level, gci_level, -1));
            }
        }

        dot_string.append("{ rank=same; ").append(String.join(" ", top_level)).append(" }\n");
        dot_string.append("{ rank=same; ").append(String.join(" ", role_level)).append(" }\n");
        dot_string.append("{ rank=same; ").append(String.join(" ", eq_level)).append(" }\n");
        dot_string.append("{ rank=same; ").append(String.join(" ", gci_level)).append(" }\n");
        dot_string.append("}");
        return dot_string.toString();
    }

    public void write_dot_graph() {
        try (PrintWriter out = new PrintWriter("rete_network.dot")) {
            out.print(this.to_dot_graph());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
