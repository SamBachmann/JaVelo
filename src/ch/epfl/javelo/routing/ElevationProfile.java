package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

/**
 * Classe représentant le profil en long d'un itinéraire, simple ou multiple.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 15/03/2022
 */

public final class ElevationProfile {
    private final static int SGN_POSITIF = 1;
    private final static int SGN_NEGATIF = -1;

    private final double length;
    private final DoubleUnaryOperator fonctionElevation;
    private final float [] elevationSamples;
    private final double minElevation;
    private final double maxElevation;

    /**
     * Constructeur modélisant un profil en long.
     *
     * @param length La longueur d'un itinéraire, en mètres.
     * @param elevationSamples Le tableau des échantillons d'altitudes, répartis uniformément.
     */
    public ElevationProfile(double length, float[] elevationSamples){
        Preconditions.checkArgument(length > 0 && elevationSamples.length > 1);

        DoubleSummaryStatistics minMax = new DoubleSummaryStatistics();
        for (float element : elevationSamples){
            minMax.accept(element);
        }

        this.minElevation = minMax.getMin();
        this.maxElevation = minMax.getMax();
        this.length = length;
        this.elevationSamples = elevationSamples.clone();
        this.fonctionElevation = Functions.sampled(elevationSamples,length);
    }


    /**
     * Accesseur de length
     *
     * @return La longeur de l'itinéraire, en mètres.
     */
    public double length() {
        return length;
    }

    /**
     * Renvoie l'altitude minimale sur l'itinéraire, déjà calculée dans le constructeur
     *
     * @return L'altitude minimale
     */
    public double minElevation(){
        return minElevation;
    }

    /**
     * Renvoie l'altitude maximale sur l'itinéraire, déjà calculée dans le constructeur
     *
     * @return L'altitude maximale
     */
    public double maxElevation(){
        return maxElevation;
    }

    /**
     * Calcule le dénivelé positif total de l'itinéraire.
     *
     * @return Le dénivelé positif total
     */
    public double totalAscent(){
        return computeAltitudeDifference(SGN_POSITIF);
    }

    /**
     * Calcule le dénivelé négatif total de l'itinéraire.
     *
     * @return Le dénivelé négatif total
     */
    public double totalDescent(){
        return computeAltitudeDifference(SGN_NEGATIF);
    }

    /**
     * Recherche l'altitude de l'itinéraire à un point donné
     *
     * @param position La position sur l'itinéraire, en mètres.
     * @return L'altitude au point position.
     */
    public double elevationAt(double position){

        return fonctionElevation.applyAsDouble(position);
    }

    /**
     * Calcule le dénivelé, positif ou négatif en fonction de sign.
     * Méthode appelée par totalAscent et totalDescent.
     *
     * @param sign Le signe du dénivelé, SGN_POSITIF = 1 si on calcule le dénivelé positf,
     *            SGN_NEGATIF = -1 si on calcule le dénivelé négatif
     * @return Le dénivelé calculé
     */
    private double computeAltitudeDifference(int sign){
        double altitudeDifferenceSum = 0.0;
        for (int i = 1; i < this.elevationSamples.length; ++i){
            float delta = elevationSamples[i] - elevationSamples[i-1];
            if (sign * delta > 0){
                altitudeDifferenceSum += sign * delta;
            }
        }
        return altitudeDifferenceSum;
    }
}
