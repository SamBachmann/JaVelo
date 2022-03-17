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
     * Retourne une fonction obtenue par interpolation linéaire entre les échantillons samples.
     *
     * @param samples Tableau contenant les échantillons de valeur y. Ceux-ci sont espacés régulièrement entre 0 et xMax.
     * @param xMax La valeur en x maximale pour le dernier échantillon
     * @return La fonction d'interpolation linéaire entre les points donnés.
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        Preconditions.checkArgument(samples.length >= 2 && xMax >= 0);
        return new Sampled(samples, xMax);
    }

    /**
     * Classe privée permettant de modéliser une fonction constante.
     * @constante est la méthode qui instancie une de ces fonctions.
     */
    private static final class Constant implements DoubleUnaryOperator {
        private final double y;

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
        private final float[] samples;
        private final double xMax;
        private final double coefficent;


        private Sampled(float[] samples, double xMax){
            this.samples = samples;
            this.xMax = xMax;
            this.coefficent = (samples.length - 1) / xMax;
        }

        /**
         * Calcule l'interpolation linéaire entre les valeurs y contenues dans samples.
         *
         * @param x Une valeur d'abcisse, comprise entre 0 et xMax
         * @return La fonction
         */
        @Override
        public double applyAsDouble(double x) {
            if (x <= 0 ){
                return this.samples[0];
            }else if (x >= xMax){
                return this.samples[samples.length -1];
            }

            double xn = Math2.clamp(0.0, x, xMax);
            int bInf = (int) Math.floor(coefficent * xn);
            int bSup = (int) Math.ceil(coefficent * xn);
            double xInNewInterval = Math.fma(coefficent,xn, - bInf);

            return Math2.interpolate(this.samples[bInf], this.samples[bSup],xInNewInterval);
        }
    }
}