package ch.epfl.javelo.routing;


import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

/**
 * Classe calculant le profil en long d'un itinéraire donné, à l'aide de sa méthode elevationProfile
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 25/03/2022
 */

public final class ElevationProfileComputer {
    private ElevationProfileComputer(){}

    /**
     * Calcule et retourne le profil en long de l'itinéraire route.
     *
     * @param route L'itinéraire dont on veut connaitre le profil
     * @param maxStepLength L'espacement maximal entre les échantillons du profil.
     * @return Le profil en long de l'itinéraire, sous la forme d'un ElevationProfil
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        Preconditions.checkArgument(maxStepLength > 0);

        float[] elevationTable = elevationTableInitialisation(route, maxStepLength);
        interpolationIntermediaire(elevationTable);

        return new ElevationProfile(route.length(), elevationTable);
    }

    /**
     * Méthode privée appelée dans ElevationProfilComputer.elevationProfile qui initialise
     * le tableau d'altitude et remplace les valeurs NaN de tête et de queue du tableau.
     *
     * @param route L'itinéraire dont on veut connaitre le profil
     * @param maxStepLength L'espacement maximal entre les échantillons du profil
     * @return Un tableau de float avec les valeurs récupérées depuis elevationAt de route.
     */
    private static float[] elevationTableInitialisation(Route route, double maxStepLength){
        int nbSamples = (int) Math.ceil(route.length() / maxStepLength) + 1;
        double espacement = route.length() / (nbSamples - 1);

        float[] elevationTable = new float[nbSamples];

        //initialiser le tableau
        for (int i = 0; i < nbSamples; ++i ){
            double position = espacement * i;
            elevationTable[i] = (float) route.elevationAt(position);
        }

        //Remplacer les valeurs NaN en tête de tableau
        int k= 0;
        while (k < elevationTable.length && Float.isNaN(elevationTable[k])){
            k ++;
        }

        // Cas du Tableau vide
        if (k == elevationTable.length){
            Arrays.fill(elevationTable,0,k, 0f);

        }else if (k > 0 && k < elevationTable.length) {
            Arrays.fill(elevationTable, 0, k, elevationTable[k]);

            //Remplir en queue de tableau.
            int c = elevationTable.length;
            do {
                --c;
            } while (c > 0 && Float.isNaN(elevationTable[c]));

            if (c < elevationTable.length - 1) {
                Arrays.fill(elevationTable, c + 1, elevationTable.length, elevationTable[c]);
            }
        }

        return elevationTable;
    }

    /**
     * Méthode privée appellée dans ElevationProfilComputer.elevationProfile qui rempli les trous
     * intermédiaire dans le profil par interpolation.
     *
     * @param elevationTable Le tableau d'altitude (qui contient encore des trous)
     * @return Le tableau d'altitude après interpolation.
     */
    private static void interpolationIntermediaire(float[] elevationTable){
        boolean rechercheBornesInterpol = false;
        int indexBorneInf = 0;
        int indexBorneSup;
        //On commence à 1 car on sait que la première valeur n'est pas NaN
        for(int i = 1; i < elevationTable.length; ++i){
            //début trou
            if (Float.isNaN(elevationTable[i]) && !rechercheBornesInterpol){
                rechercheBornesInterpol = true;
                indexBorneInf = i -1;
            }
            //fin du trou
            if (Float.isNaN(elevationTable[i-1]) && !Float.isNaN(elevationTable[i]) && rechercheBornesInterpol){
                // interpolation pour rentrer les valeurs manquantes.
                rechercheBornesInterpol = false;
                indexBorneSup = i;
                float y0 = elevationTable[indexBorneInf];
                float y1 = elevationTable[indexBorneSup];

                for (int c = indexBorneInf + 1; c < indexBorneSup; ++c){
                    double indexInterpolation = (c - indexBorneInf) / (double) (indexBorneSup - indexBorneInf);
                    elevationTable[c] = (float) Math2.interpolate(y0,y1, indexInterpolation);
                }
            }
        }
    }

}
