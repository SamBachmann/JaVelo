package ch.epfl.javelo;

/**
 *
 * @author Samuel Bachmann (340373)
 * @author Cyrus Giblain (312042)
 *
 * Le 25/02/2022
 */
public final class Preconditions {

    // Un constructeur privé pour cette classe non instantiable.
    private Preconditions() {}

    /**
     *
     * Méthode permettant de vérifier l'argument shouldBeTrue.
     *
     * @param shouldBeTrue L'argument qui doit être vrai pour qu'une
     *                     exception ne soit pas levée.
     * @throws IllegalArgumentException Si l'argument est faux.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
