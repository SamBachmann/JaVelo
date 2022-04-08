package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Enregistrement représentant l'arête d'un itinéraire
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 18/03/2022
 *
 * @param fromNodeId L'identité du nœud de départ.
 * @param toNodeId L'identité du nœud d'arrivée de l'arête.
 * @param fromPoint Le point de départ de l'arête.
 * @param toPoint Le point d'arrivée de l'arête.
 * @param length La longueur de l'arête, en mètres.
 * @param profile Le profile de l'arête.
 **/
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint,
                   PointCh toPoint, double length, DoubleUnaryOperator profile) {

    /**
     * Méthode permettant d'obtenir une instance de Edge.
     *
     * @param graph Le graphe associé à l'arête.
     * @param edgeId L'identité d'une arête.
     * @param fromNodeId Le nœud de départ de l'arête.
     * @param toNodeId Le nœud d'arrivée de l'arête.
     * @return Une instance de Edge, soit une arête composant un itinéraire.
     **/
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {

        PointCh fromPoint = graph.nodePoint(fromNodeId);
        PointCh toPoint = graph.nodePoint(toNodeId);
        double length = graph.edgeLength(edgeId);
        DoubleUnaryOperator profil = graph.edgeProfile(edgeId);

        return new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, profil);
    }

    /**
     * Méthode permettant d'obtenir le point le plus proche du point en argument sur l'arête.
     *
     * @param point Le point donné.
     * @return Un double représentant la position le long de l'arête, en mètres, qui se trouve
     * la plus proche du point donné.
     */
    public double positionClosestTo(PointCh point) {

        double aX = fromPoint.e();
        double aY = fromPoint.n();
        double bX = toPoint.e();
        double bY = toPoint.n();
        double pX = point.e();
        double pY = point.n();

        return Math2.projectionLength(aX, aY, bX, bY, pX, pY);
    }

    /**
     * Méthode permettant d'obtenir le point se trouvant à la position donnée sur l'arête,
     * exprimée en mètres.
     *
     * @param position La position donnée sur l'arête.
     * @return le point se trouvant à la position donnée sur l'arête, exprimée en mètres.
     */
    public PointCh pointAt(double position) {

        double positionE = fromPoint.e() + (position / this.length) * (toPoint.e() - fromPoint.e());
        double positionN = fromPoint.n() + (position / this.length) * (toPoint.n() - fromPoint.n());

        return new PointCh(positionE, positionN);
    }

    /**
     * Méthode permettant d'obtenir l'altitude, en mètres, à la position donnée sur l'arête.
     *
     * @param position La position donnée sur l'arête.
     * @return Un double représentant l'altitude, en mètres, à la position donnée sur l'arête.
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}
