package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * Classe permettant de représenter des fonctions mathématiques de nombres réels.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 03/03/2022
 */

public final class Functions {
    /**
     * Constructeur par défaut privé pour rendre la classe non instanciable.
     */
    private Functions(){}

    /**
     * Méthode retournant une fonction constante de valeur y.
     *
     * @param y La valeur de la constante.
     * @return Une fonction de type DoubleUnaryOperator constante.
     */
    public static DoubleUnaryOperator constant(double y){
        return new Constant(y);
    }

    /**
     *
     *
     * @param samples Tableau contenant les échantillons de valeur y. Ceux-ci sont espacés régulièrement entre 0 et xMax.
     * @param xMax La valeur en x maximale pour le dernier échantillon
     * @return La fonction d'interpolation linéaire entre les points donnés.
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        Preconditions.checkArgument(samples.length >= 2 && xMax >= 0);
        return new Sampled( samples,xMax);
    }

    /**
     * Classe privée permettant de modéliser une fonction constante.
     * @constante est la méthode qui instancie une de ces fonctions.
     */
    private static final class Constant implements DoubleUnaryOperator {
        private double y;

        private Constant(double y){
            this.y = y;
        }

        /**
         * Applique la fonction pour un x donné
         * @param x La valeur x pour laquelle on calcul y.
         * @return La valeur y de la fonction
         */
        @Override
        public double applyAsDouble(double x) {
            return this.y;
        }
    }


    private static final class Sampled implements DoubleUnaryOperator {
        private float[] samples;
        private double xMax;
        private double espacement;

        private Sampled(float[] samples, double xMax){
            this.samples = samples;
            this.xMax = xMax;
            this.espacement = xMax / samples.length;
        }

        @Override
        public double applyAsDouble(double operand) {
            return 0;
        }
    }
}