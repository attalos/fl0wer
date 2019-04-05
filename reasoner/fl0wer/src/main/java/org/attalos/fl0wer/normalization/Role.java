package org.attalos.fl0wer.normalization;

/**
 * Created by attalos on 4/24/17.
 */
public class Role {
    private int name;

    public Role(int name) {

        this.name = name;
    }

    /*public Role(OWLObjectPropertyExpression owl_role) {

        //this.name = owl_role.toString().split("#")[1];
        this.name = owl_role.toString();
        //name = name.substring(0, name.length() - 1);
    }*/

    @Override
    public String toString() {
        /*String[] role_name_split = name.split("#");
        if ( role_name_split.length == 2) {
            String return_name = role_name_split[1];
            return_name = return_name.substring(0, return_name.length() - 1);
            return return_name;
        }*/
        return "r" + Integer.toString(name);
    }

    public int getName() {
        return name;
    }

    /*public void setName(String name) {
        this.name = name;
    }*/
}
