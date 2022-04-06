package ch.epfl.javelo.routing;

import ch.epfl.javelo.KmlPrinter;
import ch.epfl.javelo.data.Graph;

import java.io.IOException;
import java.nio.file.Path;

public final class TestEtape6KML {

        public static void main(String[] args) throws IOException {
            Graph g = Graph.loadFrom(Path.of("ch_west"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);
            long t0 = System.nanoTime();
            Route r = rc.bestRouteBetween(2046055, 2694240);
            System.out.printf("Itinéraire calculé en %d ms\n",
                    (System.nanoTime() - t0) / 1_000_000);
            System.out.println("longueur : " + r.length());
            KmlPrinter.write("C:\\Users\\samue\\Documents\\EPFL\\Cours\\BA2\\PPOO\\JaVelo\\test\\ch\\epfl\\javelo\\routing\\javelo.kml", r);
        }

}
