package org.attalos.fl0ReasonerEvaluation.helpers;

public class Tuple<T, V> {
    private T left;
    private V right;

    public Tuple(T left, V right) {
        this.left = left;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public void setLeft(T left) {
        this.left = left;
    }

    public V getRight() {
        return right;
    }

    public void setRight(V right) {
        this.right = right;
    }
}
