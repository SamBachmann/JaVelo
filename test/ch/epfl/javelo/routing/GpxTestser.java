package ch.epfl.javelo.routing;

// Crée et teste un fichier GPX


import ch.epfl.javelo.data.Graph;

import java.io.IOException;
import java.nio.file.Path;

public class GpxTestser {
    public static void main(String[] args) throws IOException {
        testEpflSauvablin();
        //testLausanneBoncourt();
    }


    private static void testLausanneBoncourt() throws IOException{
        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(2046055, 2694240);
        ElevationProfile ep = ElevationProfileComputer.elevationProfile(r, 3);
        GpxGenerator.writeGpx("Lausanne_Boncour.gpx", r, ep );
        System.out.println("done");
    }

    private static void testEpflSauvablin() throws IOException{
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(159049, 117669);
        ElevationProfile ep = ElevationProfileComputer.elevationProfile(r, 10);
        GpxGenerator.writeGpx("Sauvablin1.gpx", r, ep );
        System.out.println("done");
    }
}
