package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * Interface représantant un ittinéraire.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 17/03/2022
 */

public interface Route {
    /**
     * Retourne l'index du segment se trouvant à la position donnée.
     *
     * @param position Position en mètre sur l'itinéraire.
     * @return L'index du segment à la position donnée.
     */
    int indexOfSegmentAt(double position);

    /**
     * Retourne la longueur totale de l'itinéraire, en mètres.
     *
     * @return Longueur totale de l'itinéraire, en mètres.
     */
    double length();

    /**
     * Retourne la totalité des arêtes de l'itinéraire.
     *
     * @return Toutes les arêtes de l'itinéraire, dans une liste.
     */
    List<Edge> edges();

    /**
     * Retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire.
     *
     * @return Tous les points aux extrémités des arêtes de l'itinéraire, dans une liste.
     */
    List<PointCh> points();

    /**
     *
     * @param position
     * @return
     */
    PointCh pointAt(double position);

    double elevationAt(double position);

    int nodeClosestTo(double position);

    RoutePoint pointClosestTo(PointCh point);


}
