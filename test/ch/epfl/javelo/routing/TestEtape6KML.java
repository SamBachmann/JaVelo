package ch.epfl.javelo.routing;

import ch.epfl.javelo.KmlPrinter;
import ch.epfl.javelo.data.Graph;

import java.io.IOException;
import java.nio.file.Path;

public final class TestEtape6KML {

        public static void main(String[] args) throws IOException{
            //testTimeLausanneBoncourt
            testEPFLSauvablin();
        }

        private static void testTimeLausanneBoncourt () throws IOException {
            Graph g = Graph.loadFrom(Path.of("ch_west"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);
            Route r = null;
            for (int i = 0; i < 5; ++i) {
                long t0 = System.nanoTime();
                r = rc.bestRouteBetween(2046055, 2694240);
                System.out.printf(i + " : Itinéraire calculé en %d ms\n",
                        (System.nanoTime() - t0) / 1_000_000);
            }
            //System.out.println("longueur : " + r.length());
            KmlPrinter.write("C:\\Users\\samue\\Documents\\EPFL\\Cours\\BA2\\PPOO\\JaVelo\\test\\ch\\epfl\\javelo\\routing\\javelo.kml", r);

        }

        private static void testEPFLSauvablin() throws IOException{
            Graph g = Graph.loadFrom(Path.of("lausanne"));
            CostFunction cf = new CityBikeCF(g);
            RouteComputer rc = new RouteComputer(g, cf);
            Route r = rc.bestRouteBetween(159049, 117669);


            //System.out.println("longueur : " + r.length());
            KmlPrinter.write("C:\\Users\\samue\\Documents\\EPFL\\Cours\\BA2\\PPOO\\JaVelo\\test\\EpflSauvablin.kml", r);

        }
}
