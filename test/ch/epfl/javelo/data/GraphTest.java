package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

public class GraphTest {

    @Test
    void testLoadForm() throws IOException {
        Path basePath = Path.of("lausanne");
        Graph javelo = Graph.loadFrom(basePath);

        //System.out.println(javelo.nodeCount());
        //System.out.println(javelo.nodePoint(javelo.nodeCount() - 1));
       // System.out.println(javelo.nodeOutDegree(0));
        PointCh pointCh = new PointCh(javelo.nodePoint(Math2.ceilDiv(javelo.nodeCount(), 2)).e(),
                javelo.nodePoint(Math2.ceilDiv(javelo.nodeCount(), 2)).n());
        System.out.println(javelo.nodeClosestTo(pointCh,0));



    }
}
