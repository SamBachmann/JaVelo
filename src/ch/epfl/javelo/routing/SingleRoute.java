package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

public final class SingleRoute implements Route{

    private List<Edge> edges;

    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = edges;
    }

    /**
     * Retourne l'index du segment se trouvant à la position donnée.
     *
     * @param position Position en mètre sur l'itinéraire.
     * @return L'index du segment à la position donnée.
     */
    @Override
    public int indexOfSegmentAt(double position) {
        double distance = 0.0;
        int index = 0;
        for (int i = 0; i < edges().size(); ++i) {
            index = i;
            while (position > distance) {
            distance = distance + edges().get(i).length();
            }
        }
        return index;
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
     * Retourne le point se trouvant à la position donnée sur le long de l'itinéraire.
     *
     * @param position Position d'un point sur l'itinéraire.
     * @return Un PointCh à la position donnée sur l'itinéraire.
     */
    @Override
    public PointCh pointAt(double position) {
        int index = this.indexOfSegmentAt(position);
        double distance = 0.0;
        for (int i =  0; i < index; ++i) {
            distance = distance + this.edges.get(i).length();
        }
        return this.edges.get(index).pointAt(position - distance);
    }

    /**
     * Renvoie l'altitude à la position donnée le long de l'itinéraire.
     *
     * @param position Position sur l'itinéraire dont on veut l'altitude
     * @return L'altitude à la position donnée, sur l'itinéraire.
     */
    @Override
    public double elevationAt(double position) {
        int index = this.indexOfSegmentAt(position);
        double distance = 0.0;
        for (int i =  0; i < index; ++i) {
            distance = distance + this.edges.get(i).length();
        }
        return this.edges.get(index).elevationAt(position - distance);
    }

    /**
     * Retourne l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée.
     *
     * @param position Position du noeud dont on veut connaitre l'identité
     * @return L'identié de ce noeud.
     */
    @Override
    public int nodeClosestTo(double position) {
        for (int i = 0; i < edges().size(); ++i) {
            edges.get(i).
        }
        return 0;
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
        PointCh pointCh = new PointCh(0, 0);
        double position2 = 0.0;
        for (Edge edge : edges()) {
            position2 = edge.positionClosestTo(point);
            double e = edge.toPoint().e() - ((edge.toPoint().e() - edge.fromPoint().e()) * (position2 / edge.length()));
            double n = edge.toPoint().n() - ((edge.toPoint().n() - edge.fromPoint().n()) * (position2 / edge.length()));
            pointCh = new PointCh(e, n);
            double distance2 = Math.abs(Math.sqrt(point.squaredDistanceTo(pointCh)));
            if (distance2 < distance) {
                distance = distance2;
            }
        }
        return new RoutePoint(pointCh, position2, distance);
    }
}
