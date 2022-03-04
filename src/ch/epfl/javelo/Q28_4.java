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
     * Constructeur par défaut privé pour rendre la classe non instanciable.
     */
    private Q28_4(){
    }
    /**
     * Converti un nombre entier donné dans la représentation Q28.4.
     * @param i Le nombre entier int donné.
     * @return le nombre i représenté selon Q28.4
     */
    public static int ofInt(int i){
        return i * 16;
    }

    /**
     * Converti un nombre représenté en Q28.4, de type int, en double.
     *
     * @param q28_4 Nombre représenté en Q28.4
     * @return Le même nombre représenté de manière décimale, de type double.
     */
    public static double asDouble(int q28_4){
        return Math.scalb(q28_4, -4);
    }

    /**
     * Converti un nombre représenté en Q28.4, de type int, en float.
     *
     * @param q28_4 Nombre représenté en Q28.4
     * @return Le même nombre représenté de manière décimale, de type float.
     */
    public static float asFloat(int q28_4){
        return Math.scalb(q28_4, -4);
    }

}
