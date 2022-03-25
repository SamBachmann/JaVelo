package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class SingleRouteTest {
    @Test
    void pointclosestToTest() {

        PointCh fromPoint = new PointCh(2485100,1075300 );
        PointCh toPoint = new PointCh(2485110,1075305);

        PointCh fromPoint1 = new PointCh(2485110,1075305 );
        PointCh toPoint1 = new PointCh(2485120,1075330);

        DoubleUnaryOperator profil = Functions.constant(100);

        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, fromPoint, toPoint, 3, profil))
        edges.add(new Edge(1, 2, fromPoint1, toPoint1, 3, profil));
        SingleRoute singleRoute = new SingleRoute(edges);
    }
}
