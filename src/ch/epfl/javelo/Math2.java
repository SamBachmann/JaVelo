package ch.epfl.javelo;

/**
 *
 * Collection d'outils mathématiques utiles pour le projet.
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 25/02/2022
 */
public final class Math2 {

    // Un constructeur privé pour cette classe non instantiable.
    private Math2() {}

    /**
     *
     * Retourne la partie entière par excès de la division de x par y.
     *
     * @param x Le numérateur (entier positif).
     * @param y Le dénominateur (un entier positif non-nul).
     * @return Le résultat de la division entière.
     */
    public static int ceilDiv(int x, int y){
        Preconditions.checkArgument((x >= 0) && (y > 0));
        return (x + y - 1) / y ;
    }

    /**
     *
     * Rend la coordonnée y en fonction d'un x donné sur la droite passant par (0,y0) et (1,y1).
     *
     * @param y0 Le point de la droite en x = 0
     * @param y1 Le point de la droite en x = 1
     * @param x Le point d'abscisse en lequel on calcul y
     * @return La coordonnée y
     */
    public static double interpolate(double y0, double y1, double x){
        return Math.fma(y1 - y0, x, y0);
    }

    /**
     *
     * Limite la valeur v à l'intervalle allant de min à max.
     * Si v est en dehors de cet intervalle, on le ramène à la borne la plus proche.
     *
     * @param min Valeur minimale de l'intervalle
     * @param v Valeur à limiter
     * @param max Valeur maximale de l'intervalle
     * @return v si v appartient à l'intervalle, min si v < min, max si v > max
     */
    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(min <= max);
        if (v < min){
            return min;
        }else if (v > max){
            return max;
        }
        return v;
    }

    /**
     *
     * Surcharge de la méthode clamp pour des entrées de type double.
     *
     * @param min Valeur minimale de l'intervalle.
     * @param v Valeur à limiter.
     * @param max Valeur maximale de l'intervalle.
     * @return v si v appartient à l'intervalle, min si v < min, max si v > max.
     */
    public static double clamp(double min, double v, double max){
        Preconditions.checkArgument(min <= max);
        if (v < min){
            return min;
        }else if (v > max){
            return max;
        }
        return v;
    }

    /**
     *
     * Calcul le sinus hyperbolique inverse en fonction d'un paramètre x.
     *
     * @param x La valeur entrée pour le calcul du sinus hyperbolique inverse.
     * @return le sinus hyperbolique inverse en fonction de x.
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1 + x*x));
    }

    /**
     *
     * Calcule le produit scalaire entre 2 vecteurs u et v à 2 composantes.
     *
     * @param uX Composante X de u.
     * @param uY Composante Y de u.
     * @param vX Composante X de v.
     * @param vY Composante Y de v.
     * @return Le produit scalaire entre u et v.
     */
    public static double dotProduct (double uX, double uY, double vX, double vY){
        return Math.fma(uX, vX, uY * vY);
    }

    /**
     *
     * Calcul de la norme au carré d'un vecteur u à 2 composantes.
     *
     * @param uX Composante X de u.
     * @param uY Composante Y de u.
     * @return La norme au carré du vecteur u.
     */
    public static double squaredNorm(double uX, double uY){
        return uX * uX + uY * uY;
    }

    /**
     *
     * Calcul de la norme d'un vecteur u à 2 composantes.
     *
     * @param uX Composante X de u.
     * @param uY Composante Y de u.
     * @return La norme du vecteur u.
     */
    public static double norm(double uX, double uY){
        return Math.sqrt(Math2.squaredNorm(uX, uY));
    }

    /**
     *
     * Calcul de la projection orthogonale du vecteur <strong>AP</strong> sur le vecteur <strong>AB</strong>
     *
     * @param aX Coordonnée X de A.
     * @param aY Coordonnée Y de A.
     * @param bX Coordonnée X de B.
     * @param bY Coordonnée Y de B.
     * @param pX Coordonnée X de P.
     * @param pY Coordonnée Y de P.
     * @return La longueur de la projection.
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        double uX = pX - aX;
        double uY = pY - aY;
        double vX = bX - aX;
        double vY = bY - aY;

        return Math2.dotProduct(uX, uY, vX, vY) / Math2.norm(vX, vY);
    }
}