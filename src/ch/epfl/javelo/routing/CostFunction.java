package ch.epfl.javelo.routing;

/**
 * Interface représentant une fonction de coût.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 01/04/2022
 */
public interface CostFunction {

    /**
     * Méthode représentant le facteur par lequel la longueur de l'arête d'identité edgeId, partant
     * du nœud d'identité nodeId, doit être multipliée.
     *
     * @param nodeId L'identité du nœud duquel part l'arête d'identité edgeId.
     * @param edgeId L'identité de l'arête en question.
     * @return Le facteur de multiplication de la longueur de l'arête.
     */
    double costFactor(int nodeId, int edgeId);
}
