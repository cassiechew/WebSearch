package util;

import java.util.Vector;

/**
 * Deprecated as Java Priority queue is a thing
 */
@Deprecated
public class MinHeap {

    private Vector<Integer> accHeap;

    /**
     * Constructor
     */
    public MinHeap() {
        accHeap = new Vector<>();
    }

    /**
     * Constructor with intial capacity
     * @param c initial capacity
     */
    public MinHeap(int c) {
        accHeap = new Vector<>(c);
    }

    private int leftChild(int i) {
        return (2 * i + 1);
    }

    private int rightChild(int i) {
        return (2 * i + 2);
    }

    private int parent(int i) {
        return (i - 1)/2;
    }



}
