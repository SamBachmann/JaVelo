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
        return i * 16;
    }

    /**
     * Converti un nombre représenté en Q28.4 en double.
     * On prend en compte que Q28.4 représente un nombre signé
     *
     * @param q28_4 Nombre représenté en Q28.4
     * @return Le même nombre représenté de manière décimale, en double.
     */
    public static double asDouble(int q28_4){
        int sgn = (q28_4 >>> 31) * -1;
        int unsignedNumber = q28_4 << 1 >>> 1;
        return /*sgn * Math.pow(2, 28)*/ unsignedNumber / 16.0;
    }

    public static float asFloat(int q28_4){
        return q28_4 / 16.f;
    }

}
