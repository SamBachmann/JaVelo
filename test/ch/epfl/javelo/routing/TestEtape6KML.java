package ch.epfl.javelo.routing;/*
 *	Author:      Samuel Bachmann
 *	Date:
 */

import ch.epfl.javelo.KmlPrinter;
import ch.epfl.javelo.data.Graph;

import java.io.IOException;
import java.nio.file.Path;

public final class TestEtape6KML {

        public static void main(String[] args) throws IOException {
            Graph g = Graph.loadFrom(Path.of("lausanne"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);
            Route r = rc.bestRouteBetween(159049, 117669);
            System.out.println(r.length());
            KmlPrinter.write("C:\\Users\\samue\\Documents\\EPFL\\Cours\\BA2\\PPOO\\JaVelo\\test\\ch\\epfl\\javelo\\routing\\javelo.kml", r);
        }

}
