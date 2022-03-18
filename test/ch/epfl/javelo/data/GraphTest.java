package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

public class GraphTest {

    @Test
    void testLoadFrom() throws IOException {
        Path basePath = Path.of("lausanne");
        Graph javelo = Graph.loadFrom(basePath);

        //System.out.println(javelo.nodeCount());
        //System.out.println(javelo.nodePoint(javelo.nodeCount() - 1));
       // System.out.println(javelo.nodeOutDegree(0));
        int id1 = Math2.ceilDiv(javelo.nodeCount(), 2);
        PointCh node = javelo.nodePoint(id1);

        PointCh pointCh = new PointCh(node.e() + 1000,node.n() + 1000);
        System.out.println(javelo.nodeClosestTo(pointCh,1000));



    }
}
