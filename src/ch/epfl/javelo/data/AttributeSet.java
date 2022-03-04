package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

/**
 *
 * Enregistrement permettant de représenter un ensemble d'attributs.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 01/03/2022
 */
public record AttributeSet(long bits) {

    /**
     *
     * Constructeur permettant de représenter un ensemble d'attributs.
     *
     * @param bits Le contenu de l'ensemble au moyen d'un bit par valeur possible.
     */
    public AttributeSet {
        long result = bits >> Attribute.COUNT;
        Preconditions.checkArgument(result == 0);
    }

    /**
     *
     * Construit un nouvel ensemble composé des attributs passés en arguments.
     *
     * @param attributes La liste d'attributs.
     * @return Le nouvel ensemble d'attributs.
     */
    public static AttributeSet of(Attribute... attributes) {
        long newbits = 0;
        for (Attribute attribute : attributes) {
            long mask = 1L << attribute.ordinal();
            newbits = newbits | mask;
        }
        return new AttributeSet(newbits);
    }

    /**
     *
     * Permet de savoir si un attribut est bien présent dans l'ensemble this.
     *
     * @param attribute L'attribut en question.
     * @return Un booléen indiquant si l'attribut est bien contenu dans l'ensemble.
     */
    public boolean contains(Attribute attribute) {
        long mask = 1L << attribute.ordinal();
        return  (bits & mask) == mask;
    }

    /**
     *
     * Permet de savoir si les ensembles d'attributs this et that ont au moins
     * un attribut en commun.
     *
     * @param that Un ensemble qu'on va comparer avec this.
     * @return Un booléen indiquant s'il existe une intersection entre les deux ensembles.
     */
    public boolean intersects(AttributeSet that) {
        long mask = 1L;
        boolean intersection = false;
        for (int i = 0; i < 64; ++i) {
            intersection = (mask & this.bits & that.bits) == mask;
            if (intersection) {
                break;
            }
            mask = mask << 1;
        }
        return intersection;
    }

    /**
     *
     * Redéfinit la méthode toString() afin de mieux représenter un ensemble.
     *
     * @return Un String d'une nouvelle façon.
     */
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",","{","}");
        for (Attribute attribut : Attribute.ALL) {
            if (this.contains(attribut)) {
                j.add(attribut.keyValue());
            }
        }
        return j.toString();
    }
}
