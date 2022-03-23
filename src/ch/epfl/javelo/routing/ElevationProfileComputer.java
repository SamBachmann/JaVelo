package ch.epfl.javelo.routing;


import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;

public final class ElevationProfileComputer {
    private ElevationProfileComputer(){}

    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        Preconditions.checkArgument(maxStepLength > 0);

        int nbSamples = (int) Math.ceil(route.length() / maxStepLength) + 1;
        double espacement = route.length() / (nbSamples - 1);
        float[] elevationTable = new float[nbSamples];

        //initialiser le tableau
        for (int i = 0; i < nbSamples; ++i ){
            double position = espacement * i;
            elevationTable[i] = (float) route.elevationAt(position);
        }

        //Remplacer les valeurs NaN
        //Remplir en tête de tableau
        int k= 0;
        while (k < elevationTable.length && Float.isNaN(elevationTable[k])){
            k ++;
        }

        if (k == elevationTable.length){
            Arrays.fill(elevationTable,0,k, 0f);
        }else if (k > 0 && k < elevationTable.length){
            Arrays.fill(elevationTable,0,k,elevationTable[k]);
            //Remplir en queue de tableau.
            int c = elevationTable.length;
            do {
                --c;
            } while (c > 0 && Float.isNaN(elevationTable[c]));

            if (c < elevationTable.length - 1){
                Arrays.fill(elevationTable,c + 1,elevationTable.length, elevationTable[c]);
            }
        }

        //Interpolation des trous intermédiaires.



        return null;
    }
}

