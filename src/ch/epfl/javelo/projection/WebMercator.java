package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * Gestionnaire de conversion entre WGS 84 et les coordonnées Web Mercator
 *
 * @author Samuel Bachmann (340373)
 * @author Cyrus Giblain (312042)
 *
 * Le 01/03/2022
 */
public final class WebMercator {

    // Un constructeur privé pour cette classe non instantiable.
    private WebMercator() {}

    /**
     * Méthode nous permettant trouver la coordonnée x dans le système Web Mercator.
     *
     * @param lon Longitude, donnée en radians dans le système WGS 84.
     * @return La coordonnée x dans le système Web Mercator.
     */
    public static double x(double lon) {
       return (lon + Math.PI)/(2 * Math.PI);
    }

    /**
     * Méthode nous permettant trouver la coordonnée y dans le système Web Mercator.
     *
     * @param lat Latitude, donnée en radians dans le système WGS 84.
     * @return La coordonnée y dans le système Web Mercator.
     */
    public static double y(double lat) {
        return (Math.PI - Math2.asinh(Math.tan(lat)))/(2 * Math.PI);
    }

    /**
     * Méthode nous permettant trouver la longitude dans le système WGS 84.
     *
     * @param x La coordonnée x, donnée dans le système Web Mercator.
     * @return La longitude, en radians, dans le système WGS 84.
     */
    public static double lon(double x) {
        return (2 * Math.PI * x) - Math.PI;
    }

    /**
     * Méthode nous permettant trouver la latitude dans le système WGS 84.
     *
     * @param y La coordonnée y, donnée dans le système Web Mercator.
     * @return La latitude, en radians, dans le système WGS 84.
     */
    public static double lat(double y) {
        return Math.atan(Math.sinh(Math.PI - (2 * Math.PI * y)));
    }

}