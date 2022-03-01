package ch.epfl.javelo;

/**
 * Outils de conversion entre la représentation Q28.4 et d'autres représentations.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 01/03/2022
 */

public final class Q28_4 {
    /**
     * Converti un nombre entier donné dans la représentation Q28.4.
     * @param i Le nombre entier int donné.
     * @return le nombre i représenté selon Q28.4
     */
    public static int ofInt(int i){
        return i << 4;
    }

    /**
     * Converti un nombre représenté en Q28.4 en double
     * @param q28_4 Nombre représenté en Q28.4
     * @return Le même nombre représenté de manière décimale, en double.
     */
    public static double asDouble(int q28_4){
        return (double) q28_4 / 16.0;
    }

    public static float asFloat(int q28_4){
        return q28_4 / 16.f;
    }

}
