package ch.epfl.javelo;

/**
 *
 * Extraire une séquence de bit d'un entier de type int
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 25/02/2022
 */
public final class Bits {
    /**
     * Constructeur par défaut privé pour rendre la classe non instanciable.
     */
    private Bits(){}

    /**
     * Extrait d'un entier considéré comme un vecteur de bits la plage de bits d'une longueur et depuis
     * un point de départ donnés en arguments.
     * Retourne une valeur signée en complément à 2
     *
     * @param value Le vecteur de bits dont on veut extraire une plage de bits.
     * @param start La valeur de départ (comprise) de la plage de bits.
     * @param length La longueur de la plage de bits sélectionnés.
     * @return La séquence de bits sélectionnée (signée).
     */
    public static int extractSigned(int value, int start, int length){
        Preconditions.checkArgument(start >= 0 && start < 32
                                    && length > 0 && length <= 32 - start);

        int decalGauche = 32 - length - start;
        int decalDroite = 32 - length;

        return  (value << decalGauche) >> decalDroite;
    }

    /**
     * Extrait d'un entier considéré comme un vecteur de bits la plage de bits d'une longueur et depuis
     * un point de départ donnés en arguments.
     * Retourne une valeur non signée.
     *
     * @param value Le vecteur de bits dont on veut extraire une plage de bits.
     * @param start La valeur de départ (comprise) de la plage de bits.
     * @param length La longueur de la plage de bits sélectionnés.
     * @return La séquence de bits sélectionnée (non-signée)
     */
    public static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument(start >= 0 && start < 32
                                    && length > 0 && length < 32 - start);
        int decalGauche = 32 - length - start;
        int decalDroite = 32 - length;

        return  (value << decalGauche) >>> decalDroite;
    }
}
