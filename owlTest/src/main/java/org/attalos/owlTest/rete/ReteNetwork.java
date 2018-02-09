package org.attalos.owlTest.rete;

import org.attalos.owlTest.blocking.BlockingCondition;
import org.attalos.owlTest.subsumption.ApplicableRule;
import org.attalos.owlTest.subsumption.HeadGCI;
import org.attalos.owlTest.subsumption.HeadOntology;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by attalos on 6/25/17.
 */
public class ReteNetwork {
    private ArrayList<ReteNode> top_level_nodes;
    private PriorityQueue<ApplicableRule> rule_queue;
    //private BlockingCondition blockingCondition;
    private int num_of_roles;

    public ReteNetwork(HeadOntology headOntology, int num_of_concepts, int num_of_roles) {
        this.top_level_nodes = new ArrayList<>(Collections.nCopies(num_of_concepts + 1, null));
        this.rule_queue = new PriorityQueue<>();
        //this.blockingCondition = new BlockingCondition();
        this.num_of_roles = num_of_roles;

        headOntology.get_gcis().forEach((HeadGCI head_gci) -> {
            ArrayList<Integer> used_roles = head_gci.get_subConceptHead().get_not_null_sucessor_rolenames();


            ReteFinalNode final_node = new ReteFinalNode(head_gci.get_superConceptHead(), rule_queue);

            if (used_roles.size() == 0) {
                //TODO (top concept)
            } else if (used_roles.size() == 1) {
                int used_role = used_roles.get(0);
                ArrayList<Integer> needed_concepts = head_gci.get_subConceptHead().get_concept_set_at(used_roles.get(0) + 1);
                //top_level_nodes.add(new ReteIntraElemNode(used_role, needed_concepts, final_node));
                add_rete_node(used_role, needed_concepts, final_node);
            } else {
                ReteMultipleInput collector = new ReteMultipleInput(used_roles, final_node);
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

    public void propagate_domain_elem(Long elem_id, Set<Integer> elem_concepts) {
        //void propagate_domain_elem(Long elem_index, int rolename, ArrayList<Long> domain_elem, int num_of_roles);

        int rolename = Math.toIntExact((elem_id - 1) % num_of_roles);

        for (Integer node_index : elem_concepts) {
            ReteNode rete_node = this.top_level_nodes.get(node_index);
            if (rete_node != null) {
                rete_node.propagate_domain_elem(elem_id, rolename, elem_concepts, this.num_of_roles);
            }
        }
    }

    /**
     *
     * @return the applicable rule with the highest priority or null if no rule is applicable
     */
    public ApplicableRule get_next_rule_to_fire() {
        return this.rule_queue.poll();
    }

    public void reenter_rule_to_queue(ApplicableRule ar) { this.rule_queue.offer(ar); }

    public String to_dot_graph() {
        String dot_string = "digraph g {\n";
        ArrayList<String> top_level = new ArrayList<>();
        ArrayList<String> role_level = new ArrayList<>();
        ArrayList<String> eq_level = new ArrayList<>();
        ArrayList<String> gci_level = new ArrayList<>();
        for (ReteNode top_level_node:top_level_nodes) {
            if (top_level_node != null) {
                top_level.add(Integer.toString(top_level_node.hashCode()));
                dot_string += "root -> " + Integer.toString(top_level_node.hashCode()) + "\n";
                dot_string += top_level_node.to_dot_graph(top_level, role_level, eq_level, gci_level, -1);
            }
        }

        dot_string += "{ rank=same; " + String.join(" ", top_level) + " }\n";
        dot_string += "{ rank=same; " + String.join(" ", role_level) + " }\n";
        dot_string += "{ rank=same; " + String.join(" ", eq_level) + " }\n";
        dot_string += "{ rank=same; " + String.join(" ", gci_level) + " }\n";
        dot_string += "}";
        return dot_string;
    }

    public void write_dot_graph() {
        try (PrintWriter out = new PrintWriter("rete_network.dot")) {
            out.print(this.to_dot_graph());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
