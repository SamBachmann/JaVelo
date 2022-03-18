package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

/**
 * Enregistrement représentant le point d'un itinéraire le plus proche d'un point de référence donné.
 *
 *  @authors Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 17/03/2022
 *
 * @param point Le point sur l'itinéraire (un PointCh).
 * @param position Position du point le long de l'itinéraire, donnée en mètres.
 * @param distanceToReference La distance entre le point et la référence, en mètres.
 */
public record RoutePoint(PointCh point,double position, double distanceToReference) {
    /**
     * Constante statique représentant un point inexistant.
     */
    public final static RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);

    /**
     * Décale le RoutePoint sur l'itinéraire, vers l'avant ou l'arrière
     *
     * @param positionDifference La différence de position par rapport au point de base, peut être positif ou négatif.
     * @return Un nouveau RoutePoint décalé.
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
        return new RoutePoint(this.point,this.position + positionDifference, this.distanceToReference);
    }

    /**
     * Calcule et retourne le point le plus proche du point de référence entre le point en question
     * et un point passé en paramètre.
     *
     * @param that Un autre RoutePoint dont on veut comparer la distance avec la référence avec celui-ci.
     * @return Le point le plus proche de la référence (Entre this et that)
     */
    public RoutePoint min(RoutePoint that){
        if (this.distanceToReference <= that.distanceToReference()){
            return this;
        }
        return that;
    }

    /**
     * Calcule et retourne le point le plus proche du point de référence entre le point en question
     * et un point that dont les attributs sont donnés en paramètres.
     *
     * @param thatPoint Point sur l'itinéraire.
     * @param thatPosition Position du point le long de l'itinéraire, donnée en mètres.
     * @param thatDistanceToReference Distance entre le point et la référence, en mètres.
     * @return Le point le plus proche de la référence (entre this et that)
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        if (this.distanceToReference > thatDistanceToReference){
            return new RoutePoint(thatPoint,thatPosition,thatDistanceToReference);
        }
        return this;
    }
}
