package ch.epfl.javelo.projection;

/**
 * Classe contenant les constantes et méthodes statiques liées aux limites de la zone suisse.
 *
 * @author Samuel Bachmann (340373)
 * @author Cyrus Giblain (312042)
 *
 * 25/02/2022
 */
public final class SwissBounds {

    // Un constructeur privé pour que cette classe soit non-instantiable.
    private SwissBounds() {}

    // Valeurs constantes permettant de délimiter les frontières de la Suisse.

    /**
     * Délimitation ouest de la carte.
     */
    public static final double MIN_E = 2485000;

    /**
     * Délimitation est de la carte.
     */
    public static final double MAX_E = 2834000;

    /**
     * Délimitation sud de la carte.
     */
    public static final double MIN_N = 1075000;

    /**
     * Délimitation nord de la carte.
     */
    public static final double MAX_N = 1296000;

    /**
     * Longueur de la carte en ouest-est
     */
    public static final double WIDTH = MAX_E - MIN_E;

    /**
     * Longueur de la carte en sud-nord.
     */
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Méthode nous permettant de savoir si des coordonnées sont bien dans la zone suisse.
     *
     * @param e Cet argument désigne la coordonnée E dans le système de coordonnées suisse.
     * @param n Cet argument désigne la coordonnée N dans le système de coordonnées suisse.
     * @return Un booléen indiquant si lesdites coordonnées sont bien en Suisse.
     */
    public static boolean containsEN(double e, double n) {

        return e <= MAX_E && e >= MIN_E && n <= MAX_N && n >= MIN_N;
    }
}