package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 *
 * Enregistrement permettant de représenter le tableau de tous les nœuds
 * du graphe JaVelo.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 09/03/2022
 */
public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * Cette méthode nous donne le nombre total de nœuds du graphe JaVelo.
     *
     * @return Le nombre de nœuds.
     */
    public int count() {
        return buffer.capacity() / NODE_INTS;
    }

    /**
     * Cette méthode permet de connaître la coordonnée E du nœud d'index donné.
     *
     * @param nodeId L'index du noeud.
     * @return La coordonnée E voulue.
     */
    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS));
    }

    /**
     * Cette méthode permet de connaître la coordonnée N du nœud d'index donné.
     *
     * @param nodeId L'index du noeud.
     * @return La coordonnée N voulue.
     */
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get((nodeId * NODE_INTS) + OFFSET_N));
    }

    /**
     * Cette méthode nous permet de connaître le nombre d'arêtes sortant
     * d'un nœud donné.
     *
     * @param nodeId L'indice du nœud voulu.
     * @return Le nombre d'arêtes sortant du nœud d'identité donnée.
     */
    public int outDegree(int nodeId) {
        int attribut3 = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(attribut3,28, 4);
    }

    /**
     * Cette méthode permet de connaître l'identité de l'arête d'indice donné
     * sortant du nœud d'identité donné.
     *
     * @param nodeId L'identité du nœud en question.
     * @param edgeIndex L'arête sortante concernée.
     * @return L'identité de l'arête concernée.
     */
    public int edgeId(int nodeId, int edgeIndex) {
        int attribut3 = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(attribut3, 0, 28) + edgeIndex;
    }
}
