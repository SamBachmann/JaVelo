package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 *
 *  Enregistrement permettant de représenter un point dans le système de coordonnée suisse (CH1903+)
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 25/02/2022
 */
public record PointCh(double e, double n) {

    /**
     *
     * Constructeur vérifiant que les coordonnées e et n sont dans la zone suisse.
     *
     * @param e Valeur de l'abscisse dans les coordonnées suisses.
     * @param n Valeur de l'ordonnée dans les coordonnées suisses.
     */
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     *
     * Calcule la distance au carré entre le point en question et le point entré en argument.
     *
     * @param that PointCh duquel on veut calculer la distance au carré.
     * @return La distance au carré entre les 2 PointCh.
     */
    public double squaredDistanceTo(PointCh that){
        double coordX = this.e() - that.e();
        double coordY = this.n() - that.n();
        return Math2.squaredNorm(coordX, coordY);
    }
    /**
     *
     * Calcule la distance entre le point en question et le point entré en argument.
     *
     * @param that PointCh duquel on veut calculer la distance.
     * @return La distance entre les 2 PointCh.
     */
    public double distanceTo(PointCh that){
        return Math.sqrt(this.squaredDistanceTo(that));
    }

    /**
     *
     * Méthode retournant la longitude d'un PointCh.
     *
     * @return La longitude.
     */
    public double lon(){
        return Ch1903.lon(this.e(), this.n());
    }

    /**
     *
     * Méthode retournant la latitude d'un PointCh.
     *
     * @return La latitude.
     */
    public double lat(){
        return Ch1903.lat(this.e(), this.n());
    }
}
