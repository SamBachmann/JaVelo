package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe représentant un itinéraire simple, implémente Route
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 25/03/2022
 */
public final class SingleRoute implements Route{

    private List<Edge> edges;
    double[] positionTable;

    /**
     * Construit l'itinéraire en long à partir d'une liste d'arêtes.
     *
     * @param edges La liste d'arête du segment.
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges);


        this.positionTable = new double[edges().size()];
        double distance = 0.0;
        for (int i = 0; i < edges().size(); ++i) {
            positionTable[i] = distance;
            distance = distance + edges().get(i).length();
        }
    }

    /**
     * Retourne l'index du segment se trouvant à la position donnée.
     *
     * @param position Position en mètre sur l'itinéraire.
     * @return L'index du segment à la position donnée.
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * Retourne la longueur totale de l'itinéraire, en mètres.
     *
     * @return Longueur totale de l'itinéraire, en mètres.
     */
    @Override
    public double length() {
        double taille =  0.0;
        for (Edge edge : edges) {
            taille = taille + edge.length();
        }
        return taille;
    }

    /**
     * Retourne la totalité des arêtes de l'itinéraire.
     *
     * @return Toutes les arêtes de l'itinéraire, dans une liste.
     */
    @Override
    public List<Edge> edges() {
        return this.edges;
    }

    /**
     * Retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire.
     *
     * @return Tous les points aux extrémités des arêtes de l'itinéraire, dans une liste.
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> listeDesPoints = new ArrayList<>();
        for (Edge edge : edges()) {
            listeDesPoints.add(edge.fromPoint());
            listeDesPoints.add(edge.toPoint());
        }
        return listeDesPoints;
    }

    /**
     * Méthode permettant de faire un binary search.
     *
     * @param position Position d'un point sur l'itinéraire.
     * @return Un int représentant le résultat du binary search.
     */
    private int applyBinarySearch(double position) {

        int resultatBinarySearch = Arrays.binarySearch(this.positionTable, position);

        int edgeIndex = resultatBinarySearch;
        if (resultatBinarySearch < 0) {
            edgeIndex = -resultatBinarySearch - 2;
        }
        return edgeIndex;
    }

    /**
     * Retourne le point se trouvant à la position donnée sur le long de l'itinéraire.
     *
     * @param position Position d'un point sur l'itinéraire.
     * @return Un PointCh à la position donnée sur l'itinéraire.
     */
    @Override
    public PointCh pointAt(double position) {
    int index = applyBinarySearch(position);
        return this.edges.get(index)
                .pointAt(position - this.positionTable[index]);
    }

    /**
     * Renvoie l'altitude à la position donnée le long de l'itinéraire.
     *
     * @param position Position sur l'itinéraire dont on veut l'altitude
     * @return L'altitude à la position donnée, sur l'itinéraire.
     */
    @Override
    public double elevationAt(double position) {
        int index = applyBinarySearch(position);
        return this.edges.get(index)
                .elevationAt(position - this.positionTable[index]);
    }

    /**
     * Retourne l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée.
     *
     * @param position Position du nœud dont on veut connaitre l'identité
     * @return L'identité de ce nœud.
     */
    @Override
    public int nodeClosestTo(double position) {
        PointCh pointCh = this.pointAt(position);
        Edge edge = this.edges.get(applyBinarySearch(position));
        if (pointCh.distanceTo(edge.fromPoint()) <= pointCh.distanceTo(edge.toPoint())) {
            return edge.fromNodeId();
        } else {
            return edge.toNodeId();
        }
    }

    /**
     * Retourne le point de l'itinéraire se trouvant le plus proche du point de référence donnée.
     *
     * @param point PointCh de référence
     * @return Le RoutePoint le plus proche du point donné en paramètre.
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double distance = Double.MAX_VALUE;
        double position2 = 0.0;
        PointCh pointClosestActual;
        PointCh pointClosest = this.edges().get(0).pointAt(0);
        for (Edge edge : edges()) {
            position2 = edge.positionClosestTo(point);
            pointClosestActual = edge.pointAt(position2);
            double distance2 = point.distanceTo(pointClosestActual);
            if (distance2 < distance) {
                distance = distance2;
                pointClosest = pointClosestActual;
            }
        }
        return new RoutePoint(pointClosest, position2, distance);
    }
}
