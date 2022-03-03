package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 *
 * Enregistrement permettant de représenter un point dans le système Web Mercator
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 01/03/2022
 */
public record PointWebMercator(double x, double y) {

    /**
     *
     * Constructeur vérifiant que les coordonnées e et n sont dans la zone suisse.
     *
     * @param x Valeur de l'abscisse dans les coordonnées Web Mercator.
     * @param y Valeur de l'ordonnée dans les coordonnées Web Mercator.
     */
    public PointWebMercator {
        Preconditions.checkArgument(x >= 0 && x <= 1 && y >= 0 && y <= 1);
    }

    /**
     *
     * Méthode nous permettant de connaître un point dans le système Web Mercator dont les
     * coordonnées sont x et y au niveau de zoom donné.
     *
     * @param zoomLevel Cet argument désigne le zoom qu'on souhaite appliquer à la carte.
     * @param x Cet argument désigne la coordonnée x dans le système Web Mercator.
     * @param y Cet argument désigne la coordonnée y dans le système Web Mercator.
     * @return Le point dont les coordonnées x et y sont zoomées.
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {

        double xAvecZoom = Math.scalb(x, 8 + zoomLevel);
        double yAvecZoom = Math.scalb(y, 8 + zoomLevel);

        return new PointWebMercator(xAvecZoom, yAvecZoom);
    }

    /**
     *
     * Méthode nous retournant un point dans le système Web Mercator à partir d'un point
     * dans le système suisse donné.
     *
     * @param pointCh Cet argument désigne un point dans le système de coordonnées suisse.
     * @return Le point dans le système Web Mercator.
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {

        double pointChLon = pointCh.lon();
        double pointChLat = pointCh.lat();

        double xPointCh = WebMercator.x(pointChLon);
        double yPointCh = WebMercator.y(pointChLat);

        return new PointWebMercator(xPointCh, yPointCh);
    }

    /**
     *
     * Méthode nous donnant la coordonnée x zoomée.
     *
     * @param zoomLevel Cet argument désigne le niveau de zoom à appliquer.
     * @return La coordonnée x zoomée.
     */
    public double xAtZoomLevel(int zoomLevel) {
        return x * zoomLevel;
    }

    /**
     *
     * Méthode nous donnant la coordonnée y zoomée.
     *
     * @param zoomLevel Cet argument désigne le niveau de zoom à appliquer.
     * @return La coordonnée y zoomée.
     */
    public double yAtZoomLevel(int zoomLevel) {
        return y * zoomLevel;
    }

    /**
     *
     * Méthode nous donnant la longitude du point en radians.
     *
     * @return La longitude du point.
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     *
     * Méthode nous donnant la latitude du point en radians.
     *
     * @return La latitude du point.
     */
    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     *
     * Méthode nous donnant le point de coordonnées suisses à partir du point
     * en coordonnées Web Mercator.
     *
     * @return Le point en coordonnées suisses.
     */
    public PointCh toPointCh() {

        double lon = WebMercator.lon(x);
        double lat = WebMercator.lat(y);

        double e = Ch1903.e(lon, lat);
        double n = Ch1903.n(lon, lat);

        if (SwissBounds.containsEN(e, n)) {
            return new PointCh(e, n);
        } else {
            return null;
        }
    }
}
