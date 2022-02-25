package ch.epfl.javelo.projection;

/**
 *
 * @author Samuel Bachmann (340373)
 * @author Cyrus Giblain (312042)
 *
 * Le 25/02/2022
 */

public final class SwissBounds {

    // Valeurs constantes permettant de délimiter

    public static final double MIN_E = 2485000;
    public static final double MAX_E = 2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;
    public static final double WIDTH = MAX_E - MIN_E;
    public static final double HEIGHT = MAX_N - MIN_N;

    public static boolean containsEN(double e, double n) {

        return e <= MAX_E && e >= MIN_E && n <= MAX_N && n >= MIN_N;
    }

}