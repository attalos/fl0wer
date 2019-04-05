package org.attalos.fl0wer.normalization;

/**
 * Created by attalos on 18.05.17.
 */
public class Return_Node_Con {
    private Node_Con conjunction;
    private boolean something_Changed;

    Return_Node_Con(Node_Con conjunction, boolean something_Changed) {
        this.conjunction = conjunction;
        this.something_Changed = something_Changed;
    }

    public Node_Con getConjunction() {
        return conjunction;
    }

    public boolean isSomething_Changed() {
        return something_Changed;
    }

    public void setConjunction(Node_Con conjunction) {
        this.conjunction = conjunction;
    }

    public void setSomething_Changed(boolean something_Changed) {
        this.something_Changed = something_Changed;
    }
}
