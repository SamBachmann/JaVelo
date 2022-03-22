package ch.epfl.javelo.routing;


import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;

public final class ElevationProfileComputer {
    private ElevationProfileComputer(){}

    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        Preconditions.checkArgument(maxStepLength > 0);

        int nbSamples = (int) Math.ceil(route.length() / maxStepLength) + 1;
        ArrayList<Double> elevationTable = new ArrayList<>();

        for (int position = 0; position < route.length(); ){}

        return null;
    }
}

