package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;

import java.nio.IntBuffer;

public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    public int count() {
        return buffer.capacity() / NODE_INTS;
    }

    public double nodeE(int nodeId) {
        return buffer.get(nodeId * NODE_INTS);
    }

    public double nodeN(int nodeId) {
        return buffer.get((nodeId * NODE_INTS) + OFFSET_N);
    }

    // int outDegree(int nodeId), qui retourne
    // le nombre d'arêtes sortant du nœud d'identité donné,
    public int outDegree(int nodeId) {
        int attribut3 = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(attribut3,28, 4);
    }

    // int edgeId(int nodeId, int edgeIndex), qui retourne
    // l'identité de la edgeIndex-ième arête sortant du nœud
    // d'identité nodeId.
    public int edgeId(int nodeId, int edgeIndex) {
        int attribut3 = buffer.get(nodeId * 3 + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(attribut3, 0, 28) + edgeIndex;
    }
}
