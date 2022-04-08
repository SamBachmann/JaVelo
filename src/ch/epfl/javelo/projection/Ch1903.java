package ch.epfl.javelo.projection;

/**
 *
 * @author Samuel Bachmann (340373)
 * @author Cyrus Giblain (312042)
 *
 * Le 25/02/2022
 */
public final class Ch1903 {

    // Un constructeur privé pour cette classe non instantiable.
    private Ch1903() {}

    /**
     * Méthode permettant de passer des coordonnées WGS 84 aux coordonnées suisses.
     *
     * @param lon La longitude dans les coordonnées WGS 84.
     * @param lat La latitude dans les coordonnées WGS 84.
     * @return La coordonnée E (est) du point de longitude et latitude données.
     */
    public static double e(double lon, double lat) {

        double lon1 = (Math.pow(10, -4)) * ((3600 * Math.toDegrees(lon)) - 26782.5);
        double lat1 = (Math.pow(10, -4)) * ((3600 * Math.toDegrees(lat)) - 169028.66);

        return 2600072.37 + (211455.93 * lon1) - (10938.51 * lon1 * lat1) - (0.36 * lon1 * lat1 * lat1)
                - (44.54 * lon1 * lon1 * lon1);
    }

    /**
     * Méthode permettant de passer des coordonnées WGS 84 aux coordonnées suisses.
     *
     * @param lon La longitude dans les coordonnées WGS 84.
     * @param lat La latitude dans les coordonnées WGS 84.
     * @return La coordonnée N (nord) du point de longitude et latitude données.
     */
    public static double n(double lon, double lat) {

        double lon1 = (Math.pow(10, -4)) * ((3600 * Math.toDegrees(lon)) - 26782.5);
        double lat1 = (Math.pow(10, -4)) * ((3600 * Math.toDegrees(lat)) - 169028.66);

        return 1200147.07 + (308807.95 * lat1) + (3745.25 * lon1 * lon1) + (76.63 * lat1 * lat1)
                - (194.56 * lon1 * lon1 * lat1) + (119.79 * lat1 * lat1 * lat1);
    }

    /**
     * Méthode permettant de passer des coordonnées suisses aux coordonnées WGS 84.
     *
     * @param e La valeur de l'abscisse dans les coordonnées suisses.
     * @param n La valeur de l'ordonnée dans les coordonnées suisses.
     * @return La longitude (lon) du point donné en coordonnées suisses.
     */
    public static double lon(double e, double n) {

        double x = Math.pow(10, -6) * (e - 2600000);
        double y = Math.pow(10, -6) * (n - 1200000);

        double lon0 = 2.6779094 + (4.728982 * x) + (0.791484 * x * y) + (0.1306 * x * y * y)
                - (0.0436 * x * x * x);

        return Math.toRadians(lon0) * (100.0/36);
    }

    /**
     * Méthode permettant de passer des coordonnées suisses aux coordonnées WGS 84.
     *
     * @param e La valeur de l'abscisse dans les coordonnées suisses.
     * @param n La valeur de l'ordonnée dans les coordonnées suisses.
     * @return La latitude (lat) du point donné en coordonnées suisses.
     */
    public static double lat(double e, double n) {

        double x = Math.pow(10, -6) * (e - 2600000);
        double y = Math.pow(10, -6) * (n - 1200000);

        double lat0 = 16.9023892 + (3.238272 * y) - (0.270978 * x * x) - (0.002528 * y * y)
                - (0.0447 * x * x * y) - (0.0140 * y * y * y);

        return Math.toRadians(lat0) * (100.0/36);
    }
}
