package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Classe représentant un itinéraire multiple.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 01/04/2022
 */
public class MultiRoute implements Route{

    final List<Route> segments;

    public MultiRoute(List<Route> segments) {

        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
    }

    /**
     * Retourne l'index du segment se trouvant à la position donnée.
     *
     * @param position Position en mètre sur l'itinéraire.
     * @return L'index du segment à la position donnée.
     */
    @Override
    public int indexOfSegmentAt(double position) {

        int index = 0;
        double longueur = 0.0;
        double ancienneLongueur;
        for (Route segment : this.segments) {
            ancienneLongueur = longueur;
            longueur += segment.length();
            if (position > longueur){
                index += segment.indexOfSegmentAt(longueur);
            }
            else if (position <= longueur){
                return index + segment.indexOfSegmentAt(position - ancienneLongueur);
            }
            ++ index;
            
        }
        return index - 1;
    }
    /**
     * Retourne la longueur totale de l'itinéraire, en mètres.
     *
     * @return Longueur totale de l'itinéraire, en mètres.
     */
    @Override
    public double length() {

        double longueur = 0.0;

        for (Route segment : this.segments) {
            longueur += segment.length();
        }
        return longueur;
    }

    /**
     * Retourne la totalité des arêtes de l'itinéraire.
     *
     * @return Toutes les arêtes de l'itinéraire, dans une liste.
     */
    @Override
    public List<Edge> edges() {

        List<Edge> listOfEdges = new ArrayList<>();

        for (Route segment : this.segments) {
            listOfEdges.addAll(segment.edges());
        }
        return List.copyOf(listOfEdges);
    }

    /**
     * Retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire.
     *
     * @return Tous les points aux extrémités des arêtes de l'itinéraire, dans une liste.
     */
    @Override
    public List<PointCh> points() {

        List<PointCh> listOfPoints = new ArrayList<>();

        for (int i = 0; i < this.segments.size(); ++i) {
            listOfPoints.addAll(this.segments.get(i).points());
            if (i < this.segments.size() - 1) {
                listOfPoints.remove(listOfPoints.size() - 1);
            }
        }
        return List.copyOf(listOfPoints);
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
        Route segment = this.segments.get(index);

        double distance = 0.0;
        for (int i = 0; i < index; ++i) {
            distance = distance + this.segments.get(i).length();
        }
        return segment.pointAt(position - distance);
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
        Route segment = this.segments.get(index);

        double distance = 0.0;
        for (int i = 0; i < index; ++i) {
            distance = distance + this.segments.get(i).length();
        }
        return segment.elevationAt(position - distance);
    }

    /**
     * Retourne l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée.
     *
     * @param position Position du noeud dont on veut connaitre l'identité
     * @return L'identié de ce noeud.
     */
    @Override
    public int nodeClosestTo(double position) {

        int index = this.indexOfSegmentAt(position);
        Route segment = this.segments.get(index);

        double distance = 0.0;
        for (int i = 0; i < index; ++i) {
            distance = distance + this.segments.get(i).length();
        }
        return segment.nodeClosestTo(position - distance);
    }

    /**
     * Retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné.
     *
     * @param point PointCh de référence
     * @return Le RoutePoint le plus proche du point donné en paramètre.
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double distance = Integer.MAX_VALUE;
        RoutePoint routePoint;
        RoutePoint routePoint1 = new RoutePoint(null, 0.0, 0.0);
        for (Route segment : this.segments) {
            routePoint = segment.pointClosestTo(point);
            if (routePoint.distanceToReference() < distance) {
                routePoint1 = routePoint;
                distance = routePoint.distanceToReference();
            }
        }
        return routePoint1;
    }
}
