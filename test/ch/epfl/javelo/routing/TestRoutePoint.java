package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRoutePoint {

    PointCh pt1 = new PointCh(10.0 + SwissBounds.MIN_E, 0.0 + SwissBounds.MIN_N);
    PointCh pt2 = new PointCh(10 + SwissBounds.MIN_E,3.0 + SwissBounds.MIN_N);
    PointCh pt3 = new PointCh(8.0 + SwissBounds.MIN_E, 2.625 + SwissBounds.MIN_N);
    PointCh pt4 = new PointCh(5 + SwissBounds.MIN_E,0 + SwissBounds.MIN_N);

    @Test
    void methodeTests(){
        RoutePoint ref1 = new RoutePoint(pt3,3.0, Math2.norm(2.0,2.625));
        RoutePoint ref2 = new RoutePoint(pt4,0.0,5);

        assertEquals(ref1.min(ref2), ref1);


    }
}
